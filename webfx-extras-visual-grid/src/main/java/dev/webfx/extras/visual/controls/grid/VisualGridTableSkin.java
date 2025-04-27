package dev.webfx.extras.visual.controls.grid;

import dev.webfx.extras.cell.renderer.ValueRendererRegistry;
import dev.webfx.extras.panes.LayoutPane;
import dev.webfx.extras.responsive.ResponsiveLayout;
import dev.webfx.extras.util.control.Controls;
import dev.webfx.extras.visual.*;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.platform.console.Console;
import dev.webfx.platform.scheduler.Scheduled;
import dev.webfx.platform.uischeduler.UiScheduler;
import dev.webfx.platform.useragent.UserAgent;
import dev.webfx.platform.util.Booleans;
import dev.webfx.platform.util.collection.Collections;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
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
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Bruno Salmon
 */
final class VisualGridTableSkin extends VisualGridSkinBase<Pane, Pane> implements ResponsiveLayout {

    private static final long INITIAL_BUILD_TIME_MAX_MILLIS = 50;
    private static final long ANIMATION_FRAME_BUILD_TIME_MAX_MILLIS = 10;

    private long initialBuildTimeMillis;
    private final GridHead gridHead = new GridHead();
    private final GridBody gridBody = new GridBody();
    private ScrollPane bodyScrollPane;
    private Region body; // = gridBody if fullHeight, bodyScrollPane otherwise
    private double headOffset;
    private final static Pane fakeCell = new Pane();
    private List<Node> fakeCellChildren;
    private final DoubleProperty responsiveMinWidthProperty = new SimpleDoubleProperty();

    VisualGridTableSkin(VisualGrid visualGrid) {
        super(visualGrid);
        visualGrid.getStyleClass().add("grid");
        clipChildren(gridBody);
    }

    @Override
    public boolean testResponsiveLayoutApplicability(double width) {
        return width >= responsiveMinWidthProperty.get();
    }

    @Override
    public void applyResponsiveLayout() {
        // Because of a bug in OpenJFX, we can't reuse the same skin again, so we need to create a new instance
        if (visualControl.getSkin() != this)
            visualControl.setSkin(new VisualGridTableSkin(visualControl));
    }

    @Override
    public ObservableValue<?>[] getResponsiveTestDependencies() {
        return new ObservableValue[]{responsiveMinWidthProperty};
    }

    @Override
    public void install() {
        super.install();
        unregisterOnDispose(
            FXProperties.runNowAndOnPropertiesChange(() -> {
                if (visualControl.isFullHeight()) {
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
                if (visualControl.isHeaderVisible())
                    getChildren().setAll(gridHead, body);
                else
                    getChildren().setAll(body);
            }, visualControl.headerVisibleProperty(), visualControl.fullHeightProperty())
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
        initialBuildTimeMillis = System.currentTimeMillis();
        gridHead.startBuildingGrid();
        gridBody.startBuildingGrid();
    }

    private int builtRowIndex = -1;

    @Override
    protected void buildRowCells(Pane bodyRow, int rowIndex) {
        // Skipping rows after INITIAL_BUILD_TIME_MAX_MILLIS (remaining rows will be built later in next animation frames)
        if (System.currentTimeMillis() <= initialBuildTimeMillis + INITIAL_BUILD_TIME_MAX_MILLIS) {
            super.buildRowCells(bodyRow, rowIndex);
            builtRowIndex = rowIndex;
        }
    }

    private int getBuiltRowCount() {
        return builtRowIndex + 1;
    }

    private boolean endBuildingGridIfAllRowsAreBuilt() {
        if (getBuiltRowCount() < getRowCount())
            return false;
        gridHead.endBuildingGrid();
        gridBody.endBuildingGrid();
        return true;
    }

    @Override
    protected void endBuildingGrid() {
        if (!endBuildingGridIfAllRowsAreBuilt()) {
            UiScheduler.schedulePeriodicInAnimationFrame(new Consumer<>() {
                final VisualResult rs = getRs(); // Capturing VisualResult to check if it hasn't changed

                @Override
                public void accept(Scheduled scheduled) {
                    if (rs != getRs())
                        scheduled.cancel();
                    else {
                        long animationFrameBuildEndTimeMillis = System.currentTimeMillis() + ANIMATION_FRAME_BUILD_TIME_MAX_MILLIS;
                        while (System.currentTimeMillis() <= animationFrameBuildEndTimeMillis) {
                            if (endBuildingGridIfAllRowsAreBuilt()) {
                                scheduled.cancel();
                                break;
                            } else {
                                builtRowIndex++;
                                VisualGridTableSkin.super.buildRowCells(null, builtRowIndex);
                                lastTotalWidth = -1;
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void setUpGridColumn(int gridColumnIndex, int rsColumnIndex, VisualColumn visualColumn) {
        GridColumn headColumn = gridHead.getOrCreateHeadColumn(gridColumnIndex);
        headColumn.setVisualColumn(visualColumn);
        GridColumn bodyColumn = gridBody.getOrCreateBodyColumn(gridColumnIndex);
        bodyColumn.setVisualColumn(visualColumn);
        if (bodyColumn.accumulator == null)
            bodyColumn.setAccumulator(headColumn.getAccumulator());
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
            // constraint. We don't want the label to indefinitely grow in height, but want it rather to be truncated
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
        Insets cellMargin = visualControl.getCellMargin();
        double hMargin = cellMargin.getLeft() + cellMargin.getRight();
        for (int i = 0; i < columnCount; i++) {
            GridColumn headColumn = headColumns.get(i);
            double columnWidth;
            if (headColumn.prefWidth != null)
                columnWidth = headColumn.computePrefPixelWidth(visualControl.getWidth() - leftInset - rightInset);
            else {
                GridColumn bodyColumn = bodyColumns.get(i);
                columnWidth = bodyColumn.getOrComputeContentMaxWidth();
            }
            columnWidth = snapSizeX(columnWidth + hMargin);
            width += columnWidth;
        }
        //System.out.println("prefWidth: " + width);
        return width;
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        updateColumnWidthsAndRowHeights(width - leftInset - rightInset, false);
        return (visualControl.isHeaderVisible() ? getHeaderHeight() : 0) + gridBody.computedRowHeightsTotal + topInset + bottomInset;
    }

    @Override
    protected double computeBaselineOffset(double topInset, double rightInset, double bottomInset, double leftInset) {
        return -1;
    }

    private double getHeaderHeight() {
        return 48; // temporarily hard coded
    }

    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        // Ensuring column widths and row heights are up to date
        updateColumnWidthsAndRowHeights(contentWidth, true);
        // The previous call may have updated responsiveMinWidthProperty, which may have changed the visualGrid skin (if
        // managed by responsive design) in which case this skin has been disposed! If this is the case, we exit
        if (getSkinnable() == null) // indicates it has been disposed
            return; // Not relevant anymore to lay out children. Moreover, exiting prevents a NPE in layoutInArea() !
        if (visualControl.isHeaderVisible()) {
            double headerHeight = getHeaderHeight();
            layoutInArea(gridHead, contentX - headOffset, contentY, columnWidthsTotal, headerHeight, -1, HPos.LEFT, VPos.TOP);
            contentY += headerHeight;
            contentHeight -= headerHeight;
        }
        layoutInArea(body, contentX, contentY, contentWidth, contentHeight, -1, HPos.LEFT, VPos.TOP);
    }

    private double lastTotalWidth;
    private double columnWidthsTotal;
    double computedResponsiveMinWidth;

    public void updateColumnWidthsAndRowHeights(double totalWidth, boolean apply) {
        if (lastTotalWidth != totalWidth) {
            long t0 = System.currentTimeMillis();
            columnWidthsTotal = totalWidth;
            List<GridColumn> headColumns = gridHead.headColumns;
            List<GridColumn> bodyColumns = gridBody.bodyColumns;
            Insets cellMargin = visualControl.getCellMargin();
            double hMargin = cellMargin.getLeft() + cellMargin.getRight();
            double computedTotalWidth = 0;
            double shrinkableTotalWidth = 0;
            double growableTotalWidth = 0;
            computedResponsiveMinWidth = 0;
            int columnCount = headColumns.size();
            for (int i = 0; i < columnCount; i++) {
                GridColumn headColumn = headColumns.get(i);
                // Step 1: Initial width computation based on percentage or pixel values
                double computedWidth;
                if (headColumn.prefWidth != null) {
                    computedWidth = headColumn.computePrefPixelWidth(totalWidth);
                } else {
                    GridColumn bodyColumn = bodyColumns.get(i);
                    computedWidth = bodyColumn.getOrComputeContentMaxWidth();
                }
                // Step 2: Apply min/max constraints
                if (headColumn.minWidth != null) {
                    double minW = headColumn.computeMinPixelWidth(totalWidth);
                    computedWidth = Math.max(computedWidth, minW);
                    if (headColumn.hShrink)
                        computedResponsiveMinWidth += minW;
                }
                if (headColumn.maxWidth != null) {
                    double maxW = headColumn.computeMaxPixelWidth(totalWidth);
                    computedWidth = Math.min(computedWidth, maxW);
                }
                computedWidth = snapSizeX(computedWidth + hMargin);
                headColumn.setComputedWidth(computedWidth);
                if (headColumn.minWidth == null || !headColumn.hShrink)
                    computedResponsiveMinWidth += computedWidth;
                // Updating totals
                computedTotalWidth += computedWidth;
                if (headColumn.hShrink)
                    shrinkableTotalWidth += computedWidth;
                if (headColumn.hGrow)
                    growableTotalWidth += computedWidth;
            }

            // Step 3: Handle space constraints (shrinking if needed)
            if (computedTotalWidth > totalWidth) {
                double excessWidth = computedTotalWidth - totalWidth;

                // First attempt: shrink only columns that can shrink, proportionally to their width
                for (int i = 0; i < columnCount; i++) {
                    GridColumn headColumn = headColumns.get(i);
                    if (headColumn.hShrink) {
                        double shrinkRatio = headColumn.computedWidth / shrinkableTotalWidth;
                        double reduction = excessWidth * shrinkRatio;
                        headColumn.computedWidth -= reduction;

                        // Ensure we don't shrink below minWidth
                        if (headColumn.minWidth != null) {
                            double minW = headColumn.computeMinPixelWidth(totalWidth);
                            headColumn.computedWidth = Math.max(headColumn.computedWidth, minW);
                        }
                    }
                }
            }

            // Step 4: Or distribute extra space if available
            else if (computedTotalWidth < totalWidth) {
                double extraSpace = totalWidth - computedTotalWidth;

                // Only grow columns that can grow and haven't reached their maxWidth
                for (int i = 0; i < columnCount; i++) {
                    GridColumn headColumn = headColumns.get(i);
                    double growRatio = headColumn.computedWidth / growableTotalWidth;
                    double increase = extraSpace * growRatio;
                    headColumn.computedWidth += increase;

                    // Ensure we don't exceed maxWidth
                    if (headColumn.maxWidth != null) {
                        double maxW = headColumn.computeMaxPixelWidth(totalWidth);
                        headColumn.computedWidth = Math.min(headColumn.computedWidth, maxW);
                    }
                }
            }

            // Applying the column widths to the body columns
            for (int i = 0; i < columnCount; i++)
                bodyColumns.get(i).setComputedWidth(headColumns.get(i).getComputedWidth());
            long t1 = System.currentTimeMillis();

            // Row heights calculation
            int rowCount = getBuiltRowCount(); // Might be less than getRowCount() in case the table is not yet fully populated
            double minRowHeight = visualControl.getMinRowHeight();
            double prefRowHeight = visualControl.getPrefRowHeight();
            double maxRowHeight = visualControl.getMaxRowHeight();
            boolean requiresComputation = minRowHeight == Region.USE_COMPUTED_SIZE || prefRowHeight == Region.USE_COMPUTED_SIZE || maxRowHeight == Region.USE_COMPUTED_SIZE;
            if (!requiresComputation) {
                double rowHeight = finalRowHeight(minRowHeight, prefRowHeight, maxRowHeight, 0);
                Arrays.fill(gridBody.computedRowHeights, rowHeight);
                gridBody.computedRowHeightsTotal = rowHeight * rowCount;
            } else {
                double vMargin = cellMargin.getTop() + cellMargin.getBottom();
                gridBody.computedRowHeightsTotal = 0;
                for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                    double computedRowHeight = 0;
                    for (int i = 0; i < columnCount; i++) {
                        GridColumn gridColumn = bodyColumns.get(i);
                        computedRowHeight = Math.max(computedRowHeight, gridColumn.getChildren().get(rowIndex).prefHeight(gridColumn.getComputedWidth() - hMargin) + vMargin);
                    }
                    double finalPrefRowHeight = prefRowHeight == Region.USE_COMPUTED_SIZE ? visualControl.snapSizeY(computedRowHeight) : prefRowHeight;
                    double rowHeight = finalRowHeight(minRowHeight, finalPrefRowHeight, maxRowHeight, computedRowHeight);
                    gridBody.computedRowHeights[rowIndex] = rowHeight;
                    gridBody.computedRowHeightsTotal += rowHeight;
                }
            }
            long t2 = System.currentTimeMillis();

            Console.log("Column widths computed in " + (t1 - t0) + " ms, row heights computed in " + (t2 - t1) + " ms for width = " + totalWidth);
            lastTotalWidth = totalWidth;
        }
        if (apply) {
            gridBody.applyComputedRowHeights();
            responsiveMinWidthProperty.setValue(computedResponsiveMinWidth);
        }
    }

    private static double finalRowHeight(double minRowHeight, double finalPrefRowHeight, double maxRowHeight, double computedRowHeight) {
        return LayoutPane.boundedSize(
            minRowHeight == Region.USE_COMPUTED_SIZE ? computedRowHeight : minRowHeight == Region.USE_PREF_SIZE ? finalPrefRowHeight : minRowHeight,
            finalPrefRowHeight,
            maxRowHeight == Region.USE_COMPUTED_SIZE ? computedRowHeight : maxRowHeight == Region.USE_PREF_SIZE ? finalPrefRowHeight : maxRowHeight);
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
                headColumns.add(gridColumn = new GridColumn(true));
                lastTotalWidth = -1;
            }
            return gridColumn;
        }

        @Override
        protected void layoutChildren() {
            double x = 0;
            double height = getHeaderHeight();
            for (GridColumn headColumn : headColumns) {
                double columnWidth = headColumn.getComputedWidth();
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
        private double[] computedRowHeights;
        private double[] appliedRowHeights;
        private double computedRowHeightsTotal;

        GridBody() {
            getStyleClass().add("grid-body");
        }

        void startBuildingGrid() {
            bodyRows.clear();
            bodyColumns.clear();
            // We don't wait endBuildingGrid() to create rowHeights, as this would cause NPE in other methods before the
            // table is not fully built.
            computedRowHeights = new double[getRowCount()];
            appliedRowHeights = new double[getRowCount()];
        }

        void endBuildingGrid() {
            List<Node> rowsAndColumns = new ArrayList<>(bodyRows.size() + bodyColumns.size());
            rowsAndColumns.addAll(bodyRows);
            rowsAndColumns.addAll(bodyColumns);
            getChildren().setAll(rowsAndColumns);
        }

        void applyComputedRowHeights() {
            System.arraycopy(computedRowHeights, 0, appliedRowHeights, 0, computedRowHeights.length);
        }

        private GridColumn getOrCreateBodyColumn(int columnIndex) {
            GridColumn gridColumn;
            if (columnIndex < bodyColumns.size())
                gridColumn = bodyColumns.get(columnIndex);
            else {
                bodyColumns.add(gridColumn = new GridColumn(false));
                gridColumn.setOnMouseClicked(e -> {
                    if (visualControl.getSelectionMode() != SelectionMode.DISABLED) {
                        int rowIndex = (int) (e.getY() / visualControl.getPrefRowHeight());
                        Pane row = Collections.get(bodyRows, rowIndex);
                        if (row != null)
                            row.getOnMouseClicked().handle(e);
                    }
                });
                lastTotalWidth = -1;
            }
            return gridColumn;
        }

        Pane getOrAddBodyRow(int rowIndex) {
            Pane bodyRow;
            if (rowIndex < bodyRows.size())
                bodyRow = bodyRows.get(rowIndex);
            else {
                bodyRow = new Pane();
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
            double rowY = 0;
            for (int rowIndex = 0, rowCount = getBuiltRowCount(); rowIndex < rowCount; rowIndex++) {
                double rowHeight = appliedRowHeights[rowIndex];
                bodyRows.get(rowIndex).resizeRelocate(0, rowY, width, rowHeight);
                rowY += rowHeight;
            }
            double x = 0;
            double height = rowY;
            for (GridColumn bodyColumn : bodyColumns) {
                double columnWidth = bodyColumn.getComputedWidth();
                bodyColumn.resizeRelocate(x, 0, columnWidth, height);
                if (!UserAgent.isBrowser()) // We need to clip the column to avoid overflow (done via CSS in web)
                    bodyColumn.setClip(new Rectangle(columnWidth, height));
                x += columnWidth;
            }
        }
    }

    private final class GridColumn extends Pane {
        private final boolean header;
        private Double minWidth;
        private Double prefWidth;
        private Double maxWidth;
        private boolean hGrow;
        private boolean hShrink;
        private ColumnWidthAccumulator accumulator;
        private HPos hAlignment = HPos.LEFT;
        private final VPos vAlignment = VPos.CENTER;
        private double computedWidth;

        GridColumn(boolean header) {
            this.header = header;
            getStyleClass().add("grid-col");
        }

        Pane getOrAddBodyRowCell() {
            fakeCellChildren = getChildren();
            return fakeCell;
        }

        void setVisualColumn(VisualColumn visualColumn) {
            VisualStyle style = visualColumn.getStyle();
            if (style != null) {
                minWidth = style.getMinWidth();
                prefWidth = style.getPrefWidth();
                maxWidth = style.getMaxWidth();
                hGrow = Booleans.isNotFalse(style.getHGrow());
                hShrink = Booleans.isNotFalse(style.getHShrink());
                String textAlign = style.getTextAlign();
                if (textAlign != null) {
                    switch (textAlign) {
                        case "left":   hAlignment = HPos.LEFT;   break;
                        case "center": hAlignment = HPos.CENTER; break;
                        case "right":  hAlignment = HPos.RIGHT;  break;
                    }
                }
                String styleClass = style.getStyleClass();
                if (styleClass != null)
                    getStyleClass().addAll(styleClass.split("\\s+"));
            }
            if (prefWidth == null)
                setAccumulator(visualColumn.getAccumulator());
        }

        void setAccumulator(ColumnWidthAccumulator accumulator) {
            this.accumulator = accumulator;
            if (accumulator != null)
                accumulator.registerColumnNodes(getChildren());
        }

        ColumnWidthAccumulator getAccumulator() {
            if (accumulator == null)
                setAccumulator(new ColumnWidthAccumulator());
            return accumulator;
        }

        ColumnWidthAccumulator getUpToDateAccumulator() {
            getAccumulator();
            accumulator.update();
            return accumulator;
        }

        void setComputedWidth(double width) {
            computedWidth = width;
        }

        double getComputedWidth() {
            return computedWidth;
        }

        double getOrComputeContentMaxWidth() {
            return getUpToDateAccumulator().getMaxWidth();
        }

        double computeMinPixelWidth(double totalWidth) {
            return computeMinPrefMaxPixelWidth(minWidth, totalWidth);
        }

        double computePrefPixelWidth(double totalWidth) {
            return computeMinPrefMaxPixelWidth(prefWidth, totalWidth);
        }

        double computeMaxPixelWidth(double totalWidth) {
            return computeMinPrefMaxPixelWidth(maxWidth, totalWidth);
        }

        private static double computeMinPrefMaxPixelWidth(double minPrefMaxWidth, double totalWidth) {
            return minPrefMaxWidth <= 1 ? minPrefMaxWidth * totalWidth : minPrefMaxWidth;
        }

        @Override
        protected void layoutChildren() {
            boolean snapToPixel = visualControl.isSnapToPixel();
            Insets cellMargin = visualControl.getCellMargin();
            double cellWidth = getWidth();
            double y = 0;
            double headerHeight = header ? getHeight() : 0;
            int rowCount = header ? 1 : getChildren().size(); // should be getBuiltRowCount()
            // TODO: see why getChildren() is sometimes empty() while getBuiltRowCount() > 0
            for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                double rowHeight = header ? headerHeight : gridBody.appliedRowHeights[rowIndex];
                Node child = getChildren().get(rowIndex);
                // We try to fill the with and height in general, but for HBox we don't fill the width to make
                // hAlignment work (otherwise fillWidth would stretch the HBox, which would apply its own internal
                // alignment instead).
                boolean fillWidth = !(child instanceof HBox);
                layoutInArea(child, 0, y, cellWidth, rowHeight, -1, cellMargin, fillWidth, true, hAlignment, vAlignment, snapToPixel);
                y += rowHeight;
            }
        }
    }
}
