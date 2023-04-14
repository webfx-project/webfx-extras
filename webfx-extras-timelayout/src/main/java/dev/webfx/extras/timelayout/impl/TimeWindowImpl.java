package dev.webfx.extras.timelayout.impl;

import dev.webfx.extras.timelayout.TimeWindow;
import dev.webfx.extras.timelayout.TimeWindowTransaction;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.function.BiConsumer;

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
    private BiConsumer<T, T> timeWindowChangedHandler;

    @Override
    public ObjectProperty<T> timeWindowStartProperty() {
        return timeWindowStartProperty;
    }

    @Override
    public ObjectProperty<T> timeWindowEndProperty() {
        return timeWindowEndProperty;
    }

    @Override
    public void setOnTimeWindowChanged(BiConsumer<T, T> timeWindowChangedHandler) {
        this.timeWindowChangedHandler = timeWindowChangedHandler;
    }

    private void onTimeWindowChanged() {
        if (timeWindowChangedHandler != null) {
            T start = getTimeWindowStart();
            T end = getTimeWindowEnd();
            if (start != null && end != null)
                timeWindowChangedHandler.accept(start, end);
        }
    }

}
