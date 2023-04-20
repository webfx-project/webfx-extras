package dev.webfx.extras.timelayout;

import dev.webfx.extras.timelayout.impl.LayeredTimeLayoutImpl;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;

public interface LayeredTimeLayout<T> extends TimeWindow<T>, CanLayout {

    ObservableList<TimeLayout<?, T>> getLayers();

    void addLayer(TimeLayout<?, T> layer);

    void removeLayer(TimeLayout<?, T> layer);

    <C> void setSelectedChild(C child, TimeLayout<C, T> childLayer);

    Object getSelectedChild();

    ObjectProperty<Object> selectedChildProperty();

    TimeLayout<?, T> getSelectedChildLayer();

    Object pickChildAt(double x, double y);

    Object selectChildAt(double x, double y);

    static <T> LayeredTimeLayout<T> create() {
        return new LayeredTimeLayoutImpl<>();
    }
}
