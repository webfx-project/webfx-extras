package dev.webfx.extras.timelayout;

import dev.webfx.extras.timelayout.impl.MultilayerTimeLayoutImpl;
import javafx.collections.ObservableList;

public interface MultilayerTimeLayout<T> extends ListenableTimeWindow<T>, CanLayout, CanSelectChild<Object> {

    ObservableList<TimeLayout<?, T>> getLayers();

    void addLayer(TimeLayout<?, T> layer);

    void removeLayer(TimeLayout<?, T> layer);

    @Override
    default void setSelectedChild(Object child) {
        throw new UnsupportedOperationException("Use LayeredTimeLayout.setSelectedChild(child, childLayer) instead");
    }

    <C> void setSelectedChild(C child, TimeLayout<C, T> childLayer);

    TimeLayout<?, T> getSelectedChildLayer();

    static <T> MultilayerTimeLayout<T> create() {
        return new MultilayerTimeLayoutImpl<>();
    }
}
