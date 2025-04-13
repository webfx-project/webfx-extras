package dev.webfx.extras.visual.controls.grid;

import dev.webfx.extras.cell.renderer.ValueRendererRegistry;
import dev.webfx.extras.util.control.Controls;
import dev.webfx.extras.visual.*;
import dev.webfx.extras.visual.controls.SelectableVisualResultControlSkinBase;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.platform.scheduler.Scheduled;
import dev.webfx.platform.uischeduler.UiScheduler;
import dev.webfx.platform.useragent.UserAgent;
import dev.webfx.platform.util.collection.Collections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Bruno Salmon
 */
final class VisualGridTableSkin extends SelectableVisualResultControlSkinBase<VisualGrid, Pane, Pane> {

    private static final int INITIAL_BUILT_ROWS_MAX = 20;

    private final GridHead gridHead = new GridHead();
    private final GridBody gridBody = new GridBody();
    private ScrollPane bodyScrollPane;
    private Region body; // = gridBody if fullHeight, bodyScrollPane otherwise
    private double headOffset;
    private final static Pane fakeCell = new Pane();
    private List<Node> fakeCellChildren;

    VisualGridTableSkin(VisualGrid visualGrid) {
        super(visualGrid, false);
        visualGrid.getStyleClass().add("grid");
        clipChildren(gridBody);
    }

    @Override
    public void install() {
        super.install();
        VisualGrid visualGrid = getSkinnable();
        unregisterOnDispose(
            FXProperties.runNowAndOnPropertiesChange(() -> {
                if (visualGrid.isFullHeight()) {
                    if (bodyScrollPane != null)
                        bodyScrollPane.setContent(null);
                    body = gridBody;
                } else {
                    if (!(body instanceof ScrollPane)) {
                        if (bodyScrollPane == null) {
                            bodyScrollPane = new ScrollPane();
                            bodyScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                            FXProperties.runOnPropertyChange(() -> {
                                headOffset = Controls.computeScrollPaneHOffset(bodyScrollPane, false);
                                gridHead.relocate(-headOffset, 0);
                            }, bodyScrollPane.hvalueProperty());
                        }
                        bodyScrollPane.setContent(gridBody);
                        body = bodyScrollPane;
                    }
                }
                if (visualGrid.isHeaderVisible())
                    getChildren().setAll(gridHead, body);
                else
                    getChildren().setAll(body);
            }, visualGrid.headerVisibleProperty(), visualGrid.fullHeightProperty())
        );
    }

    private static void clipChildren(Region region) {
        Rectangle outputClip = new Rectangle();
        region.setClip(outputClip);
        FXProperties.runOnPropertyChange(layoutBounds -> {
            outputClip.setWidth(layoutBounds.getWidth());
            outputClip.setHeight(layoutBounds.getHeight());
        }, region.layoutBoundsProperty());
    }

    @Override
    protected void startBuildingGrid() {
        gridHead.startBuildingGrid();
        gridBody.startBuildingGrid();
    }

    private int builtRowIndex = -1;

    @Override
    protected void buildRowCells(Pane bodyRow, int rowIndex) {
        // Skipping rows after INITIAL_BUILT_ROWS_MAX (will be built later in animation frames)
        if (rowIndex <= INITIAL_BUILT_ROWS_MAX) {
            super.buildRowCells(bodyRow, rowIndex);
            builtRowIndex = rowIndex;
        }
    }

    @Override
    protected void endBuildingGrid() {
        if (getRowCount() <= INITIAL_BUILT_ROWS_MAX) {
            gridHead.endBuildingGrid();
            gridBody.endBuildingGrid();
        } else
            UiScheduler.schedulePeriodicInAnimationFrame(new Consumer<>() {
                final VisualResult rs = getRs();
                @Override
                public void accept(Scheduled scheduled) {
                    if (rs != getRs())
                        scheduled.cancel();
                    else {
                        builtRowIndex++;
                        if (builtRowIndex >= getRowCount()) {
                            scheduled.cancel();
                            gridHead.endBuildingGrid();
                            gridBody.endBuildingGrid();
                        } else {
                            VisualGridTableSkin.super.buildRowCells(null, builtRowIndex);
                            lastContentWidth = -1;
                        }
                    }
                }
            });
    }

    @Override
    protected void setUpGridColumn(int gridColumnIndex, int rsColumnIndex, VisualColumn visualColumn) {
        GridColumn headColumn = gridHead.getOrCreateHeadColumn(gridColumnIndex);
        headColumn.setVisualColumn(visualColumn);
        GridColumn bodyColumn = gridBody.getOrCreateBodyColumn(gridColumnIndex);
        bodyColumn.setVisualColumn(visualColumn);
        if (bodyColumn.cumulator == null)
            bodyColumn.setCumulator(headColumn.getCumulator());
        super.setUpGridColumn(gridColumnIndex, rsColumnIndex, visualColumn);
    }

    @Override
    protected Pane getOrAddHeadCell(int gridColumnIndex) {
        return gridHead.getOrAddHeadCell(gridColumnIndex);
    }

    @Override
    protected Pane getOrAddBodyRow(int rowIndex) {
        return gridBody.getOrAddBodyRow(rowIndex);
    }

    @Override
    protected void applyBodyRowStyleAndBackground(Pane bodyRow, int rowIndex) {
        gridBody.applyBodyRowStyleAndBackground(bodyRow, rowIndex);
    }

    @Override
    protected void applyBodyRowStyleAndBackground(Pane bodyRow, int rowIndex, String rowStyle, Paint rowBackground) {
    }

    @Override
    protected Pane getOrAddBodyRowCell(Pane bodyRow, int rowIndex, int gridColumnIndex) {
        return gridBody.getOrAddBodyRowCell(gridColumnIndex);
    }

    @Override
    protected void setCellContent(Pane cell, Node content, VisualColumn visualColumn) {
        List<Node> children = cell == fakeCell ? fakeCellChildren : cell.getChildren();
        if (content == null) // If there is no content, we use an empty text instead (each row must have a content)
            content = new Text();
        else
            // If the content to render in the cell is a wrapped label that has been constrained in height to auto wrap
            // (such as "wrappedLabel" or "ellipsisLabel" renderers from ValueRendererRegistry), we actually remove that
            // constraint because we don't want the label to indefinitely grow in height, but want it rather to be truncated
            // (with possible ellipsis) if all the text doesn't fit in the cell.
            ValueRendererRegistry.removePossibleLabelAutoWrap(content);
        children.add(content);
    }

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return 0;
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return 0;
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        double width = leftInset + rightInset;
        List<GridColumn> headColumns = gridHead.headColumns;
        List<GridColumn> bodyColumns = gridBody.bodyColumns;
        int columnCount = headColumns.size();
        Insets cellMargin = getSkinnable().getCellMargin();
        double hMargin = cellMargin.getLeft() + cellMargin.getRight();
        for (int i = 0; i < columnCount; i++) {
            GridColumn headColumn = headColumns.get(i);
            if (headColumn.fixedWidth != null)
                width += headColumn.fixedWidth;
            else {
                GridColumn bodyColumn = bodyColumns.get(i);
                ColumnWidthCumulator cumulator = bodyColumn.getUpToDateCumulator();
                double columnWidth = snapSizeX(cumulator.getMaxWidth() + hMargin);
                width += columnWidth;
            }
        }
        //System.out.println("prefWidth: " + width);
        return width;
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        VisualGrid visualGrid = getSkinnable();
        return (visualGrid.isHeaderVisible() ? visualGrid.getRowHeight() : 0) + visualGrid.getRowHeight() * getRowCount() + topInset + bottomInset;
    }

    @Override
    protected double computeBaselineOffset(double topInset, double rightInset, double bottomInset, double leftInset) {
        return -1;
    }

    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        updateColumnWidths(contentWidth);
        if (getSkinnable().isHeaderVisible()) {
            double headerHeight = getSkinnable().getRowHeight();
            layoutInArea(gridHead, contentX - headOffset, contentY, columnWidthsTotal, headerHeight, -1, HPos.LEFT, VPos.TOP);
            contentY += headerHeight;
            contentHeight -= headerHeight;
        }
        layoutInArea(body, contentX, contentY, contentWidth, contentHeight, -1, HPos.LEFT, VPos.TOP);
    }

    private double lastContentWidth;
    private double columnWidthsTotal;

    private void updateColumnWidths(double contentWidth) {
        if (lastContentWidth != contentWidth) {
            columnWidthsTotal = contentWidth;
            List<GridColumn> headColumns = gridHead.headColumns;
            int columnCount = headColumns.size();
            int remainingColumnCount = columnCount;
            double currentColumnWidthsTotal = 0;
            // First we set the fixed width columns
            Insets cellMargin = getSkinnable().getCellMargin();
            double hMargin = cellMargin.getLeft() + cellMargin.getRight();
            for (GridColumn headColumn : headColumns) {
                Double fixedWidth = headColumn.fixedWidth;
                if (fixedWidth != null) {
                    if (fixedWidth > 24)
                        fixedWidth *= 1.3;
                    fixedWidth = snapSizeX(fixedWidth + hMargin);
                    headColumn.setColumnWidth(fixedWidth);
                    currentColumnWidthsTotal += fixedWidth;
                    remainingColumnCount--;
                }
            }
            //double resizableColumnWidth = snapSize(remainingColumnWidthsTotal / remainingColumnCount);
            List<GridColumn> bodyColumns = gridBody.bodyColumns;
            if (remainingColumnCount > 0) { // If there are resizable columns
                double currentResizableColumnWidthsTotal = 0;
                // We set the column width from the body column cumulator
                for (int i = 0; i < columnCount; i++) {
                    GridColumn headColumn = headColumns.get(i);
                    if (headColumn.fixedWidth == null) {
                        GridColumn bodyColumn = bodyColumns.get(i);
                        ColumnWidthCumulator cumulator = bodyColumn.getUpToDateCumulator();
                        double columnWidth = snapSizeX(cumulator.getMaxWidth() + hMargin);
                        headColumn.setColumnWidth(columnWidth);
                        currentResizableColumnWidthsTotal += columnWidth;
                    }
                }
                currentColumnWidthsTotal += currentResizableColumnWidthsTotal;
                double remainingColumnWidthsTotal = columnWidthsTotal - currentColumnWidthsTotal;
                // If there is some remaining space, we spread it over the resizable columns, keeping same proportions
                if (remainingColumnWidthsTotal > 0) {
                    for (GridColumn headColumn : headColumns)
                        if (headColumn.fixedWidth == null) {
                            double columnWidth = headColumn.getColumnWidth();
                            columnWidth += columnWidth / currentResizableColumnWidthsTotal * remainingColumnWidthsTotal;
                            headColumn.setColumnWidth(columnWidth);
                        }
                } else if (remainingColumnWidthsTotal < 0) { // if we exceed the content width, we reduce the largest resizable column
                    GridColumn largestColumn = null;
                    for (GridColumn headColumn : headColumns)
                        if (headColumn.fixedWidth == null && (largestColumn == null || largestColumn.getColumnWidth() < headColumn.getColumnWidth()))
                            largestColumn = headColumn;
                    if (largestColumn != null)
                        largestColumn.setColumnWidth(Math.max(0, largestColumn.getColumnWidth() + remainingColumnWidthsTotal));
                }
            }
            // Applying the column widths to the body columns
            for (int i = 0; i < columnCount; i++)
                bodyColumns.get(i).setColumnWidth(headColumns.get(i).getColumnWidth());
            gridBody.setPrefWidth(columnWidthsTotal);
            lastContentWidth = contentWidth;
            // Once the column widths are set, we also need to layout the grid head so that its columns are aligned with the body ones
            gridHead.requestLayout();
        }
    }

    private final class GridHead extends Region {

        private final List<GridColumn> headColumns = new ArrayList<>();

        GridHead() {
            getStyleClass().add("grid-head");
        }

        void startBuildingGrid() {
            headColumns.clear();
        }

        void endBuildingGrid() {
            getChildren().setAll(headColumns);
        }

        Pane getOrAddHeadCell(int gridColumnIndex) {
            return getOrCreateHeadColumn(gridColumnIndex).getOrAddBodyRowCell();
        }

        private GridColumn getOrCreateHeadColumn(int columnIndex) {
            GridColumn gridColumn;
            if (columnIndex < headColumns.size())
                gridColumn = headColumns.get(columnIndex);
            else {
                headColumns.add(gridColumn = new GridColumn());
                lastContentWidth = -1;
            }
            return gridColumn;
        }

        @Override
        protected void layoutChildren() {
            double x = 0;
            double height = getSkinnable().getRowHeight();
            for (GridColumn headColumn : headColumns) {
                double columnWidth = headColumn.getColumnWidth();
                headColumn.resizeRelocate(x, 0, columnWidth, height);
                if (!UserAgent.isBrowser()) // We need to clip the column to avoid overflow (done via CSS in web)
                    headColumn.setClip(new Rectangle(columnWidth, height));
                x += columnWidth;
            }
        }
    }

    private final class GridBody extends Region {
        private final List<Pane> bodyRows = new ArrayList<>();
        private final List<GridColumn> bodyColumns = new ArrayList<>();

        GridBody() {
            getStyleClass().add("grid-body");
        }

        void startBuildingGrid() {
            bodyRows.clear();
            bodyColumns.clear();
        }

        void endBuildingGrid() {
            List<Node> rowsAndColumns = new ArrayList<>(bodyRows.size() + bodyColumns.size());
            rowsAndColumns.addAll(bodyRows);
            rowsAndColumns.addAll(bodyColumns);
            getChildren().setAll(rowsAndColumns);
            //setPrefWidth(getGridColumnCount() * columnWidth);
            setPrefHeight(getRowCount() * getSkinnable().getRowHeight());
        }

        private GridColumn getOrCreateBodyColumn(int columnIndex) {
            GridColumn gridColumn;
            if (columnIndex < bodyColumns.size())
                gridColumn = bodyColumns.get(columnIndex);
            else {
                bodyColumns.add(gridColumn = new GridColumn());
                gridColumn.setOnMouseClicked(e -> {
                    if (getSkinnable().getSelectionMode() != SelectionMode.DISABLED) {
                        int rowIndex = (int) (e.getY() / getSkinnable().getRowHeight());
                        Pane row = Collections.get(bodyRows, rowIndex);
                        if (row != null)
                            row.getOnMouseClicked().handle(e);
                    }
                });
                lastContentWidth = -1;
            }
            return gridColumn;
        }

        Pane getOrAddBodyRow(int rowIndex) {
            Pane bodyRow;
            if (rowIndex < bodyRows.size())
                bodyRow = bodyRows.get(rowIndex);
            else {
                bodyRow = new Pane();
                bodyRow.relocate(0, rowIndex * getSkinnable().getRowHeight());
                //bodyRow.resize(BIG_WIDTH, rowHeight);
                bodyRow.getStyleClass().add("grid-row");
                bodyRows.add(bodyRow);
            }
            return bodyRow;
        }

        Pane getOrAddBodyRowCell(int gridColumnIndex) {
            return getOrCreateBodyColumn(gridColumnIndex).getOrAddBodyRowCell();
        }

        void applyBodyRowStyleAndBackground(Pane bodyRow, int rowIndex) {
            Object[] rowStyleClasses = getRowStyleClasses(rowIndex);
            if (rowStyleClasses != null) {
                for (Object rowStyleClass : rowStyleClasses)
                    if (rowStyleClass != null)
                        bodyRow.getStyleClass().add(rowStyleClass.toString());
            }
            Paint fill = getRowBackground(rowIndex);
            bodyRow.setBackground(fill == null ? null : Background.fill(fill));
        }

        @Override
        protected void layoutChildren() {
            double width = columnWidthsTotal;
            double rowHeight = getSkinnable().getRowHeight();
            for (Pane row : bodyRows)
                row.resize(width, rowHeight);
            double x = 0;
            double height = rowHeight * getRowCount();
            for (GridColumn bodyColumn : bodyColumns) {
                double columnWidth = bodyColumn.getColumnWidth();
                bodyColumn.resizeRelocate(x, 0, columnWidth, height);
                if (!UserAgent.isBrowser()) // We need to clip the column to avoid overflow (done via CSS in web)
                    bodyColumn.setClip(new Rectangle(columnWidth, height));
                x += columnWidth;
            }
        }
    }

    private final class GridColumn extends Pane {
        private Double fixedWidth;
        private ColumnWidthCumulator cumulator;
        private HPos hAlignment = HPos.LEFT;
        private final VPos vAlignment = VPos.CENTER;
        private double columnWidth;

        GridColumn() {
            getStyleClass().add("grid-col");
        }

        void setColumnWidth(double width) {
            columnWidth = width;
        }

        double getColumnWidth() {
            return columnWidth;
        }

        void setVisualColumn(VisualColumn visualColumn) {
            VisualStyle style = visualColumn.getStyle();
            if (style != null) {
                fixedWidth = style.getPrefWidth();
                String textAlign = style.getTextAlign();
                if (textAlign != null) {
                    switch (textAlign) {
                        case "left":   hAlignment = HPos.LEFT;   break;
                        case "center": hAlignment = HPos.CENTER; break;
                        case "right":  hAlignment = HPos.RIGHT;  break;
                    }
                }
            }
            if (fixedWidth == null)
                setCumulator(visualColumn.getCumulator());
        }

        void setCumulator(ColumnWidthCumulator cumulator) {
            this.cumulator = cumulator;
            if (cumulator != null)
                cumulator.registerColumnNodes(getChildren());
        }

        ColumnWidthCumulator getCumulator() {
            if (cumulator == null)
                setCumulator(new ColumnWidthCumulator());
            return cumulator;
        }

        ColumnWidthCumulator getUpToDateCumulator() {
            getCumulator();
            cumulator.update();
            return cumulator;
        }

        Pane getOrAddBodyRowCell() {
            fakeCellChildren = getChildren();
            return fakeCell;
        }

        @Override
        protected void layoutChildren() {
            //System.out.println("Column " + columnIndex + " - layoutChildren() with " + getChildren().size() + " children");
            VisualGrid visualGrid = getSkinnable();
            boolean snapToPixel = visualGrid.isSnapToPixel();
            Insets cellMargin = visualGrid.getCellMargin();
            double rowHeight = visualGrid.getRowHeight();
            double cellWidth = getWidth();
            double y = 0;
            for (Node child : getChildren()) {
                // We try to fill the with and height in general, but for HBox we don't fill the width in order to make
                // hAlignment work (otherwise fillWidth would stretch the HBox which would apply its own internal
                // alignment instead).
                boolean fillWidth = !(child instanceof HBox);
                layoutInArea(child, 0, y, cellWidth, rowHeight, -1, cellMargin, fillWidth, true, hAlignment, vAlignment, snapToPixel);
                y += rowHeight;
            }
        }
    }
}
