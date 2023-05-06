package dev.webfx.extras.time.layout.gantt;

import dev.webfx.extras.time.TimeUtil;
import dev.webfx.extras.time.YearWeek;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;

/**
 * @author Bruno Salmon
 */
public final class LocalDateGanttLayout<C> extends GanttLayout<C, LocalDate> {

    // Static factory methods

    public static LocalDateGanttLayout<Year> createYearLocalDateGanttLayout() {
        LocalDateGanttLayout<Year> ganttLayout = new LocalDateGanttLayout<>();
        ganttLayout.setInclusiveChildStartTimeReader(TimeUtil::getFirstDayOfYear);
        ganttLayout.setInclusiveChildEndTimeReader(TimeUtil::getLastDayOfYear);
        ganttLayout.setOnTimeWindowChanged((start, end) ->
            ganttLayout.getChildren().setAll(TimeUtil.generateYears(Year.from(start), Year.from(end))));
        return ganttLayout;
    }

    public static LocalDateGanttLayout<YearMonth> createYearMonthLocalDateGanttLayout() {
        LocalDateGanttLayout<YearMonth> ganttLayout = new LocalDateGanttLayout<>();
        ganttLayout.setInclusiveChildStartTimeReader(TimeUtil::getFirstDayOfMonth);
        ganttLayout.setInclusiveChildEndTimeReader(TimeUtil::getLastDayOfMonth);
        ganttLayout.setOnTimeWindowChanged((start, end) ->
            ganttLayout.getChildren().setAll(TimeUtil.generateYearMonths(YearMonth.from(start), YearMonth.from(end))));
        return ganttLayout;
    }

    public static LocalDateGanttLayout<YearWeek> createYearWeekLocalDateGanttLayout() {
        LocalDateGanttLayout<YearWeek> ganttLayout = new LocalDateGanttLayout<>();
        ganttLayout.setInclusiveChildStartTimeReader(TimeUtil::getFirstDayOfWeek);
        ganttLayout.setInclusiveChildEndTimeReader(TimeUtil::getLastDayOfWeek);
        ganttLayout.setOnTimeWindowChanged((start, end) ->
            ganttLayout.getChildren().setAll(TimeUtil.generateYearWeeks(YearWeek.from(start), YearWeek.from(end))));
        return ganttLayout;
    }

    public static LocalDateGanttLayout<LocalDate> createDayLocalDateGanttLayout() {
        LocalDateGanttLayout<LocalDate> ganttLayout = new LocalDateGanttLayout<>();
        ganttLayout.setOnTimeWindowChanged((start, end) ->
                ganttLayout.getChildren().setAll(TimeUtil.generateLocalDates(start, end)));
        return ganttLayout;
    }
}
