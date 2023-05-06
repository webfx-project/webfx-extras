package dev.webfx.extras.time;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;

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

    public LocalDate getFirstDay() {
        LocalDate firstYearMonday = LocalDate.of(getYear(), 1, 1).with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
        return firstYearMonday.plus(getWeek() - 1, ChronoUnit.WEEKS);
    }

    public LocalDate getLastDay() {
        LocalDate firstYearMonday = LocalDate.of(getYear(), 1, 1).with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
        return firstYearMonday.plus(getWeek(), ChronoUnit.WEEKS).minus(1, ChronoUnit.DAYS);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        YearWeek yearWeek = (YearWeek) o;

        if (year != yearWeek.year) return false;
        return week == yearWeek.week;
    }

    @Override
    public int hashCode() {
        int result = year;
        result = 31 * result + week;
        return result;
    }

    public static YearWeek of(int year, int week) {
        return new YearWeek(year, week);
    }

    public static YearWeek from(LocalDate localDate) {
        return new YearWeek(localDate.get(IsoFields.WEEK_BASED_YEAR), localDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
    }
}
