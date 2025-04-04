package dev.webfx.extras.visual.controls.grid;

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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Bruno Salmon
 */
public final class VisualGridSkin extends SelectableVisualResultControlSkinBase<VisualGrid, Pane, Pane> {

    private static final double ROW_HEIGHT = 48;
    private static final double HEADER_HEIGHT = ROW_HEIGHT;
    private static final int INITIAL_BUILT_ROWS_MAX = 20;

    private final GridHead gridHead = new GridHead();
    private final GridBody gridBody = new GridBody();
    private ScrollPane bodyScrollPane;
    private Region body;
    private double headOffset;
    private final static Pane fakeCell = new Pane();
    private List<Node> fakeCellChildren;

    VisualGridSkin(VisualGrid visualGrid) {
        super(visualGrid, false);
        visualGrid.getStyleClass().add("grid");
        clipChildren(gridBody);
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
                            // Same code as LayoutUtil.computeScrollPaneHoffset() - but not accessible from here
                            double hmin = bodyScrollPane.getHmin();
                            double hmax = bodyScrollPane.getHmax();
                            double hvalue = bodyScrollPane.getHvalue();
                            double contentWidth = gridBody.getLayoutBounds().getWidth();
                            double viewportWidth = bodyScrollPane.getViewportBounds().getWidth();
                            headOffset = Math.max(0, contentWidth - viewportWidth) * (hvalue - hmin) / (hmax - hmin);
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
        }, visualGrid.headerVisibleProperty(), visualGrid.fullHeightProperty());
        start();
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

    @Override
    protected void buildRowCells(Pane bodyRow, int rowIndex) {
        if (getRowCount() <= INITIAL_BUILT_ROWS_MAX)
            super.buildRowCells(bodyRow, rowIndex);
    }

    @Override
    protected void endBuildingGrid() {
        if (getRowCount() <= INITIAL_BUILT_ROWS_MAX) {
            gridHead.endBuildingGrid();
            gridBody.endBuildingGrid();
        } else
            UiScheduler.schedulePeriodicInAnimationFrame(new Consumer<>() {
                final VisualResult rs = getRs();
                int rowIndex = 0;
                @Override
                public void accept(Scheduled scheduled) {
                    if (rs != getRs())
                        scheduled.cancel();
                    else {
                        if (rowIndex >= getRowCount())
                            scheduled.cancel();
                        else {
                            VisualGridSkin.super.buildRowCells(null, rowIndex);
                        }
                        if (rowIndex == 0) {
                            gridHead.endBuildingGrid();
                            gridBody.endBuildingGrid();
                        }
                        rowIndex++;
                        lastContentWidth = -1;
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
        if (content != null) {
            List<Node> children = cell == fakeCell ? fakeCellChildren : cell.getChildren();
            children.add(content);
        }
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
        for (int i = 0; i < columnCount; i++) {
            GridColumn headColumn = headColumns.get(i);
            if (headColumn.fixedWidth != null)
                width += headColumn.fixedWidth;
            else {
                GridColumn bodyColumn = bodyColumns.get(i);
                ColumnWidthCumulator cumulator = bodyColumn.getUpToDateCumulator();
                double columnWidth = snapSizeX(cumulator.getMaxWidth() + 10); // because of the 5px left and right padding
                width += columnWidth;
            }
        }
        //System.out.println("prefWidth: " + width);
        return width;
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return (getSkinnable().isHeaderVisible() ? HEADER_HEIGHT : 0) + ROW_HEIGHT * getRowCount() + topInset + bottomInset;
    }

    @Override
    protected double computeBaselineOffset(double topInset, double rightInset, double bottomInset, double leftInset) {
        return -1;
    }

    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        updateColumnWidths(contentWidth);
        if (getSkinnable().isHeaderVisible()) {
            layoutInArea(gridHead, contentX - headOffset, contentY, columnWidthsTotal, HEADER_HEIGHT, -1, HPos.LEFT, VPos.TOP);
            contentY += HEADER_HEIGHT;
            contentHeight -= HEADER_HEIGHT;
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
            for (GridColumn headColumn : headColumns) {
                Double fixedWidth = headColumn.fixedWidth;
                if (fixedWidth != null) {
                    if (fixedWidth > 24)
                        fixedWidth *= 1.3;
                    fixedWidth = snapSizeX(fixedWidth + 10); // because of the 5px left and right padding
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
                        double columnWidth = snapSizeX(cumulator.getMaxWidth() + 10); // because of the 5px left and right padding
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
            boolean isOpenJFXScrollPane = body == bodyScrollPane && bodyScrollPane.getSkin() != null;
            double x = 0; // isOpenJFXScrollPane ? 1 : 0;
            double height = ROW_HEIGHT;
            for (GridColumn headColumn : headColumns) {
                double columnWidth = headColumn.getColumnWidth();
                headColumn.resizeRelocate(x, 0, columnWidth, height);
                if (isOpenJFXScrollPane)
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
            setPrefHeight(getRowCount() * ROW_HEIGHT);
        }

        private GridColumn getOrCreateBodyColumn(int columnIndex) {
            GridColumn gridColumn;
            if (columnIndex < bodyColumns.size())
                gridColumn = bodyColumns.get(columnIndex);
            else {
                bodyColumns.add(gridColumn = new GridColumn());
                gridColumn.setOnMouseClicked(e -> {
                    if (getSkinnable().getSelectionMode() != SelectionMode.DISABLED) {
                        int rowIndex = (int) (e.getY() / ROW_HEIGHT);
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
                bodyRow.relocate(0, rowIndex * ROW_HEIGHT);
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
            for (Pane row : bodyRows)
                row.resize(width, ROW_HEIGHT);
            double x = 0;
            double height = ROW_HEIGHT * getRowCount();
            for (GridColumn bodyColumn : bodyColumns) {
                double columnWidth = bodyColumn.getColumnWidth();
                bodyColumn.resizeRelocate(x, 0, columnWidth, height);
                if (!UserAgent.isBrowser()) // We need to clip the column to avoid overflow (done via CSS in web)
                    bodyColumn.setClip(new Rectangle(columnWidth, height));
                x += columnWidth;
            }
        }
    }

    private static final Insets CELL_MARGIN = new Insets(0, 0, 0, 5);

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
            boolean snapToPixel = getSkinnable().isSnapToPixel();
            double cellWidth = getWidth();
            double y = 0;
            for (Node child : getChildren()) {
                // We try to fill the with and height in general, but for HBox we don't fill the width in order to make
                // hAlignment work (otherwise fillWidth would stretch the HBox which would apply its own internal
                // alignement instead).
                boolean fillWidth = !(child instanceof HBox);
                layoutInArea(child, 0, y, cellWidth, ROW_HEIGHT, -1, CELL_MARGIN, fillWidth, true, hAlignment, vAlignment, snapToPixel);
                y += ROW_HEIGHT;
            }
        }
    }
}
