package dev.webfx.extras.visual.controls.grid.skin;

import dev.webfx.extras.panes.MonoPane;
import dev.webfx.extras.visual.SelectionMode;
import dev.webfx.extras.visual.VisualColumn;
import dev.webfx.extras.visual.VisualStyle;
import dev.webfx.extras.visual.controls.grid.VisualGrid;
import dev.webfx.platform.util.collection.Collections;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.List;

/**
 * The GridBody contains the body of the grid i.e., everything under the header, which consists of (from back to front):
 * - data rows (aligned horizontally) with no data inside but used only to display the row borders
 * - in desktop layout: table columns (aligned vertically and clipped) with the data cells of that column inside
 * - in mobile layout: a single mobile column containing all VisualColumn together, all cells of a row being aligned vertically
 * - eventually group rows (aligned horizontally) with a group name, inserted before a new group of data rows
 *
 * @author Bruno Salmon
 */
final class GridBody extends Region {
    // List containing all data rows (index = index of the data in the visual result)
    final List<Pane> bodyDataRows = new ArrayList<>();
    // List of table columns for the desktop responsive layout
    final List<GridTableColumn> bodyTableColumns = new ArrayList<>();
    // List containing all group rows (index = index of the group)
    final List<Pane> bodyGroupRows = new ArrayList<>();
    // List of global row indexes (mixed group/data rows). For each global row, this list contains either a
    // negative index to indicate a group row index or a positive index to indicate a data row index.
    final List<Integer> globalRowsIndexes = new ArrayList<>();
    // Array containing all row heights (index = global index of mixed group/data row)
    final List<Double> computedRowHeights = new ArrayList<>();
    private final VisualGridSkin visualGridSkin;
    double[] appliedDataRowHeights;
    double computedRowHeightsTotal;

    GridBody(VisualGridSkin visualGridSkin) {
        getStyleClass().add("grid-body");
        this.visualGridSkin = visualGridSkin;
    }

    void startBuildingGrid() {
        bodyDataRows.clear();
        bodyTableColumns.clear();
        bodyGroupRows.clear();
        globalRowsIndexes.clear();
        computedRowHeights.clear();
        visualGridSkin.invalidateRowHeight(-1);
    }

    void endBuildingGrid() {
        int globalRowCount = bodyGroupRows.size() + bodyDataRows.size();
        List<Node> rowsAndColumns = new ArrayList<>(globalRowCount + bodyTableColumns.size());
        rowsAndColumns.addAll(bodyDataRows);
        if (visualGridSkin.isMultiColumnLayout())
            rowsAndColumns.addAll(bodyTableColumns);
        rowsAndColumns.addAll(bodyGroupRows);
        getChildren().setAll(rowsAndColumns);
    }

    void applyComputedRowHeights() {
        appliedDataRowHeights = computedRowHeights.stream().mapToDouble(Double::doubleValue).toArray();
    }

    GridTableColumn getOrCreateBodyColumn(int columnIndex) {
        GridTableColumn gridTableColumn;
        if (columnIndex < bodyTableColumns.size())
            gridTableColumn = bodyTableColumns.get(columnIndex);
        else {
            bodyTableColumns.add(gridTableColumn = new GridTableColumn(visualGridSkin, false));
            gridTableColumn.setOnMouseClicked(e -> {
                VisualGrid visualGrid = visualGridSkin.getVisualGrid();
                if (visualGrid.getSelectionMode() != SelectionMode.DISABLED) {
                    int rowIndex = (int) (e.getY() / visualGrid.getPrefRowHeight());
                    Pane row = Collections.get(bodyDataRows, rowIndex);
                    if (row != null)
                        row.getOnMouseClicked().handle(e);
                }
            });
            visualGridSkin.invalidateColumnWidths();
        }
        return gridTableColumn;
    }

    // Note: rowIndex is not passed because not used, as this method is always called to append a new group row at the tail
    Pane createBodyGroupRowCell(VisualColumn groupColumn) {
        MonoPane groupCell = new MonoPane();
        groupCell.getStyleClass().add("grid-group");
        VisualStyle style = groupColumn.getStyle();
        if (style != null) {
            String styleClass = style.getStyleClass();
            if (styleClass != null)
                groupCell.getStyleClass().addAll(styleClass.split("\\s+"));
        }
        globalRowsIndexes.add(-bodyGroupRows.size() - 1);
        bodyGroupRows.add(groupCell);
        return groupCell;
    }

    // Note: rowIndex is not passed because not used, as this method is always called to append a new body row at the tail
    Pane createBodyRow() {
        Pane bodyRow;
        if (visualGridSkin.isMonoColumnLayout()) {
            // In mono column layout, the body row is a VBox that will contain all the data cells of that row
            bodyRow = new VBox();
            VisualGrid visualGrid = visualGridSkin.getVisualGrid();
            //bodyRow.paddingProperty().bind(visualGrid.cellMarginProperty());
            bodyRow.minHeightProperty().bind(visualGrid.minRowHeightProperty());
            bodyRow.maxHeightProperty().bind(visualGrid.maxRowHeightProperty());
        } else {
            // In multicolumn layout, the body row is an empty Pane used only to display the row borders.
            // The data cells will be injected in the GridTableColumn instead
            bodyRow = new Pane();
        }
        bodyRow.getStyleClass().add("grid-row");
        globalRowsIndexes.add(bodyDataRows.size());
        bodyDataRows.add(bodyRow);
        return bodyRow;
    }

    int globalRowIndexToMatchingRowIndex(int globalRowIndex) {
        return globalRowsIndexes.get(globalRowIndex);
    }

    static boolean isMatchingRowIndexGroupRowIndex(int matchingRowIndex) {
        return matchingRowIndex < 0;
    }

    static int matchingRowIndexToGroupRowIndex(int matchingRowIndex) {
        return -matchingRowIndex - 1;
    }

    static int matchingRowIndexToDataRowIndex(int matchingRowIndex) {
        return matchingRowIndex;
    }

    Pane createBodyRowCell(int rowIndex, int gridColumnIndex) {
        if (visualGridSkin.isMonoColumnLayout())
            return bodyDataRows.get(rowIndex);
        return getOrCreateBodyColumn(gridColumnIndex).createBodyRowCell();
    }

    void applyBodyRowStyleAndBackground(Pane bodyRow, int rowIndex) {
        Object[] rowStyleClasses = visualGridSkin.getRowStyleClasses(rowIndex);
        if (rowStyleClasses != null) {
            for (Object rowStyleClass : rowStyleClasses)
                if (rowStyleClass != null)
                    bodyRow.getStyleClass().add(rowStyleClass.toString());
        }
        Paint fill = visualGridSkin.getRowBackground(rowIndex);
        bodyRow.setBackground(fill == null ? null : Background.fill(fill));
    }

    // Layout of the grid body rows and columns
    @Override
    protected void layoutChildren() {
        double width = getWidth();
        double rowY = 0;
        int globalRowCount = globalRowsIndexes.size();
        // TODO: see why appliedRowHeights is sometimes empty() while getBuiltRowCount() > 0
        for (int globalRowIndex = 0; globalRowIndex < globalRowCount; globalRowIndex++) {
            double rowHeight = appliedDataRowHeights[globalRowIndex];
            //double rowHeight = computedRowHeights.get(globalRowIndex);
            if (rowHeight <= 0)
                break;
            resizeRelocateRow(globalRowIndex, rowY, width, rowHeight);
            rowY += rowHeight;
        }
        if (visualGridSkin.isMultiColumnLayout()) {
            double x = 0;
            if (rowY == 0)
                rowY = getRowMaxY(globalRowCount - 1);
            double height = rowY;
            for (GridTableColumn bodyColumn : bodyTableColumns) {
                double columnWidth = bodyColumn.getComputedWidth();
                bodyColumn.resizeRelocate(x, 0, columnWidth, height);
                VisualGridSkin.clipRegion(bodyColumn);
                x += columnWidth;
            }
        }
    }

    void resizeRelocateRow(int globalRowIndex, double rowY, double width, double rowHeight) {
        Pane rowNode = getRow(globalRowIndex);
        rowNode.resizeRelocate(0, rowY, width, rowHeight);
    }

    private Pane getRow(int globalRowIndex) {
        int matchingRowIndex = globalRowIndexToMatchingRowIndex(globalRowIndex);
        Pane rowNode;
        if (isMatchingRowIndexGroupRowIndex(matchingRowIndex)) // group row
            rowNode = bodyGroupRows.get(matchingRowIndexToGroupRowIndex(matchingRowIndex));
        else // data row
            rowNode = bodyDataRows.get(matchingRowIndexToDataRowIndex(matchingRowIndex));
        return rowNode;
    }

    double getRowMaxY(int globalRowIndex) {
        if (globalRowIndex < 0)
            return 0;
        Pane rowNode = getRow(globalRowIndex);
        return rowNode.getLayoutY() + rowNode.getHeight();
    }
}
