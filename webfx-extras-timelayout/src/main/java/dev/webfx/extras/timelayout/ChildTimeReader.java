package dev.webfx.extras.timelayout;

public interface ChildTimeReader<C, T> {

    T getStartTime(C child);

    T getEndTime(C child);

}
