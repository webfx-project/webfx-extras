package dev.webfx.extras.time.layout;

import dev.webfx.extras.geometry.Bounds;
import dev.webfx.extras.layer.interact.InteractiveLayer;
import dev.webfx.extras.time.layout.impl.ChildBounds;
import dev.webfx.extras.time.projector.HasTimeProjector;
import dev.webfx.extras.time.window.ListenableTimeWindow;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface TimeLayout<C, T> extends CanLayout,
        ListenableTimeWindow<T>,
        HasTimeProjector<T>,
        InteractiveLayer<C> {

    // Input methods

    double getChildFixedHeight();

    TimeLayout<C, T> setChildFixedHeight(double childFixedHeight);

    boolean isFillHeight();

    TimeLayout<C, T> setFillHeight(boolean fillHeight);

    double getTopY();

    TimeLayout<C, T> setTopY(double topY);

    double getHSpacing();

    TimeLayout<C, T> setHSpacing(double hSpacing);

    double getVSpacing();

    TimeLayout<C, T> setVSpacing(double vSpacing);

    default TimeLayout<C, T> setInclusiveChildStartTimeReader(Function<C, T> startTimeReader) {
        return setChildStartTimeReader(startTimeReader, false);
    }

    default TimeLayout<C, T> setExclusiveChildStartTimeReader(Function<C, T> startTimeReader) {
        return setChildStartTimeReader(startTimeReader, true);
    }

    TimeLayout<C, T> setChildStartTimeReader(Function<C, T> startTimeReader, boolean exclusive);

    default TimeLayout<C, T> setInclusiveChildEndTimeReader(Function<C, T> endTimeReader) {
        return setChildEndTimeReader(endTimeReader, false);
    }

    default TimeLayout<C, T> setExclusiveChildEndTimeReader(Function<C, T> endTimeReader) {
        return setChildEndTimeReader(endTimeReader, true);
    }

    TimeLayout<C, T> setChildEndTimeReader(Function<C, T> childEndTimeReader, boolean exclusive);

    default TimeLayout<C, T> setCanvasDirtyMarker(Runnable canvasDirtyMarker) {
        addOnAfterLayout(canvasDirtyMarker);
        selectedChildProperty().addListener(observable -> canvasDirtyMarker.run());
        return this;
    }

    void invalidateHorizontalLayout();

    void setParent(MultilayerTimeLayout<T> parent);

    // Output methods

    ChildBounds<C, T> getChildBounds(int childIndex);

    int getRowsCount();

    default void processVisibleChildren(BiConsumer<C, Bounds> childProcessor) {
        processVisibleChildren(null, 0, 0, childProcessor);
    }

    void processVisibleChildren(javafx.geometry.Bounds visibleArea, double originX, double originY, BiConsumer<C, Bounds> childProcessor);

    // If the child belongs to a parent row that is not fully expanded, then this method should return the bounds of the
    // that parent row that can be used for clipping the child
    default Bounds getClippingParentRowBounds(C child) {
        return null;
    }
}
