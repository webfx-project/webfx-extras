package dev.webfx.extras.time.format.spi;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableStringValue;

import java.time.DayOfWeek;
import java.time.Month;
import java.time.YearMonth;

/**
 * @author Bruno Salmon
 */
public interface TimeFormatProvider {

    default String getMonthName(Month month) {
        return month.name();
    }

    default ObservableStringValue monthNameProperty(Month month) {
        return new SimpleStringProperty(getMonthName(month));
    }

    default String getYearMonthName(YearMonth yearMonth) {
        return getMonthName(yearMonth.getMonth()) + " " + yearMonth.getYear();
    }

    default ObservableStringValue yearMonthNameProperty(YearMonth yearMonth) {
        return new SimpleStringProperty(getYearMonthName(yearMonth));
    }

    default String getDayOfWeekName(DayOfWeek dayOfWeek) {
        return dayOfWeek.name();
    }

    default ObservableStringValue dayOfWeekNameProperty(DayOfWeek dayOfWeek) {
        return new SimpleStringProperty(getDayOfWeekName(dayOfWeek));
    }

    default String formatDayAndMonth(int day, Month month) {
        return day + " " + getMonthName(month);
    }

    default ObservableStringValue dayAndMonthProperty(int day, Month month) {
        return new SimpleStringProperty(formatDayAndMonth(day, month));
    }
}
