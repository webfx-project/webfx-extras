package dev.webfx.extras.timelayout.impl;

public interface TimeProjector<T> {

    double timeToX(T time, boolean start, boolean exclusive, double layoutWidth);

}