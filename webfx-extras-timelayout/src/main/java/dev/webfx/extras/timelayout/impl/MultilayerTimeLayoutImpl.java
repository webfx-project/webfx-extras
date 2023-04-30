package dev.webfx.extras.timelayout.impl;

import dev.webfx.extras.timelayout.*;
import dev.webfx.extras.timelayout.util.DirtyMarker;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableIntegerValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.Objects;

/**
 * @author Bruno Salmon
 */
public class MultilayerTimeLayoutImpl<T> extends ListenableTimeWindowImpl<T> implements MultilayerTimeLayout<T> {

    private final DoubleProperty widthProperty = new SimpleDoubleProperty() {
        @Override
        protected void invalidated() {
            markLayoutAsDirty();
        }
    };
    private final DoubleProperty heightProperty = new SimpleDoubleProperty() {
        @Override
        protected void invalidated() {
            //markLayoutAsDirty();
        }
    };
    private final IntegerProperty layoutCountProperty = new SimpleIntegerProperty();
    private final ObservableList<TimeLayout<?, T>> layers = FXCollections.observableArrayList();

    private boolean childSelectionEnabled = true;
    private final ObjectProperty<Object> selectedChildProperty = new SimpleObjectProperty<>();
    private TimeLayout<?, T> selectedChildLayer;
    private final DirtyMarker layoutDirtyMarker = new DirtyMarker(this::layout);

    @Override
    public DoubleProperty widthProperty() {
        return widthProperty;
    }

    @Override
    public DoubleProperty heightProperty() {
        return heightProperty;
    }

    @Override
    public ObservableIntegerValue layoutCountProperty() {
        return layoutCountProperty;
    }

    @Override
    public ObservableList<TimeLayout<?, T>> getLayers() {
        return layers;
    }

    @Override
    public void addLayer(TimeLayout<?, T> layer) {
        layers.add(layer);
        try (TimeWindowTransaction closable = TimeWindowTransaction.open()) {
            layer.timeWindowStartProperty().bind(timeWindowStartProperty);
        }
        layer.timeWindowEndProperty().bind(timeWindowEndProperty);
        layer.widthProperty().bind(widthProperty);
        layer.selectedChildProperty().addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> onLayerChildSelected(newValue, layer));
        layer.getChildren().addListener((ListChangeListener<Object>) c -> markLayoutAsDirty());
        if (!layer.getChildren().isEmpty())
            markLayoutAsDirty();
    }

    @Override
    public void removeLayer(TimeLayout<?, T> layer) {
        if (layers.remove(layer)) {
            layer.widthProperty().unbind();
            layer.timeWindowStartProperty().unbind();
            layer.timeWindowEndProperty().unbind();
        }
    }

    @Override
    public void markLayoutAsDirty() {
        if (!isLayouting())
            layoutDirtyMarker.markAsDirty();
    }

    @Override
    public boolean isLayoutDirty() {
        return layoutDirtyMarker.isDirty();
    }

    @Override
    public void layout() {
        int newLayoutCount = getLayoutCount() + 1;
        layoutCountProperty.set(-newLayoutCount); // may trigger onBeforeLayout runnable(s)
        layers.forEach(CanLayout::layout);
        // Automatically updating this layout height
        if (!heightProperty.isBound())
            setHeight(getLayersMaxY());
        layoutDirtyMarker.markAsClean();
        layoutCountProperty.set(newLayoutCount); // may trigger onAfterLayout runnable(s)
    }

    private double getLayersMaxY() {
        double maxY = 0;
        for (TimeLayout<?, T> layer : getLayers()) {
            if (layer.isVisible()) {
                maxY = Math.max(maxY, layer.getTopY() + layer.getHeight());
            }
        }
        return maxY;
    }

    @Override
    public boolean isChildSelectionEnabled() {
        return childSelectionEnabled;
    }

    @Override
    public void setChildSelectionEnabled(boolean childSelectionEnabled) {
        this.childSelectionEnabled = childSelectionEnabled;
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
    public ObjectProperty<Object> selectedChildProperty() {
        return selectedChildProperty;
    }

    @Override
    public TimeLayout<?, T> getSelectedChildLayer() {
        return selectedChildLayer;
    }

    @Override
    public Object pickChildAt(double x, double y, boolean onlyIfSelectable) {
        return layers.stream()
                .filter(TimeLayout::isVisible)
                .map(layer -> layer.pickChildAt(x, y, onlyIfSelectable))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Object selectChildAt(double x, double y) {
        if (!isChildSelectionEnabled())
            return null;
        return layers.stream()
                .filter(TimeLayout::isVisible)
                .map(layer -> layer.selectChildAt(x, y))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

}
