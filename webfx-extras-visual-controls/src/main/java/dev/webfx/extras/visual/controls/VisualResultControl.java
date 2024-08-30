package dev.webfx.extras.visual.controls;

import dev.webfx.extras.visual.HasVisualResultProperty;
import dev.webfx.extras.visual.VisualResult;
import javafx.beans.property.*;
import javafx.scene.control.Control;

/**
 * @author Bruno Salmon
 */
public abstract class VisualResultControl extends Control implements
        HasVisualResultProperty {

    private final ObjectProperty<VisualResult> visualResultProperty = new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
            VisualResult visualResult = get();
            rowCountProperty.set(visualResult == null ? 0 : visualResult.getRowCount());
            requestParentLayout();
        }
    };

    @Override
    public ObjectProperty<VisualResult> visualResultProperty() {
        return visualResultProperty;
    }

    private final IntegerProperty rowCountProperty = new SimpleIntegerProperty();

    public int getRowCount() {
        return rowCountProperty.get();
    }

    public ReadOnlyIntegerProperty rowCountProperty() {
        return rowCountProperty;
    }
}
