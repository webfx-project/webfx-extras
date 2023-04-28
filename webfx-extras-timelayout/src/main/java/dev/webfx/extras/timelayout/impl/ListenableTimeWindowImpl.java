package dev.webfx.extras.timelayout.impl;

import dev.webfx.extras.timelayout.ListenableTimeWindow;

import java.util.function.BiConsumer;

/**
 * @author Bruno Salmon
 */
public class ListenableTimeWindowImpl<T> extends TimeWindowImpl<T> implements ListenableTimeWindow<T> {

    private BiConsumer<T, T> timeWindowChangedHandler;

    @Override
    public void setOnTimeWindowChanged(BiConsumer<T, T> timeWindowChangedHandler) {
        this.timeWindowChangedHandler = timeWindowChangedHandler;
    }

    @Override
    protected void onTimeWindowChanged() {
        if (timeWindowChangedHandler != null) {
            T start = getTimeWindowStart();
            T end = getTimeWindowEnd();
            if (start != null && end != null)
                timeWindowChangedHandler.accept(start, end);
        }
    }

}
