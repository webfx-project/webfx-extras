package dev.webfx.extras.visual.controls.grid.skin;

import dev.webfx.extras.cell.renderer.ValueRendererRegistry;
import dev.webfx.extras.panes.LayoutPane;
import dev.webfx.extras.panes.MonoPane;
import dev.webfx.extras.util.control.Controls;
import dev.webfx.extras.visual.VisualColumn;
import dev.webfx.extras.visual.VisualStyle;
import dev.webfx.extras.visual.controls.SelectableVisualResultControlSkinBase;
import dev.webfx.extras.visual.controls.grid.VisualGrid;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.platform.console.Console;
import dev.webfx.platform.useragent.UserAgent;
import dev.webfx.platform.util.collection.Collections;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.List;

/**
 * @author Bruno Salmon
 */
public final class VisualGridSkin extends SelectableVisualResultControlSkinBase<VisualGrid, Pane, Pane> {

    private static final boolean LOG_TIMING = false;

    //private long initialBuildTimeMillis;
    private final GridTableHead gridTableHead = new GridTableHead(this);
    final GridBody gridBody = new GridBody(this);
    private ScrollPane bodyScrollPane;
    private Region body; // = gridBody if fullHeight, bodyScrollPane otherwise
    private double headOffset;
    private final static Pane fakeCell = new Pane();
    private List<Node> fakeCellChildren;
    private final DoubleProperty tableLayoutMinWidthProperty = new SimpleDoubleProperty();
    private final BooleanProperty monoColumnLayoutProperty = new SimpleBooleanProperty(false) {
        @Override
        protected void invalidated() {
            // Initial version where we rebuild the grid when switching between desktop/table and mobile layout
            buildGrid(); // TODO: see if we can rebuild only a part of it
        }
    };

    public VisualGridSkin(VisualGrid visualGrid) {
        super(visualGrid, false);
        visualGrid.getStyleClass().add("grid");
        bindRegionClip(gridBody);
    }

    VisualGrid getVisualGrid() {
        return visualControl;
    }

    public double getTableLayoutMinWidth() {
        tableLayoutMinWidthProperty.setValue(computedTableLayoutMinWidth);
        return tableLayoutMinWidthProperty.get();
    }

    public DoubleProperty tableLayoutMinWidthProperty() {
        return tableLayoutMinWidthProperty;
    }

    public void setMonoColumnLayout(boolean mobileLayout) {
        monoColumnLayoutProperty.set(mobileLayout);
    }

    public void setMonoColumnLayout() {
        setMonoColumnLayout(true);
    }

    public boolean isMonoColumnLayout() {
        return monoColumnLayoutProperty.get();
    }

    public boolean isTableLayout() {
        return !isMonoColumnLayout();
    }

    public void setTableLayout() {
        setMonoColumnLayout(false);
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
                                gridTableHead.relocate(-headOffset, 0);
                            }, bodyScrollPane.hvalueProperty());
                        }
                        bodyScrollPane.setContent(gridBody);
                        body = bodyScrollPane;
                    }
                }
                if (visualControl.isHeaderVisible())
                    getChildren().setAll(gridTableHead, body);
                else
                    getChildren().setAll(body);
            }, visualControl.headerVisibleProperty(), visualControl.fullHeightProperty())
        );
    }

    static void clipRegion(Region region) {
        if (UserAgent.isBrowser()) // Done via web CSS with overflow: hidden
            return;
        Node clip = region.getClip();
        Rectangle outputClip = clip instanceof Rectangle ? (Rectangle) clip : new Rectangle();
        outputClip.setWidth(region.getWidth());
        outputClip.setHeight(region.getHeight());
        region.setClip(outputClip);
    }

    private static void bindRegionClip(Region region) {
        if (UserAgent.isBrowser()) // Done via web CSS with overflow: hidden
            return;
        FXProperties.runOnPropertyChange(layoutBounds -> clipRegion(region), region.layoutBoundsProperty());
    }

    @Override
    protected void startBuildingGrid() {
        //initialBuildTimeMillis = System.currentTimeMillis();
        gridTableHead.startBuildingGrid();
        gridBody.startBuildingGrid();
        builtRowIndex = -1;
    }

    private int builtRowIndex;

    @Override
    protected void buildRowCells(Pane bodyRow, int rowIndex) {
        //initialBuildTimeMillis = System.currentTimeMillis();
        super.buildRowCells(bodyRow, rowIndex);
        builtRowIndex = rowIndex;
        invalidateRowHeight(builtRowIndex);
    }

    int getBuiltRowCount() {
        return builtRowIndex + 1;
    }

    @Override
    protected void endBuildingGrid() {
        gridTableHead.endBuildingGrid();
        gridBody.endBuildingGrid();
        //Console.log("Grid built in " + (System.currentTimeMillis() - initialBuildTimeMillis) + " ms");
    }

    void invalidateColumnWidths() {
        lastTotalWidth = -1; // // this will force recomputation of all column width and row heights
    }

    void invalidateRowHeight(int builtRowIndex) {
        lastTotalWidth = -1; // // this will force recomputation of all column width and row heights
        if (builtRowIndex < lastComputedRowHeightsBuiltRowIndex) {
            lastComputedRowHeightsBuiltRowIndex = builtRowIndex;
        }
    }

    @Override
    protected void setUpGridColumn(int gridColumnIndex, int rsColumnIndex, VisualColumn visualColumn) {
        GridTableColumn headColumn = gridTableHead.getOrCreateHeadColumn(gridColumnIndex);
        if (headColumn != null) { // happens in mono-column mode (as there is no head)
            headColumn.setVisualColumn(visualColumn);
            GridTableColumn bodyColumn = gridBody.getOrCreateBodyColumn(gridColumnIndex);
            bodyColumn.setVisualColumn(visualColumn);
            if (bodyColumn.accumulator == null)
                bodyColumn.setAccumulator(headColumn.getAccumulator());
            super.setUpGridColumn(gridColumnIndex, rsColumnIndex, visualColumn);
        }
    }

    @Override
    protected Pane getOrAddHeadCell(int gridColumnIndex) {
        return gridTableHead.getOrAddHeadCell(gridColumnIndex);
    }

    @Override
    protected Pane createBodyGroupCell(int rowIndex, VisualColumn groupColumn) {
        // Note: rowIndex is not passed because not used, as this method is always called to append a new group row at the tail
        return gridBody.createBodyGroupRowCell(groupColumn);
    }

    @Override
    protected Pane getOrAddBodyRow(int rowIndex) {
        // Note: rowIndex is not passed because not used, as this method is always called to append a new body row at the tail
        return gridBody.createBodyRow();
    }

    @Override
    protected void applyBodyRowStyleAndBackground(Pane bodyRow, int rowIndex) {
        gridBody.applyBodyRowStyleAndBackground(bodyRow, rowIndex);
    }

    @Override
    protected void applyBodyRowStyleAndBackground(Pane bodyRow, int rowIndex, String rowStyle, Paint rowBackground) {
    }

    @Override
    protected Pane createBodyRowCell(Pane bodyRow, int rowIndex, int gridColumnIndex) {
        return gridBody.createBodyRowCell(rowIndex, gridColumnIndex);
    }

    @Override
    protected void setCellContent(Pane cell, Node content, VisualColumn visualColumn) {
        if (isTableLayout()) { // probably desktops
            if (content == null) // If there is no content, we use an empty text instead (each row must have a content)
                content = new Text();
            else
                // If the content to render in the cell is a wrapped label that has been constrained in height to auto wrap
                // (such as "wrappedLabel" or "ellipsisLabel" renderers from ValueRendererRegistry), we actually remove that
                // constraint. We don't want the label to indefinitely grow in height, but want it rather to be truncated
                // (with possible ellipsis) if all the text doesn't fit in the cell.
                ValueRendererRegistry.removePossibleLabelAutoWrap(content);
            getCellChildren(cell).add(content);
        } else { // Mono-column layout - probably mobiles
            if (content != null) {
                VisualStyle style = visualColumn.getStyle();
                if (style != null) {
                    String styleClass = style.getStyleClass();
                    if (styleClass != null)
                        content.getStyleClass().addAll(styleClass.split(" "));
                    String textAlign = style.getTextAlign();
                    if (textAlign != null) {
                        // If an alignment is specified, we wrap the content in a mono pane to consider it, because VBox
                        // alone doesn't support alignment (there is no VBox.setAlignment() method)
                        Pos alignment;
                        switch (textAlign) {
                            case "center": alignment = Pos.CENTER;       break;
                            case "right":  alignment = Pos.CENTER_RIGHT; break;
                            case "left":
                            default:       alignment = Pos.CENTER_LEFT;  break;
                        }
                        MonoPane monoPane = new MonoPane(content);
                        monoPane.setAlignment(alignment);
                        monoPane.setMaxWidth(Double.MAX_VALUE); // Filling the VBox width so the alignment is correctly applied
                        content = monoPane;
                    }
                }
                VBox.setMargin(content, visualControl.getCellMargin());
                cell.getChildren().add(content);
            }
        }
    }

    Pane prepareFakeCell(Pane fakePane) {
        fakeCellChildren = fakePane.getChildren();
        return fakeCell;
    }

    private List<Node> getCellChildren(Pane cell) {
        return cell == fakeCell ? fakeCellChildren : cell.getChildren();
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
        List<GridTableColumn> headColumns = gridTableHead.headColumns;
        List<GridTableColumn> bodyColumns = gridBody.bodyTableColumns;
        int columnCount = headColumns.size();
        Insets cellMargin = visualControl.getCellMargin();
        double hMargin = cellMargin.getLeft() + cellMargin.getRight();
        for (int i = 0; i < columnCount; i++) {
            GridTableColumn headColumn = headColumns.get(i);
            double columnWidth;
            if (headColumn.prefWidth != null)
                columnWidth = headColumn.computePrefPixelWidth(visualControl.getWidth() - leftInset - rightInset);
            else {
                GridTableColumn bodyColumn = bodyColumns.get(i);
                columnWidth = bodyColumn.getOrComputeContentMaxWidth();
            }
            columnWidth = snapSizeX(columnWidth + hMargin);
            width += columnWidth;
        }
        //System.out.println("prefWidth: " + width);
        return width;
    }

    private boolean isHeaderVisible() {
        return isTableLayout() && visualControl.isHeaderVisible();
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        updateColumnWidthsAndRowHeights(width - leftInset - rightInset, false);
        return (isHeaderVisible() ? getHeaderHeight() : 0) + gridBody.computedRowHeightsTotal + topInset + bottomInset;
    }

    @Override
    protected double computeBaselineOffset(double topInset, double rightInset, double bottomInset, double leftInset) {
        return -1;
    }

    double getHeaderHeight() {
        return 48; // temporarily hard coded
    }

    // Layout of the whole skin, i.e., the grid head and body
    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        // Ensuring column widths and row heights are up to date
        updateColumnWidthsAndRowHeights(contentWidth, true);
        // The previous call may have updated responsiveMinWidthProperty, which may have changed the visualGrid skin (if
        // managed by responsive design) in which case this skin has been disposed! If this is the case, we exit
        if (getSkinnable() == null) // indicates it has been disposed
            return; // Not relevant anymore to lay out children. Moreover, exiting prevents a NPE in layoutInArea() !
        // head layout (if visible)
        if (isHeaderVisible()) {
            double headerHeight = getHeaderHeight();
            layoutInArea(gridTableHead, contentX - headOffset, contentY, contentWidth, headerHeight, -1, HPos.LEFT, VPos.TOP);
            contentY += headerHeight;
            contentHeight -= headerHeight;
        }
        // body layout (to occupy the whole remaining area)
        layoutInArea(body, contentX, contentY, contentWidth, contentHeight, -1, HPos.LEFT, VPos.TOP);
    }

    private double lastTotalWidth;
    private int lastComputedRowHeightsBuiltRowIndex;
    double computedTableLayoutMinWidth;

    private void updateColumnWidthsAndRowHeights(double totalWidth, boolean apply) {
        if (lastTotalWidth != totalWidth) {
            lastTotalWidth = totalWidth;
            if (isTableLayout())
                updateColumnWidths(totalWidth);
            lastComputedRowHeightsBuiltRowIndex = -1;
        } else if (computedTableLayoutMinWidth == 0)
            updateColumnWidths(totalWidth);
        if (lastComputedRowHeightsBuiltRowIndex == -1 || lastComputedRowHeightsBuiltRowIndex < gridBody.globalRowsIndexes.size() - 1) {
            computeRowHeights(totalWidth);
        }
        if (apply) {
            gridBody.applyComputedRowHeights();
            tableLayoutMinWidthProperty.setValue(computedTableLayoutMinWidth);
        }
    }

    // Column width computation
    private void updateColumnWidths(double totalWidth) {
        long t0 = System.currentTimeMillis();
        Insets cellMargin = visualControl.getCellMargin();
        double hMargin = cellMargin.getLeft() + cellMargin.getRight();
        List<GridTableColumn> headColumns = gridTableHead.headColumns;
        List<GridTableColumn> bodyColumns = gridBody.bodyTableColumns;
        int columnCount = headColumns.size();
        double computedTotalWidth = 0;
        double shrinkableTotalWidth = 0;
        double growableTotalWidth = 0;
        computedTableLayoutMinWidth = 0;
        for (int i = 0; i < columnCount; i++) {
            GridTableColumn headColumn = headColumns.get(i);
            // Step 1: Initial width computation based on percentage or pixel values
            double computedWidth;
            if (headColumn.prefWidth != null) {
                computedWidth = headColumn.computePrefPixelWidth(totalWidth);
            } else {
                GridTableColumn bodyColumn = bodyColumns.get(i);
                computedWidth = bodyColumn.getOrComputeContentMaxWidth();
            }
            // Step 2: Apply min/max constraints
            if (headColumn.minWidth != null) {
                double minW = headColumn.computeMinPixelWidth(totalWidth);
                computedWidth = Math.max(computedWidth, minW);
                if (headColumn.hShrink)
                    computedTableLayoutMinWidth += minW;
            }
            if (headColumn.maxWidth != null) {
                double maxW = headColumn.computeMaxPixelWidth(totalWidth);
                computedWidth = Math.min(computedWidth, maxW);
            }
            computedWidth = snapSizeX(computedWidth + hMargin);
            headColumn.setComputedWidth(computedWidth);
            if (headColumn.minWidth == null || !headColumn.hShrink)
                computedTableLayoutMinWidth += computedWidth;
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
                GridTableColumn headColumn = headColumns.get(i);
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
                GridTableColumn headColumn = headColumns.get(i);
                if (headColumn.hGrow) {
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
        }

        // Applying the column widths to the body columns
        for (int i = 0; i < columnCount; i++)
            bodyColumns.get(i).setComputedWidth(headColumns.get(i).getComputedWidth());

        if (LOG_TIMING) {
            long t1 = System.currentTimeMillis();
            Console.log("Column widths computed in " + (t1 - t0) + " ms for width = " + totalWidth);
        }
    }

    // Row heights computation
    private void computeRowHeights(double totalWidth) {
        long t0 = System.currentTimeMillis();
        int globalRowCount = gridBody.globalRowsIndexes.size();
        if (globalRowCount == 0) {
            gridBody.computedRowHeightsTotal = 0;
            return;
        }
        int globalRowIndexStart = 0, globalRowIndexEnd = globalRowCount - 1;
        double minRowHeight = visualControl.getMinRowHeight();
        double prefRowHeight = visualControl.getPrefRowHeight();
        double maxRowHeight = visualControl.getMaxRowHeight();
        boolean requiresComputation = minRowHeight == Region.USE_COMPUTED_SIZE || prefRowHeight == Region.USE_COMPUTED_SIZE || maxRowHeight == Region.USE_COMPUTED_SIZE;
        if (!requiresComputation) {
            double rowHeight = finalRowHeight(minRowHeight, prefRowHeight, maxRowHeight, 0);
            Collections.setAll(gridBody.computedRowHeights, java.util.Collections.nCopies(globalRowCount, rowHeight));
            gridBody.computedRowHeightsTotal = rowHeight * globalRowCount;
        } else {
            Insets cellMargin = visualControl.getCellMargin();
            double hMargin = cellMargin.getLeft() + cellMargin.getRight();
            double vMargin = cellMargin.getTop() + cellMargin.getBottom();
            int dataRowCount = getBuiltRowCount(); // in case the table is not yet fully populated
            /*if (lastComputedRowHeightsBuiltRowIndex == -1) {
                gridBody.computedRowHeightsTotal = 0;
            } else {
                globalRowIndexStart = lastComputedRowHeightsBuiltRowIndex + 1;
                globalRowIndexEnd = builtRowIndex;
            }*/
            gridBody.computedRowHeightsTotal = 0;
            int columnCount = gridTableHead.headColumns.size(); // ignored in mono-column mode
            for (int globalRowIndex = globalRowIndexStart; globalRowIndex <= globalRowIndexEnd; globalRowIndex++) {
                computeAndAccumulateRowHeight(globalRowIndex, totalWidth, hMargin, vMargin, columnCount, minRowHeight, prefRowHeight, maxRowHeight, dataRowCount);
            }
        }

        if (LOG_TIMING) {
            long t1 = System.currentTimeMillis();
            Console.log("🟥 Row heights computed in " + (t1 - t0) + " ms from " + globalRowIndexStart + " to " + globalRowIndexEnd);
        }

        lastComputedRowHeightsBuiltRowIndex = globalRowCount - 1;
    }

    private void computeAndAccumulateRowHeight(int globalRowIndex, double totalWidth, double hMargin, double vMargin, int columnCount, double minRowHeight, double prefRowHeight, double maxRowHeight, int dataRowCount) {
        double computedRowHeight = 0;
        int matchingRowIndex = gridBody.globalRowIndexToMatchingRowIndex(globalRowIndex);
        if (GridBody.isMatchingRowIndexGroupRowIndex(matchingRowIndex)) { // group row
            int groupRowIndex = GridBody.matchingRowIndexToGroupRowIndex(matchingRowIndex);
            Node groupRow = gridBody.bodyGroupRows.get(groupRowIndex);
            computedRowHeight = groupRow.prefHeight(totalWidth - hMargin);
        } else { // data row
            int dataRowIndex = GridBody.matchingRowIndexToDataRowIndex(matchingRowIndex);
            if (dataRowIndex >= dataRowCount)
                return;
            if (isMonoColumnLayout()) {
                Pane pane = gridBody.bodyDataRows.get(dataRowIndex);
                computedRowHeight = pane.prefHeight(totalWidth - hMargin) + vMargin;
            } else {
                for (int i = 0; i < columnCount; i++) {
                    GridTableColumn gridTableColumn = gridBody.bodyTableColumns.get(i);
                    Node cellNode = Collections.get(gridTableColumn.getChildren(), dataRowIndex);
                    if (cellNode != null)
                        computedRowHeight = Math.max(computedRowHeight, cellNode.prefHeight(gridTableColumn.getComputedWidth() - hMargin) + vMargin);
                }
            }
        }
        double finalPrefRowHeight = prefRowHeight == Region.USE_COMPUTED_SIZE ? visualControl.snapSizeY(computedRowHeight) : prefRowHeight;
        double rowHeight = finalRowHeight(minRowHeight, finalPrefRowHeight, maxRowHeight, computedRowHeight);
        if (gridBody.computedRowHeights.isEmpty())
            Collections.setAll(gridBody.computedRowHeights, java.util.Collections.nCopies(gridBody.globalRowsIndexes.size(), 0d));
        gridBody.computedRowHeights.set(globalRowIndex, rowHeight);
        gridBody.computedRowHeightsTotal += rowHeight;
    }

    private static double finalRowHeight(double minRowHeight, double finalPrefRowHeight, double maxRowHeight, double computedRowHeight) {
        return LayoutPane.boundedSize(
            minRowHeight == Region.USE_COMPUTED_SIZE ? computedRowHeight : minRowHeight == Region.USE_PREF_SIZE ? finalPrefRowHeight : minRowHeight,
            finalPrefRowHeight,
            maxRowHeight == Region.USE_COMPUTED_SIZE ? computedRowHeight : maxRowHeight == Region.USE_PREF_SIZE ? finalPrefRowHeight : maxRowHeight);
    }

}
