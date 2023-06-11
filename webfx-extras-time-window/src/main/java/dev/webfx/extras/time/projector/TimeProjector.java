package dev.webfx.extras.time.projector;

import java.time.temporal.TemporalUnit;

public interface TimeProjector<T> {

    double timeToX(T time, boolean start, boolean exclusive);

    T xToTime(double x);

    TemporalUnit getTemporalUnit();


}
