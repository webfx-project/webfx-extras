package dev.webfx.extras.time.window.impl;

import dev.webfx.extras.time.window.TimeWindow;
import dev.webfx.extras.time.window.TimeWindowTransaction;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @author Bruno Salmon
 */
public class TimeWindowImpl<T> implements TimeWindow<T> {

    protected final ObjectProperty<T> timeWindowStartProperty = new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
            if (!TimeWindowTransaction.isInTimeWindowTransaction())
                onTimeWindowChanged();
        }
    };
    protected final ObjectProperty<T> timeWindowEndProperty = new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
            onTimeWindowChanged();
        }
    };

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
