package dev.webfx.extras.visual.controls.grid;

import dev.webfx.extras.responsive.ResponsiveLayout;
import dev.webfx.extras.visual.controls.SelectableVisualResultControlSkinBase;
import javafx.scene.Node;
import javafx.scene.paint.Paint;

/**
 * @author Bruno Salmon
 */
abstract class VisualGridSkinBase<ROW extends Node, CELL extends Node> extends SelectableVisualResultControlSkinBase<VisualGrid, ROW, CELL> implements ResponsiveLayout {

    protected final VisualGrid visualGrid;

    public VisualGridSkinBase(VisualGrid visualGrid) {
        super(visualGrid, false);
        this.visualGrid = visualGrid;
    }

    @Override
    public void applyResponsiveLayout() {
        visualGrid.setSkin(this); // May produce an exception if reused (OpenJFX bug), so better to override
    }

    @Override
    protected void applyBodyRowStyleAndBackground(ROW bodyRow, int rowIndex, String rowStyle, Paint rowBackground) {
    }

}
