package dev.webfx.extras.time.layout.calendar;

import dev.webfx.extras.time.layout.impl.TimeLayoutBase;
import dev.webfx.extras.time.layout.TimeProjector;
import dev.webfx.platform.util.Dates;

import java.time.*;

/**
 * @author Bruno Salmon
 */
public class CalendarLayout<C, T> extends TimeLayoutBase<C, T> {

    public CalendarLayout() {
        setTimeProjector(new TimeProjector<T>() {
            @Override
            public double timeToX(T time, boolean start, boolean exclusive) {
                DayOfWeek dayOfWeek;
                if (time instanceof DayOfWeek)
                    dayOfWeek = (DayOfWeek) time;
                else {
                    LocalDate localDate = timeToLocalDate(time);
                    dayOfWeek = localDate.getDayOfWeek();
                }
                int dayOfWeekColumn = getDayOfWeekColumn(dayOfWeek);
                if (start && exclusive || !start && !exclusive)
                    dayOfWeekColumn++;
                return dayOfWeekColumn * getWidth() / 7;
            }

            @Override
            public T xToTime(double x) {
                return null;
            }
        });
        javafx.collections.ObservableList<C> children = null; // This is just to force the WebFX CLI to add the dependency to javafx-base
    }

    private LocalDate timeToLocalDate(T time) {
        if (time instanceof MonthDay) {
            MonthDay monthDay = (MonthDay) time;
            return monthDay.atYear(Year.now().getValue()); // Assuming it's this year
        } else if (time instanceof YearMonth) {
            YearMonth yearMonth = (YearMonth) time;
            return yearMonth.atDay(1);
        }
        return Dates.toLocalDate(time);
    }

    private int getDayOfWeekColumn(DayOfWeek dayOfWeek) {
        return dayOfWeek.ordinal();
    }

    @Override
    protected int computeChildColumnIndex(int childIndex, C child, T startTime, T endTime, double startX, double endX) {
        DayOfWeek dayOfWeek = null;
        if (startTime instanceof DayOfWeek)
            dayOfWeek = (DayOfWeek) startTime;
        else {
            LocalDate localDate = timeToLocalDate(startTime);
            if (localDate != null)
                dayOfWeek = localDate.getDayOfWeek();
        }
        return dayOfWeek == null ? 0 : getDayOfWeekColumn(dayOfWeek);
    }

    @Override
    protected int computeChildRowIndex(int childIndex, C child, T startTime, T endTime, double startX, double endX) {
        if (startTime instanceof DayOfWeek || startTime instanceof YearMonth)
            return 0;
        LocalDate localDate = timeToLocalDate(startTime);
        int dayOfMonth0 = localDate.getDayOfMonth() - 1; // Starting with 0 for computation convenience
        DayOfWeek dayOfWeek = localDate.getDayOfWeek();
        int dayOfFirstWeek0 = dayOfMonth0 % 7; // Still has same dayOfWeek
        int firstOfMonthColumnShift = (dayOfWeek.ordinal() - dayOfFirstWeek0 + 7 /* to ensure not negative */) % 7;
        return (dayOfMonth0 + firstOfMonthColumnShift) / 7;
    }

}