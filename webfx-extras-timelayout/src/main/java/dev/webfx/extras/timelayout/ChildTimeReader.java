package dev.webfx.extras.timelayout;

public interface ChildTimeReader<T, C> {

    T getStartTime(C child);

    T getEndTime(C child);

}
