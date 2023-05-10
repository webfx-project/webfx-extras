package dev.webfx.extras.time.layout;

public interface TimeProjector<T> {

    double timeToX(T time, boolean start, boolean exclusive);

    T xToTime(double x);

}
