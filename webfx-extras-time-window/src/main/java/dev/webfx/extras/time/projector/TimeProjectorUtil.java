package dev.webfx.extras.time.projector;

import dev.webfx.platform.util.Dates;

import java.time.*;

/**
 * @author Bruno Salmon
 */
public class TimeProjectorUtil {

    public static <T> LocalDate timeToLocalDate(T time) {
        if (time instanceof MonthDay) {
            MonthDay monthDay = (MonthDay) time;
            return monthDay.atYear(Year.now().getValue()); // Assuming it's this year
        } else if (time instanceof YearMonth) {
            YearMonth yearMonth = (YearMonth) time;
            return yearMonth.atDay(1);
        }
        return Dates.toLocalDate(time);
    }

    public static int getDayOfWeekColumn(DayOfWeek dayOfWeek) {
        return dayOfWeek.ordinal();
    }

}
