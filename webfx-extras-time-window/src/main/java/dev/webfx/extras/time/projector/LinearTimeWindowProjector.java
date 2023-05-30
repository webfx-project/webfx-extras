package dev.webfx.extras.time.projector;

import dev.webfx.extras.time.window.TimeWindow;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.function.Supplier;

/**
 * @author Bruno Salmon
 */
public class LinearTimeWindowProjector<T extends Temporal> implements TimeProjector<T> {

    private final TimeWindow<T> timeWindow;
    private final Supplier<Double> widthSupplier;

    public LinearTimeWindowProjector(TimeWindow<T> timeWindow, Supplier<Double> widthSupplier) {
        this.timeWindow = timeWindow;
        this.widthSupplier = widthSupplier;
    }

    @Override
    public double timeToX(T time, boolean start, boolean exclusive) {
        T timeWindowStart = timeWindow.getTimeWindowStart();
        T timeWindowEnd = timeWindow.getTimeWindowEnd();
        if (timeWindowStart == null || timeWindowEnd == null)
            return 0;
        long totalDays = timeWindowStart.until(timeWindowEnd, ChronoUnit.DAYS) + 1;
        long daysToTime = timeWindowStart.until(time, ChronoUnit.DAYS);
        if (start && exclusive || !start && !exclusive)
            daysToTime++;
        double width = widthSupplier.get();
        double x = width * daysToTime / totalDays;
        x = Math.round(x);
        return x;
    }

    @Override
    public T xToTime(double x) {
        T timeWindowStart = timeWindow.getTimeWindowStart();
        T timeWindowEnd = timeWindow.getTimeWindowEnd();
        if (timeWindowStart == null || timeWindowEnd == null)
            return null;
        long totalDays = timeWindowStart.until(timeWindowEnd, ChronoUnit.DAYS) + 1;
        double width = widthSupplier.get();
        return (T) timeWindowStart.plus((long) (x * totalDays / width), ChronoUnit.DAYS);
    }
}
