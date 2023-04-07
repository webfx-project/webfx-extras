package dev.webfx.extras.timelayout.impl;

import dev.webfx.extras.timelayout.CanLayout;
import dev.webfx.extras.timelayout.LayeredTimeLayout;
import dev.webfx.extras.timelayout.TimeLayout;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Bruno Salmon
 */
public class LayeredTimeLayoutImpl<T> implements LayeredTimeLayout<T> {

    private final ObservableList<TimeLayout<?, T>> layers = FXCollections.observableArrayList();

    public LayeredTimeLayoutImpl() {
    }

    @Override
    public ObservableList<TimeLayout<?, T>> getLayers() {
        return layers;
    }

    @Override
    public void addLayer(TimeLayout<?, T> layer) {
        layers.add(layer);
    }

    @Override
    public void setTimeWindow(T timeWindowStart, T timeWindowEnd) {
        layers.forEach(layer -> layer.setTimeWindow(timeWindowStart, timeWindowEnd));
    }

    @Override
    public void markLayoutAsDirty() {
        layers.forEach(CanLayout::markLayoutAsDirty);
    }

    @Override
    public void layout(double width, double height) {
        layers.forEach(layer -> layer.layout(width, height));
    }

}
