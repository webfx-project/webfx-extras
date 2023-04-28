package dev.webfx.extras.timelayout;

import java.util.function.BiConsumer;

public interface ListenableTimeWindow<T> extends TimeWindow<T> {

    void setOnTimeWindowChanged(BiConsumer<T, T> timeWindowChangedHandler);

}
