package dev.webfx.extras.time.window;

import javafx.beans.property.ObjectProperty;

import java.util.Objects;

/**
 * @author Bruno Salmon
 */
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

    default void bindTimeWindow(TimeWindow<T> otherTimeWindow) {
        timeWindowStartProperty().bind(otherTimeWindow.timeWindowStartProperty());
        timeWindowEndProperty().bind(otherTimeWindow.timeWindowEndProperty());
    }

    default void bindTimeWindowBidirectional(TimeWindow<T> otherTimeWindow) {
        timeWindowStartProperty().bindBidirectional(otherTimeWindow.timeWindowStartProperty());
        timeWindowEndProperty().bindBidirectional(otherTimeWindow.timeWindowEndProperty());
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

}
