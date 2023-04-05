package dev.webfx.extras.timelayout.impl.calendar;

import dev.webfx.extras.timelayout.impl.TimeProjector;
import dev.webfx.platform.util.Dates;

import java.time.*;

/**
 * @author Bruno Salmon
 */
public class CalendarDayTimeProjector<T> implements TimeProjector<T> {

    @Override
    public double timeToX(T time, boolean start, boolean exclusive, double layoutWidth) {
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
        return dayOfWeekColumn * layoutWidth / 7;
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

}
