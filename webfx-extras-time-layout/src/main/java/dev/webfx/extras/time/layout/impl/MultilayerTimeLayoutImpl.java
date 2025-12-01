package dev.webfx.extras.time.layout.impl;

import dev.webfx.extras.layer.interact.InteractiveLayer;
import dev.webfx.extras.time.layout.CanLayout;
import dev.webfx.extras.time.layout.MultilayerTimeLayout;
import dev.webfx.extras.time.layout.TimeLayout;
import dev.webfx.extras.time.projector.TimeProjector;
import dev.webfx.extras.time.window.TimeWindowTransaction;
import dev.webfx.extras.time.window.impl.ListenableTimeWindowImpl;
import dev.webfx.extras.util.DirtyMarker;
import dev.webfx.kit.util.properties.FXProperties;
import javafx.beans.property.*;
import javafx.beans.value.ObservableIntegerValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * @author Bruno Salmon
 */
public class MultilayerTimeLayoutImpl<T> extends ListenableTimeWindowImpl<T> implements MultilayerTimeLayout<T> {

    private final DoubleProperty widthProperty = FXProperties.newDoubleProperty(this::markLayoutAsDirty);
    private final DoubleProperty heightProperty = new SimpleDoubleProperty();
    private TimeProjector<T> timeProjector;
    private final IntegerProperty layoutCountProperty = new SimpleIntegerProperty();
    private final ObservableList<TimeLayout<?, T>> layers = FXCollections.observableArrayList();

    private boolean childSelectionEnabled = true;
    private final ObjectProperty<Object> selectedChildProperty = new SimpleObjectProperty<>();
    private TimeLayout<?, T> selectedChildLayer;
    private final DirtyMarker layoutDirtyMarker = new DirtyMarker(this::layout);
    private Runnable canvasDirtyMarker;

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
    public TimeProjector<T> getTimeProjector() {
        return timeProjector;
    }

    @Override
    public MultilayerTimeLayoutImpl<T> setTimeProjector(TimeProjector<T> timeProjector) {
        this.timeProjector = timeProjector;
        // Passing the time projector to existing layers (will be also set on layers added later)
        if (timeProjector != null)
            layers.forEach(layer -> layer.setTimeProjector(timeProjector));
        return this;
    }

    @Override
    public void addLayer(TimeLayout<?, T> layer) {
        if (timeProjector != null)
            layer.setTimeProjector(timeProjector);
        else
            timeProjector = layer.getTimeProjector();
        layers.add(layer);
        try (TimeWindowTransaction closable = TimeWindowTransaction.open()) {
            layer.timeWindowStartProperty().bind(timeWindowStartProperty);
        }
        layer.timeWindowEndProperty().bind(timeWindowEndProperty);
        layer.widthProperty().bind(widthProperty);
        FXProperties.runOnPropertyChange(child -> onLayerChildSelected(child, layer), layer.selectedChildProperty());
        if (canvasDirtyMarker != null)
            layer.setCanvasDirtyMarker(canvasDirtyMarker);
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
            setHeight(getVisibleLayersMaxBottom());
        layoutDirtyMarker.markAsClean();
        layoutCountProperty.set(newLayoutCount); // may trigger onAfterLayout runnable(s)
        if (canvasDirtyMarker != null)
            canvasDirtyMarker.run();
    }

    private double getVisibleLayersMaxBottom() {
        double maxBottom = 0;
        for (TimeLayout<?, T> layer : getLayers()) {
            if (layer.isVisible()) {
                maxBottom = Math.max(maxBottom, layer.getTopY() + layer.getHeight());
            }
        }
        return maxBottom;
    }

    @Override
    public boolean isSelectionEnabled() {
        return childSelectionEnabled;
    }

    @Override
    public MultilayerTimeLayoutImpl<T> setSelectionEnabled(boolean selectionEnabled) {
        this.childSelectionEnabled = selectionEnabled;
        return this;
    }

    @Override
    public <C, L extends InteractiveLayer<C>> void setSelectedChild(C child, L childLayer) {
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
    public void setCanvasDirtyMarker(Runnable canvasDirtyMarker) {
        this.canvasDirtyMarker = canvasDirtyMarker;
        layers.forEach(layer -> layer.setCanvasDirtyMarker(canvasDirtyMarker));
    }
}
