package dev.webfx.extras.timelayout;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;

import java.util.Objects;
import java.util.function.BiConsumer;

public interface TimeWindow<T> {

    ObjectProperty<T> timeWindowStartProperty();

    default T getTimeWindowStart() {
        return timeWindowStartProperty().get();
    }

    default void setTimeWindowStart(T timeWindowStart) {
        if (!Objects.equals(timeWindowStart, getTimeWindowStart()))
            timeWindowStartProperty().set(timeWindowStart);
    }

    ObjectProperty<T> timeWindowEndProperty();

    default T getTimeWindowEnd() {
        return timeWindowEndProperty().get();
    }

    default void setTimeWindowEnd(T timeWindowEnd) {
        if (!Objects.equals(timeWindowEnd, getTimeWindowEnd()))
            timeWindowEndProperty().set(timeWindowEnd);
    }

    default void setTimeWindow(T timeWindowStart, T timeWindowEnd) {
        boolean startUnchanged = Objects.equals(timeWindowStart, getTimeWindowStart());
        boolean endUnchanged = Objects.equals(timeWindowEnd, getTimeWindowEnd());
        if (startUnchanged && endUnchanged)
            return;
        if (startUnchanged)
            setTimeWindowEnd(timeWindowEnd);
        else if (endUnchanged)
            setTimeWindowStart(timeWindowStart);
        else {
            try (TimeWindowTransaction closable = TimeWindowTransaction.open()) {
                setTimeWindowStart(timeWindowStart);
            }
            setTimeWindowEnd(timeWindowEnd);
        }
    }

    default void bindTimeWindow(Property<T> startProperty, Property<T> endProperty, boolean applyInitialValues, boolean bidirectional) {
        if (applyInitialValues)
            setTimeWindow(startProperty.getValue(), endProperty.getValue());
        if (bidirectional) {
            startProperty.bindBidirectional(timeWindowStartProperty());
            endProperty.bindBidirectional(timeWindowEndProperty());
        } else {
            startProperty.bind(timeWindowStartProperty());
            endProperty.bind(timeWindowEndProperty());
        }
    }

    void setOnTimeWindowChanged(BiConsumer<T, T> timeWindowChangedHandler);

}
