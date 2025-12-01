package dev.webfx.extras.time.window.impl;

import dev.webfx.extras.time.window.TimeWindow;
import dev.webfx.extras.time.window.TimeWindowTransaction;
import dev.webfx.kit.util.properties.FXProperties;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * @author Bruno Salmon
 */
public class TimeWindowImpl<T> implements TimeWindow<T> {

    protected final ObjectProperty<T> timeWindowStartProperty = FXProperties.newObjectProperty(() -> {
        if (!TimeWindowTransaction.isInTimeWindowTransaction())
            onTimeWindowChanged();
    });
    protected final ObjectProperty<T> timeWindowEndProperty = FXProperties.newObjectProperty(this::onTimeWindowChanged);
    protected final DoubleProperty timeWindowTranslateXProperty = new SimpleDoubleProperty();

    @Override
    public ObjectProperty<T> timeWindowStartProperty() {
        return timeWindowStartProperty;
    }

    @Override
    public ObjectProperty<T> timeWindowEndProperty() {
        return timeWindowEndProperty;
    }

    @Override
    public DoubleProperty timeWindowTranslateXProperty() {
        return timeWindowTranslateXProperty;
    }

    protected void onTimeWindowChanged() {
    }

}
