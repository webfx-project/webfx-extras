package dev.webfx.extras.visual.controls.grid.skin;

import dev.webfx.extras.visual.ColumnWidthAccumulator;
import dev.webfx.extras.visual.VisualColumn;
import dev.webfx.extras.visual.VisualStyle;
import dev.webfx.extras.visual.controls.grid.VisualGrid;
import dev.webfx.platform.util.Booleans;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

/**
 * A GridColumn is a graphical representation of a VisualColumn that contains all the data cells for that column,
 * and lay them out vertically. The vertical position of a data cell is computed to match its associated row.
 *
 * @author Bruno Salmon
 */
final class GridTableColumn extends Pane {
    private final VisualGridSkin visualGridSkin;
    private final boolean header;
    Double minWidth;
    Double prefWidth;
    Double maxWidth;
    boolean hGrow;
    boolean hShrink;
    private HPos hAlignment = HPos.LEFT;
    private final VPos vAlignment = VPos.CENTER;
    ColumnWidthAccumulator accumulator;
    double computedWidth;

    GridTableColumn(VisualGridSkin visualGridSkin, boolean header) {
        this.visualGridSkin = visualGridSkin;
        this.header = header;
        getStyleClass().add("grid-col");
    }

    Pane createBodyRowCell() {
        return visualGridSkin.prepareFakeCell(this);
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
                    case "left":
                        hAlignment = HPos.LEFT;
                        break;
                    case "center":
                        hAlignment = HPos.CENTER;
                        break;
                    case "right":
                        hAlignment = HPos.RIGHT;
                        break;
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

    // Layout of the cells within that head or body column
    @Override
    protected void layoutChildren() {
        VisualGrid visualGrid = visualGridSkin.getVisualGrid();
        boolean snapToPixel = visualGrid.isSnapToPixel();
        Insets cellMargin = visualGrid.getCellMargin();
        double cellWidth = getWidth();
        double y = 0;
        double headerHeight = header ? getHeight() : 0;
        int globalRowCount = header ? 1 : visualGridSkin.gridBody.appliedDataRowHeights.length;
        int dataRowCount = header ? 1 : Math.min(visualGridSkin.getBuiltRowCount(), getChildren().size());
        for (int globalRowIndex = 0; globalRowIndex < globalRowCount; globalRowIndex++) {
            double rowHeight = header ? headerHeight : visualGridSkin.gridBody.appliedDataRowHeights[globalRowIndex];
            int matchingIndex;
            if (header)
                matchingIndex = 0;
            else {
                matchingIndex = visualGridSkin.gridBody.globalRowsIndexes.get(globalRowIndex);
                if (matchingIndex < 0) { // ignoring group rows
                    y += rowHeight;
                    continue;
                }
                if (matchingIndex >= dataRowCount)
                    break;
            }
            Node child = getChildren().get(matchingIndex);
            // We try to fill the with and height in general, but for HBox we don't fill the width to make
            // hAlignment work (otherwise fillWidth would stretch the HBox, which would apply its own internal
            // alignment instead).
            boolean fillWidth = !(child instanceof HBox);
            layoutInArea(child, 0, y, cellWidth, rowHeight, -1, cellMargin, fillWidth, true, hAlignment, vAlignment, snapToPixel);
            y += rowHeight;
        }
    }
}
