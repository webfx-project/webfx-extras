package dev.webfx.extras.timelayout.impl;

import dev.webfx.extras.timelayout.CanLayout;
import dev.webfx.extras.timelayout.LayeredTimeLayout;
import dev.webfx.extras.timelayout.TimeLayout;
import dev.webfx.extras.timelayout.TimeWindowTransaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Bruno Salmon
 */
public final class LayeredTimeLayoutImpl<T> extends TimeWindowImpl<T> implements LayeredTimeLayout<T> {

    private final ObservableList<TimeLayout<?, T>> layers = FXCollections.observableArrayList();

    @Override
    public ObservableList<TimeLayout<?, T>> getLayers() {
        return layers;
    }

    @Override
    public void addLayer(TimeLayout<?, T> layer) {
        try (TimeWindowTransaction closable = TimeWindowTransaction.open()) {
            layer.timeWindowStartProperty().bind(timeWindowStartProperty);
        }
        layer.timeWindowEndProperty().bind(timeWindowEndProperty);
        layers.add(layer);
    }

    @Override
    public void removeLayer(TimeLayout<?, T> layer) {
        if (layers.remove(layer)) {
            layer.timeWindowStartProperty().unbind();
            layer.timeWindowEndProperty().unbind();
        }
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
