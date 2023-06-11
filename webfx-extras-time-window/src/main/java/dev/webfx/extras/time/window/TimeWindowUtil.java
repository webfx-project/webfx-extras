package dev.webfx.extras.time.window;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;

/**
 * @author Bruno Salmon
 */
public final class TimeWindowUtil {

    public static <T extends Temporal> long getTimeWindowDuration(TimeWindow<T> timeWindow, TemporalUnit temporalUnit) {
        return temporalUnit.between(timeWindow.getTimeWindowStart(), timeWindow.getTimeWindowEnd()) + 1;
    }

    public static <T extends Temporal> T getTimeWindowCenter(TimeWindow<T> timeWindow, TemporalUnit temporalUnit) {
        T start = timeWindow.getTimeWindowStart();
        long duration = TimeWindowUtil.getTimeWindowDuration(timeWindow, temporalUnit);
        return (T) start.plus((duration - 1) / 2, temporalUnit);
    }

    public static <T extends Temporal> void setTimeWindowStartAndDuration(TimeWindow<T> timeWindow, T start, long duration, TemporalUnit temporalUnit) {
        timeWindow.setTimeWindow(start, (T) start.plus(duration, temporalUnit));
    }

    public static <T extends Temporal> void setTimeWindowCenterAndDuration(TimeWindow<T> timeWindow, T middle, long duration, TemporalUnit temporalUnit) {
        T start = (T) middle.minus(duration / 2, temporalUnit);
        setTimeWindowStartAndDuration(timeWindow, start, duration, temporalUnit);
    }

    public static <T extends Temporal> void setTimeWindowDurationKeepCentered(TimeWindow<T> timeWindow, long duration, TemporalUnit temporalUnit) {
        T center = TimeWindowUtil.getTimeWindowCenter(timeWindow, temporalUnit);
        setTimeWindowCenterAndDuration(timeWindow, center, duration, temporalUnit);
    }

    public static <T extends Temporal> void setTimeWindowStart(TimeWindow<T> timeWindow, T newStart, TemporalUnit temporalUnit) {
        long timeWindowDuration = getTimeWindowDuration(timeWindow, temporalUnit);
        T newEnd = (T) timeWindow.getTimeWindowStart().plus(timeWindowDuration, temporalUnit);
        timeWindow.setTimeWindow(newStart, newEnd);
    }

    public static <T extends Temporal> void setTimeWindowCenter(TimeWindow<T> timeWindow, T center, TemporalUnit temporalUnit) {
        setTimeWindowCenterAndDuration(timeWindow, center, getTimeWindowDuration(timeWindow, temporalUnit), temporalUnit);
    }

    public static <T extends Temporal> void shiftTimeWindow(TimeWindow<T> timeWindow, long amount, TemporalUnit temporalUnit) {
        setTimeWindowStart(timeWindow, (T) timeWindow.getTimeWindowStart().plus(amount, temporalUnit), temporalUnit);
    }

}
