package dev.webfx.extras.time.layout.gantt;

import dev.webfx.extras.time.TimeUtil;
import dev.webfx.extras.time.YearWeek;
import dev.webfx.extras.time.layout.impl.TimeProjector;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.function.Function;

/**
 * @author Bruno Salmon
 */
public final class LocalDateGanttLayout<C> extends GanttLayout<C, LocalDate> {

    // Final casts for the fluent API

    @Override
    public LocalDateGanttLayout<C> setChildParentReader(Function<C, ?> childParentReader) {
        return (LocalDateGanttLayout<C>) super.setChildParentReader(childParentReader);
    }

    @Override
    public LocalDateGanttLayout<C> setChildGrandparentReader(Function<C, ?> childGrandparentReader) {
        return (LocalDateGanttLayout<C>) super.setChildGrandparentReader(childGrandparentReader);
    }

    @Override
    public LocalDateGanttLayout<C> setParentWidth(double parentWidth) {
        return (LocalDateGanttLayout<C>) super.setParentWidth(parentWidth);
    }

    @Override
    public LocalDateGanttLayout<C> setGrandparentHeight(double grandparentHeight) {
        return (LocalDateGanttLayout<C>) super.setGrandparentHeight(grandparentHeight);
    }

    @Override
    public LocalDateGanttLayout<C> setTetrisPacking(boolean tetrisPacking) {
        return (LocalDateGanttLayout<C>) super.setTetrisPacking(tetrisPacking);
    }

    @Override
    public LocalDateGanttLayout<C> setInclusiveChildStartTimeReader(Function<C, LocalDate> startTimeReader) {
        return (LocalDateGanttLayout<C>) super.setInclusiveChildStartTimeReader(startTimeReader);
    }

    @Override
    public LocalDateGanttLayout<C> setExclusiveChildStartTimeReader(Function<C, LocalDate> startTimeReader) {
        return (LocalDateGanttLayout<C>) super.setExclusiveChildStartTimeReader(startTimeReader);
    }

    @Override
    public LocalDateGanttLayout<C> setInclusiveChildEndTimeReader(Function<C, LocalDate> endTimeReader) {
        return (LocalDateGanttLayout<C>) super.setInclusiveChildEndTimeReader(endTimeReader);
    }

    @Override
    public LocalDateGanttLayout<C> setExclusiveChildEndTimeReader(Function<C, LocalDate> endTimeReader) {
        return (LocalDateGanttLayout<C>) super.setExclusiveChildEndTimeReader(endTimeReader);
    }

    @Override
    public LocalDateGanttLayout<C> setChildStartTimeReader(Function<C, LocalDate> startTimeReader, boolean exclusive) {
        return (LocalDateGanttLayout<C>) super.setChildStartTimeReader(startTimeReader, exclusive);
    }

    @Override
    public LocalDateGanttLayout<C> setChildEndTimeReader(Function<C, LocalDate> childEndTimeReader, boolean exclusive) {
        return (LocalDateGanttLayout<C>) super.setChildEndTimeReader(childEndTimeReader, exclusive);
    }

    @Override
    public LocalDateGanttLayout<C> setChildFixedHeight(double childFixedHeight) {
        return (LocalDateGanttLayout<C>) super.setChildFixedHeight(childFixedHeight);
    }

    @Override
    public LocalDateGanttLayout<C> setFillHeight(boolean fillHeight) {
        return (LocalDateGanttLayout<C>) super.setFillHeight(fillHeight);
    }

    @Override
    public LocalDateGanttLayout<C> setTopY(double topY) {
        return (LocalDateGanttLayout<C>) super.setTopY(topY);
    }

    @Override
    public LocalDateGanttLayout<C> setHSpacing(double hSpacing) {
        return (LocalDateGanttLayout<C>) super.setHSpacing(hSpacing);
    }

    @Override
    public LocalDateGanttLayout<C> setVSpacing(double vSpacing) {
        return (LocalDateGanttLayout<C>) super.setVSpacing(vSpacing);
    }

    @Override
    public LocalDateGanttLayout<C> setTimeProjector(TimeProjector<LocalDate> timeProjector) {
        return (LocalDateGanttLayout<C>) super.setTimeProjector(timeProjector);
    }

    @Override
    public LocalDateGanttLayout<C> setSelectionEnabled(boolean selectionEnabled) {
        return (LocalDateGanttLayout<C>) super.setSelectionEnabled(selectionEnabled);
    }

    // Static factory methods

    public static LocalDateGanttLayout<Year> createYearLocalDateGanttLayout() {
        LocalDateGanttLayout<Year> ganttLayout = new LocalDateGanttLayout<Year>()
                .setInclusiveChildStartTimeReader(TimeUtil::getFirstDayOfYear)
                .setInclusiveChildEndTimeReader(TimeUtil::getLastDayOfYear);
        ganttLayout.setOnTimeWindowChanged((start, end) ->
            ganttLayout.getChildren().setAll(TimeUtil.generateYears(Year.from(start), Year.from(end))));
        return ganttLayout;
    }

    public static LocalDateGanttLayout<YearMonth> createYearMonthLocalDateGanttLayout() {
        LocalDateGanttLayout<YearMonth> ganttLayout = new LocalDateGanttLayout<YearMonth>()
                .setInclusiveChildStartTimeReader(TimeUtil::getFirstDayOfMonth)
                .setInclusiveChildEndTimeReader(TimeUtil::getLastDayOfMonth);
        ganttLayout.setOnTimeWindowChanged((start, end) ->
            ganttLayout.getChildren().setAll(TimeUtil.generateYearMonths(YearMonth.from(start), YearMonth.from(end))));
        return ganttLayout;
    }

    public static LocalDateGanttLayout<YearWeek> createYearWeekLocalDateGanttLayout() {
        LocalDateGanttLayout<YearWeek> ganttLayout = new LocalDateGanttLayout<YearWeek>()
                .setInclusiveChildStartTimeReader(TimeUtil::getFirstDayOfWeek)
                .setInclusiveChildEndTimeReader(TimeUtil::getLastDayOfWeek);
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