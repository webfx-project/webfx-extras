package dev.webfx.extras.time.window.impl;

import dev.webfx.extras.time.window.TimeWindow;
import dev.webfx.extras.time.window.TimeWindowTransaction;
import dev.webfx.kit.util.properties.FXProperties;
import javafx.beans.property.ObjectProperty;

/**
 * @author Bruno Salmon
 */
public class TimeWindowImpl<T> implements TimeWindow<T> {

    protected final ObjectProperty<T> timeWindowStartProperty = FXProperties.newObjectProperty(() -> {
        if (!TimeWindowTransaction.isInTimeWindowTransaction())
            onTimeWindowChanged();
    });
    protected final ObjectProperty<T> timeWindowEndProperty = FXProperties.newObjectProperty(this::onTimeWindowChanged);

    @Override
    public ObjectProperty<T> timeWindowStartProperty() {
        return timeWindowStartProperty;
    }

    @Override
    public ObjectProperty<T> timeWindowEndProperty() {
        return timeWindowEndProperty;
    }

    protected void onTimeWindowChanged() {
    }

}
