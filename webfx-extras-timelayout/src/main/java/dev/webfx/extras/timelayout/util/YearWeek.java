package dev.webfx.extras.timelayout.util;

import java.time.LocalDate;
import java.time.temporal.IsoFields;

/**
 * @author Bruno Salmon
 */
public final class YearWeek {

    private final int year;
    private final int week;

    public YearWeek(int year, int week) {
        this.year = year;
        this.week = week;
    }

    public int getYear() {
        return year;
    }

    public int getWeek() {
        return week;
    }

    public static YearWeek of(int year, int week) {
        return new YearWeek(year, week);
    }

    public static YearWeek from(LocalDate localDate) {
        return new YearWeek(localDate.get(IsoFields.WEEK_BASED_YEAR), localDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
    }
}
