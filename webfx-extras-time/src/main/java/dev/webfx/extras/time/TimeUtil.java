package dev.webfx.extras.time;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Bruno Salmon
 */
public final class TimeUtil {

    public static List<MonthDay> generateThisMonthDays() {
        return generateMonthDays(YearMonth.now());
    }

    public static List<MonthDay> generateMonthDays(YearMonth yearMonth) {
        return IntStream.range(1, yearMonth.atEndOfMonth().getDayOfMonth() + 1)
                .mapToObj(d -> MonthDay.of(yearMonth.getMonth(), d))
                .collect(Collectors.toList());
    }

    public static List<LocalDate> generateThisMonthDates() {
        return generateMonthDates(YearMonth.now());
    }

    public static List<LocalDate> generateMonthDates(YearMonth yearMonth) {
        return IntStream.range(1, yearMonth.atEndOfMonth().getDayOfMonth() + 1)
                .mapToObj(d -> LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), d))
                .collect(Collectors.toList());
    }

    public static List<DayOfWeek> generateDaysOfWeek() {
        return Arrays.asList(DayOfWeek.values());
    }

    public static List<YearMonth> generateThisYearMonths() {
        return generateYearMonths(Year.now().getValue());
    }

    public static List<YearMonth> generateYearMonths(int year) {
        return generateYearMonths(year, 1, 12);
    }

    public static List<YearMonth> generateYearMonthsRelativeToThisMonth(int startShift, int endShift) {
        YearMonth thisMonth = YearMonth.now();
        return generateYearMonths(thisMonth.plusMonths(startShift), thisMonth.plusMonths(endShift));
    }


    public static List<YearMonth> generateYearMonths(int year, int firstMonth, int lastMonth) {
        return generateYearMonths(YearMonth.of(year, firstMonth), YearMonth.of(year, lastMonth));
    }

    public static List<YearMonth> generateYearMonths(YearMonth first, YearMonth last) {
        List<YearMonth> yearMonths = new ArrayList<>();
        if (last.isAfter(first) || last.equals(first)) {
            for (YearMonth yearMonth = first; yearMonth.isBefore(last) || yearMonth.equals(last); yearMonth = yearMonth.plusMonths(1)) {
                yearMonths.add(yearMonth);
            }
        }
        return yearMonths;
    }

    public static List<Year> generateYears(int firstYear, int lastYear) {
        return generateYears(Year.of(firstYear), Year.of(lastYear));
    }

    public static List<Year> generateYears(Year first, Year last) {
        List<Year> years = new ArrayList<>();
        if (last.isAfter(first) || last.equals(first)) {
            for (Year year = first; year.isBefore(last) || year.equals(last); year = year.plusYears(1)) {
                years.add(year);
            }
        }
        return years;
    }

    public static List<YearWeek> generateYearWeeks(int firstYear, int firstWeek, int lastYear, int lastWeek) {
        return generateYearWeeks(YearWeek.of(firstYear, firstWeek), YearWeek.of(lastYear, lastWeek));
    }

    public static List<YearWeek> generateYearWeeks(YearWeek first, YearWeek last) {
        List<YearWeek> yearWeeks = new ArrayList<>();
        LocalDate localDate = LocalDate.of(first.getYear(), 1, 1).plus(first.getWeek(), ChronoUnit.WEEKS);
        while (true) {
            int year = localDate.get( IsoFields.WEEK_BASED_YEAR ) ;
            int week = localDate.get( IsoFields.WEEK_OF_WEEK_BASED_YEAR ) ;
            if (year > last.getYear() || year == last.getYear() && week > last.getWeek())
                break;
            yearWeeks.add(YearWeek.of(year, week));
            localDate = localDate.plus(1, ChronoUnit.WEEKS);
        }
        return yearWeeks;
    }

    public static List<LocalDate> generateLocalDates(LocalDate first, LocalDate last) {
        List<LocalDate> dates = new ArrayList<>();
        if (last.isAfter(first) || last.equals(first)) {
            for (LocalDate date = first; date.isBefore(last) || date.equals(last); date = date.plusDays(1)) {
                dates.add(date);
            }
        }
        return dates;
    }

    public static LocalDate getFirstDayOfYear(Year year) {
        return year.atDay(1);
    }

    public static LocalDate getLastDayOfYear(Year year) {
        return year.atMonthDay(MonthDay.of(12, 31));
    }

    public static LocalDate getFirstDayOfMonth(YearMonth yearMonth) {
        return yearMonth.atDay(1);
    }

    public static LocalDate getLastDayOfMonth(YearMonth yearMonth) {
        return yearMonth.atEndOfMonth();
    }

    public static LocalDate getFirstDayOfWeek(YearWeek yearWeek) {
        return yearWeek.getFirstDay();
    }

    public static LocalDate getLastDayOfWeek(YearWeek yearWeek) {
        return yearWeek.getLastDay();
    }

}
