package dev.webfx.extras.visual.controls;

import dev.webfx.extras.visual.HasVisualResultProperty;
import dev.webfx.extras.visual.VisualResult;
import dev.webfx.kit.util.properties.FXProperties;
import javafx.beans.property.*;
import javafx.scene.control.Control;

/**
 * @author Bruno Salmon
 */
public abstract class VisualResultControl extends Control implements
        HasVisualResultProperty {

    private final IntegerProperty rowCountProperty = new SimpleIntegerProperty();
    private Object appContext;

    private final ObjectProperty<VisualResult> visualResultProperty = FXProperties.newObjectProperty(visualResult -> {
            rowCountProperty.set(visualResult == null ? 0 : visualResult.getRowCount());
            requestParentLayout();
        });

    @Override
    public ObjectProperty<VisualResult> visualResultProperty() {
        return visualResultProperty;
    }

    public int getRowCount() {
        return rowCountProperty.get();
    }

    public ReadOnlyIntegerProperty rowCountProperty() {
        return rowCountProperty;
    }

    public Object getAppContext() {
        return appContext;
    }

    public void setAppContext(Object appContext) {
        this.appContext = appContext;
    }
}
