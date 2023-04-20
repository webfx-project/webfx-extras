package dev.webfx.extras.timelayout.impl;

import dev.webfx.extras.timelayout.CanLayout;
import dev.webfx.extras.timelayout.LayeredTimeLayout;
import dev.webfx.extras.timelayout.TimeLayout;
import dev.webfx.extras.timelayout.TimeWindowTransaction;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Objects;

/**
 * @author Bruno Salmon
 */
public final class LayeredTimeLayoutImpl<T> extends TimeWindowImpl<T> implements LayeredTimeLayout<T> {

    private final ObservableList<TimeLayout<?, T>> layers = FXCollections.observableArrayList();

    private final ObjectProperty<Object> selectedChildProperty = new SimpleObjectProperty<>();

    private TimeLayout<?, T> selectedChildLayer;

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
        layer.selectedChildProperty().addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> onLayerChildSelected(newValue, layer));
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
        layers.forEach(layer -> {
            if (layer.isVisible())
                layer.layout(width, height);
        });
    }

    @Override
    public <C> void setSelectedChild(C child, TimeLayout<C, T> childLayer) {
        childLayer.setSelectedChild(child);
    }

    private void onLayerChildSelected(Object child, TimeLayout<?, T> childLayer) {
        if (child != null || childLayer == selectedChildLayer) {
            selectedChildLayer = childLayer;
            selectedChildProperty.set(child);
            layers.forEach(layer -> {
                if (layer != selectedChildLayer)
                    layer.setSelectedChild(null);
            });
        }
    }

    @Override
    public Object getSelectedChild() {
        return selectedChildProperty.get();
    }

    @Override
    public ObjectProperty<Object> selectedChildProperty() {
        return selectedChildProperty;
    }

    @Override
    public TimeLayout<?, T> getSelectedChildLayer() {
        return selectedChildLayer;
    }

    @Override
    public Object pickChildAt(double x, double y) {
        return layers.stream()
                .filter(TimeLayout::isVisible)
                .map(layer -> layer.pickChildAt(x, y))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Object selectChildAt(double x, double y) {
        return layers.stream()
                .filter(TimeLayout::isVisible)
                .map(layer -> layer.selectChildAt(x, y))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

}
