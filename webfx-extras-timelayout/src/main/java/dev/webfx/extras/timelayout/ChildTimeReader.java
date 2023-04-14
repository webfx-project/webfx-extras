package dev.webfx.extras.timelayout;

public interface ChildTimeReader<C, T> {

    T getTime(C child);

}
