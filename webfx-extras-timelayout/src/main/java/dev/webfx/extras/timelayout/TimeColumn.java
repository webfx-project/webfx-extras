package dev.webfx.extras.timelayout;

/**
 * @author Bruno Salmon
 */
public interface TimeColumn<T> {

    int getIndex();

    double getWidth();

    double getHeight();

    double getX();

    T getHeadStartTime();

    T getHeadEndTime();

    boolean isHeadInTimeWindow();

    boolean hasChildren();

}
