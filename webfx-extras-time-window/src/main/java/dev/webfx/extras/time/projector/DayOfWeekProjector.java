package dev.webfx.extras.time.projector;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.function.Supplier;

/**
 * @author Bruno Salmon
 */
public class DayOfWeekProjector<T> implements TimeProjector<T> {

    private final Supplier<Double> widthSupplier;

    public DayOfWeekProjector(Supplier<Double> widthSupplier) {
        this.widthSupplier = widthSupplier;
    }

    @Override
    public double timeToX(T time, boolean start, boolean exclusive) {
        DayOfWeek dayOfWeek;
        if (time instanceof DayOfWeek)
            dayOfWeek = (DayOfWeek) time;
        else {
            LocalDate localDate = TimeProjectorUtil.timeToLocalDate(time);
            dayOfWeek = localDate.getDayOfWeek();
        }
        int dayOfWeekColumn = TimeProjectorUtil.getDayOfWeekColumn(dayOfWeek);
        if (start && exclusive || !start && !exclusive)
            dayOfWeekColumn++;
        double width = widthSupplier.get();
        return dayOfWeekColumn * width / 7;
    }

    @Override
    public T xToTime(double x) {
        throw new UnsupportedOperationException(); // Not used so far. Should it be implemented?
    }

    @Override
    public TemporalUnit getTemporalUnit() {
        return ChronoUnit.DAYS;
    }
}
