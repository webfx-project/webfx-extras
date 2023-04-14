package dev.webfx.extras.timelayout;

import dev.webfx.extras.timelayout.impl.LayeredTimeLayoutImpl;
import javafx.collections.ObservableList;

public interface LayeredTimeLayout<T> extends TimeWindow<T>, CanLayout {

    ObservableList<TimeLayout<?, T>> getLayers();

    void addLayer(TimeLayout<?, T> layer);

    void removeLayer(TimeLayout<?, T> layer);

    static <T> LayeredTimeLayout<T> create() {
        return new LayeredTimeLayoutImpl<>();
    }
}
