package dev.webfx.extras.time.layout;

import dev.webfx.extras.layer.interact.InteractiveLayer;
import dev.webfx.extras.time.window.ListenableTimeWindow;

import java.util.function.Function;

public interface TimeLayout<C, T> extends CanLayout,
        ListenableTimeWindow<T>,
        InteractiveLayer<C> {

    // Input methods

     double getChildFixedHeight();

    void setChildFixedHeight(double childFixedHeight);

    boolean isFillHeight();

    void setFillHeight(boolean fillHeight);

    double getTopY();

    void setTopY(double topY);

    double getHSpacing();

    void setHSpacing(double hSpacing);

    double getVSpacing();

    void setVSpacing(double vSpacing);

    default void setInclusiveChildStartTimeReader(Function<C, T> startTimeReader) {
        setChildStartTimeReader(startTimeReader, false);
    }

    default void setExclusiveChildStartTimeReader(Function<C, T> startTimeReader) {
        setChildStartTimeReader(startTimeReader, true);
    }

    void setChildStartTimeReader(Function<C, T> startTimeReader, boolean exclusive);

    default void setInclusiveChildEndTimeReader(Function<C, T> endTimeReader) {
        setChildEndTimeReader(endTimeReader, false);
    }

    default void setExclusiveChildEndTimeReader(Function<C, T> endTimeReader) {
        setChildEndTimeReader(endTimeReader, true);
    }

    void setChildEndTimeReader(Function<C, T> childEndTimeReader, boolean exclusive);

    // Output methods

    LayoutBounds getChildPosition(int childIndex);

    int getRowsCount();

}
