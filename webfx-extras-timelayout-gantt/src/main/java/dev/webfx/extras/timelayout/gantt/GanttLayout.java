package dev.webfx.extras.timelayout.gantt;

import dev.webfx.extras.timelayout.impl.TimeLayoutBase;
import dev.webfx.extras.timelayout.impl.TimeProjector;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

/**
 * @author Bruno Salmon
 */
public final class GanttLayout <C, T> extends TimeLayoutBase<C, T> implements TimeProjector<T> {

    @Override
    protected TimeProjector<T> getTimeProjector() {
        return this;
    }

    @Override
    public double timeToX(T time, boolean start, boolean exclusive, double layoutWidth) {
        long totalDays = ((Temporal) timeWindowStart).until((Temporal) timeWindowEnd, ChronoUnit.DAYS) + 1;
        long daysToTime = ((Temporal) timeWindowStart).until((Temporal) time, ChronoUnit.DAYS);
        if (start && exclusive || !start && !exclusive)
            daysToTime++;
        return layoutWidth * daysToTime / totalDays;
    }

    @Override
    protected int computeChildColumnIndex(int childIndex, C child, T startTime, T endTime, double startX, double endX) {
        return 0;
    }

    @Override
    protected int computeChildRowIndex(int childIndex, C child, T startTime, T endTime, double startX, double endX) {
        return child instanceof Temporal ? 0 : childIndex;
    }

}
