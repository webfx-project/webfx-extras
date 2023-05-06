package dev.webfx.extras.time.layout.impl;

public interface TimeProjector<T> {

    double timeToX(T time, boolean start, boolean exclusive);

}
