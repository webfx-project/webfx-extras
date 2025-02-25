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

    default String formatMonth(Month month) {
        return month.name();
    }

    default ObservableStringValue formatMonthProperty(Month month) {
        return new SimpleStringProperty(formatMonth(month));
    }

    default String formatYearMonth(YearMonth yearMonth) {
        return formatMonth(yearMonth.getMonth()) + " " + yearMonth.getYear();
    }

    default ObservableStringValue formatYearMonthProperty(YearMonth yearMonth) {
        return new SimpleStringProperty(formatYearMonth(yearMonth));
    }

    default String formatDayOfWeek(DayOfWeek dayOfWeek) {
        return dayOfWeek.name();
    }

    default ObservableStringValue formatDayOfWeekProperty(DayOfWeek dayOfWeek) {
        return new SimpleStringProperty(formatDayOfWeek(dayOfWeek));
    }

    default String formatDayMonth(int day, Month month) {
        return day + " " + formatMonth(month);
    }

    default ObservableStringValue formatDayMonthProperty(int day, Month month) {
        return new SimpleStringProperty(formatDayMonth(day, month));
    }
}
