package dev.webfx.extras.time.window;

import java.util.function.BiConsumer;

public interface ListenableTimeWindow<T> extends TimeWindow<T> {

    void setOnTimeWindowChanged(BiConsumer<T, T> timeWindowChangedHandler);

}
