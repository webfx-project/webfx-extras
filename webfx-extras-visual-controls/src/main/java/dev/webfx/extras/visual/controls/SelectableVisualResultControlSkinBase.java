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
    protected void start() {
        super.start();
        updateVisualSelection(getSkinnable().getVisualSelection(), null);
        FXProperties.runOnPropertyChange((o, oldValue, newValue) -> updateVisualSelection(newValue, oldValue)
            , getSkinnable().visualSelectionProperty());
    }

    private void updateVisualSelection(VisualSelection selection, VisualSelection oldSelection) {
        if (oldSelection != null)
            oldSelection.forEachRow(rowIndex -> getOrAddBodyRow(rowIndex).getStyleClass().remove("selected"));
        if (selection != null)
            selection.forEachRow(rowIndex -> getOrAddBodyRow(rowIndex).getStyleClass().add("selected"));
    }

    @Override
    protected void updateResult(VisualResult rs) {
        getSkinnable().setVisualSelection(null);
        super.updateResult(rs);
    }

    @Override
    protected void setUpBodyRow(ROW bodyRow, int rowIndex) {
        super.setUpBodyRow(bodyRow, rowIndex);
        bodyRow.setOnMouseClicked(e -> {
            C control = getSkinnable();
            VisualSelection visualSelection = control.getVisualSelection();
            if (visualSelection == null || visualSelection.getSelectedRow() != rowIndex)
                visualSelection = VisualSelection.createSingleRowSelection(rowIndex);
            else
                visualSelection = null;
            control.setVisualSelection(visualSelection);
        });
    }
}
