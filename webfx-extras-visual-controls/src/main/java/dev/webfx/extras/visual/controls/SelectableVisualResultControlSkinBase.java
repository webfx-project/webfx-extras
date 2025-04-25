package dev.webfx.extras.visual.controls;

import dev.webfx.kit.util.properties.FXProperties;
import javafx.scene.Node;
import dev.webfx.extras.visual.VisualResult;
import dev.webfx.extras.visual.VisualSelection;

/**
 * @author Bruno Salmon
 */
public abstract class SelectableVisualResultControlSkinBase<C extends SelectableVisualResultControl, ROW extends Node, CELL extends Node>
    extends VisualResultControlSkinBase<C, ROW, CELL> {

    public SelectableVisualResultControlSkinBase(C control, boolean hasSpecialRenderingForImageAndText) {
        super(control, hasSpecialRenderingForImageAndText);
    }

    @Override
    public void install() {
        super.install();
        unregisterOnDispose(
            FXProperties.runNowAndOnPropertyChange((o, oldValue, newValue)
                    -> updateVisualSelection(newValue, oldValue)
                , visualControl.visualSelectionProperty())
        );
    }

    private void updateVisualSelection(VisualSelection selection, VisualSelection oldSelection) {
        if (oldSelection != null)
            oldSelection.forEachRow(rowIndex -> getOrAddBodyRow(rowIndex).getStyleClass().remove("selected"));
        if (selection != null)
            selection.forEachRow(rowIndex -> getOrAddBodyRow(rowIndex).getStyleClass().add("selected"));
    }

    @Override
    protected void updateResult(VisualResult rs) {
        visualControl.setVisualSelection(null);
        super.updateResult(rs);
    }

    @Override
    protected void setUpBodyRow(ROW bodyRow, int rowIndex) {
        super.setUpBodyRow(bodyRow, rowIndex);
        bodyRow.setOnMouseClicked(e -> {
            VisualSelection visualSelection = visualControl.getVisualSelection();
            if (visualSelection == null || visualSelection.getSelectedRow() != rowIndex)
                visualSelection = VisualSelection.createSingleRowSelection(rowIndex);
            else
                visualSelection = null;
            visualControl.setVisualSelection(visualSelection);
        });
    }
}
