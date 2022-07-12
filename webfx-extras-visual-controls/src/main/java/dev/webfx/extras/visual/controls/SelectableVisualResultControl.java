package dev.webfx.extras.visual.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import dev.webfx.extras.visual.VisualSelection;
import dev.webfx.extras.visual.HasVisualSelectionProperty;
import dev.webfx.extras.visual.HasSelectionModeProperty;
import dev.webfx.extras.visual.SelectionMode;

/**
 * @author Bruno Salmon
 */
public abstract class SelectableVisualResultControl extends VisualResultControl implements
        HasVisualSelectionProperty,
        HasSelectionModeProperty {

    private final ObjectProperty<VisualSelection> visualSelectionProperty = new SimpleObjectProperty<>();
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
