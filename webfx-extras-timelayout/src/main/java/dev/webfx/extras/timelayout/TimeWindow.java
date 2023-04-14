package dev.webfx.extras.timelayout;

import javafx.beans.property.ObjectProperty;

import java.util.Objects;
import java.util.function.BiConsumer;

public interface TimeWindow<T> {

    ObjectProperty<T> timeWindowStartProperty();

    default T getTimeWindowStart() {
        return timeWindowStartProperty().get();
    }

    default void setTimeWindowStart(T timeWindowStart) {
        timeWindowStartProperty().set(timeWindowStart);
    }

    ObjectProperty<T> timeWindowEndProperty();

    default T getTimeWindowEnd() {
        return timeWindowEndProperty().get();
    }

    default void setTimeWindowEnd(T timeWindowEnd) {
        timeWindowEndProperty().set(timeWindowEnd);
    }

    default void setTimeWindow(T timeWindowStart, T timeWindowEnd) {
        if (Objects.equals(timeWindowStart, getTimeWindowStart()))
            setTimeWindowEnd(timeWindowEnd);
        else if (Objects.equals(timeWindowEnd, getTimeWindowEnd()))
            setTimeWindowStart(timeWindowStart);
        else {
            try (TimeWindowTransaction closable = TimeWindowTransaction.open()) {
                setTimeWindowStart(timeWindowStart);
            }
            setTimeWindowEnd(timeWindowEnd);
        }
    }

    void setOnTimeWindowChanged(BiConsumer<T, T> timeWindowChangedHandler);

}
