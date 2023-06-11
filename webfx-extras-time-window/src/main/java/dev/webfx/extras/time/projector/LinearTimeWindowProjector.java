package dev.webfx.extras.time.projector;

import dev.webfx.extras.time.window.TimeWindow;
import dev.webfx.extras.time.window.TimeWindowUtil;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.function.Supplier;

/**
 * @author Bruno Salmon
 */
public class LinearTimeWindowProjector<T extends Temporal> implements TimeProjector<T> {

    private final TimeWindow<T> timeWindow;
    private final TemporalUnit temporalUnit;
    private final Supplier<Double> widthSupplier;

    public LinearTimeWindowProjector(TimeWindow<T> timeWindow, TemporalUnit timeUnit, Supplier<Double> widthSupplier) {
        this.timeWindow = timeWindow;
        this.temporalUnit = timeUnit;
        this.widthSupplier = widthSupplier;
    }

    @Override
    public double timeToX(T time, boolean start, boolean exclusive) {
        T timeWindowStart = timeWindow.getTimeWindowStart();
        T timeWindowEnd = timeWindow.getTimeWindowEnd();
        if (timeWindowStart == null || timeWindowEnd == null)
            return 0;
        long duration = TimeWindowUtil.getTimeWindowDuration(timeWindow, temporalUnit);
        long unitsUntilTime = timeWindowStart.until(time, temporalUnit);
        if (start && exclusive || !start && !exclusive)
            unitsUntilTime++;
        double width = widthSupplier.get();
        double x = width * unitsUntilTime / duration;
        x = Math.round(x);
        return x;
    }

    @Override
    public T xToTime(double x) {
        T timeWindowStart = timeWindow.getTimeWindowStart();
        T timeWindowEnd = timeWindow.getTimeWindowEnd();
        if (timeWindowStart == null || timeWindowEnd == null)
            return null;
        long duration = TimeWindowUtil.getTimeWindowDuration(timeWindow, temporalUnit);
        double width = widthSupplier.get();
        return (T) timeWindowStart.plus((long) (x * duration / width), temporalUnit);
    }

    public TemporalUnit getTemporalUnit() {
        return temporalUnit;
    }
}
