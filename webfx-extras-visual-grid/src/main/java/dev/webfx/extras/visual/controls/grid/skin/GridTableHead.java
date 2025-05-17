package dev.webfx.extras.visual.controls.grid.skin;

import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bruno Salmon
 */
final class GridTableHead extends Region {

    private final VisualGridSkin visualGridSkin;
    final List<GridTableColumn> headColumns = new ArrayList<>();

    GridTableHead(VisualGridSkin visualGridSkin) {
        this.visualGridSkin = visualGridSkin;
        getStyleClass().add("grid-head");
    }

    void startBuildingGrid() {
        headColumns.clear();
        visualGridSkin.invalidateColumnWidths();
    }

    void endBuildingGrid() {
        getChildren().setAll(headColumns);
    }

    Pane getOrAddHeadCell(int gridColumnIndex) {
        return getOrCreateHeadColumn(gridColumnIndex).createBodyRowCell();
    }

    GridTableColumn getOrCreateHeadColumn(int columnIndex) {
        if (visualGridSkin.isMonoColumnLayout())
            return null;
        GridTableColumn gridTableColumn;
        if (columnIndex < headColumns.size())
            gridTableColumn = headColumns.get(columnIndex);
        else {
            headColumns.add(gridTableColumn = new GridTableColumn(visualGridSkin, true));
            visualGridSkin.invalidateColumnWidths();
        }
        return gridTableColumn;
    }

    // Layout of the grid head content = head columns
    @Override
    protected void layoutChildren() {
        double x = 0;
        double height = visualGridSkin.getHeaderHeight();
        for (GridTableColumn headColumn : headColumns) {
            double columnWidth = headColumn.getComputedWidth();
            headColumn.resizeRelocate(x, 0, columnWidth, height);
            VisualGridSkin.clipRegion(headColumn);
            x += columnWidth;
        }
    }
}
