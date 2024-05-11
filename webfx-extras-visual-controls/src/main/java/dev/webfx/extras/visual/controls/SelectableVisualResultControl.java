package dev.webfx.extras.visual.controls;

import dev.webfx.extras.visual.HasSelectionModeProperty;
import dev.webfx.extras.visual.HasVisualSelectionProperty;
import dev.webfx.extras.visual.SelectionMode;
import dev.webfx.extras.visual.VisualSelection;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @author Bruno Salmon
 */
public abstract class SelectableVisualResultControl extends VisualResultControl implements
        HasVisualSelectionProperty,
        HasSelectionModeProperty {

    private final ObjectProperty<VisualSelection> visualSelectionProperty = VisualSelection.createVisualSelectionProperty();
    @Override
    public ObjectProperty<VisualSelection> visualSelectionProperty() {
        return visualSelectionProperty;
    }

    private final ObjectProperty<SelectionMode> selectionModeProperty = new SimpleObjectProperty<>(SelectionMode.MULTIPLE);
    @Override
    public ObjectProperty<SelectionMode> selectionModeProperty() {
        return selectionModeProperty;
    }
}
