package dev.webfx.extras.visual.controls.grid;

import dev.webfx.extras.visual.controls.SelectableVisualResultControlSkinBase;
import javafx.scene.Node;
import javafx.scene.paint.Paint;

/**
 * @author Bruno Salmon
 */
abstract class VisualGridSkinBase<ROW extends Node, CELL extends Node> extends SelectableVisualResultControlSkinBase<VisualGrid, ROW, CELL> {

    public VisualGridSkinBase(VisualGrid control) {
        super(control, false);
    }

    @Override
    protected void applyBodyRowStyleAndBackground(ROW bodyRow, int rowIndex, String rowStyle, Paint rowBackground) {
    }

}
