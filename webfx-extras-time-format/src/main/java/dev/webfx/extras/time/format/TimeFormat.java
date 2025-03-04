package dev.webfx.extras.time.format;

import dev.webfx.extras.time.format.spi.TimeFormatProvider;
import dev.webfx.extras.time.format.spi.impl.TimeFormatProviderImpl;
import dev.webfx.platform.service.SingleServiceProvider;
import javafx.beans.value.ObservableStringValue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.ServiceLoader;

/**
 * @author Bruno Salmon
 */
public final class TimeFormat {

    private TimeFormat() {}

    private static final TimeFormatProvider PROVIDER;

    static {
        TimeFormatProvider provider = SingleServiceProvider.getProvider(TimeFormatProvider.class, () -> ServiceLoader.load(TimeFormatProvider.class), SingleServiceProvider.NotFoundPolicy.RETURN_NULL);
        if (provider == null)
            provider = new TimeFormatProviderImpl();
        PROVIDER = provider;
    }

    private static TimeFormatProvider getProvider() {
        return PROVIDER;
    }

    public static String formatMonth(Month month) {
        return getProvider().formatMonth(month);
    }

    public static ObservableStringValue formatMonthProperty(Month month) {
        return getProvider().formatMonthProperty(month);
    }

    public static String formatYearMonth(YearMonth yearMonth) {
        return getProvider().formatYearMonth(yearMonth);
    }

    public static ObservableStringValue formatYearMonthProperty(YearMonth yearMonth) {
        return getProvider().formatYearMonthProperty(yearMonth);
    }


    public static String formatDayOfWeek(DayOfWeek dayOfWeek) {
        return getProvider().formatDayOfWeek(dayOfWeek);
    }

    public static ObservableStringValue formatDayOfWeekProperty(DayOfWeek dayOfWeek) {
        return getProvider().formatDayOfWeekProperty(dayOfWeek);
    }

    public static String formatDayMonth(int day, Month month) {
        return getProvider().formatDayMonth(day, month);
    }

    public static ObservableStringValue formatDayMonthProperty(int day, Month month) {
        return getProvider().formatDayMonthProperty(day, month);
    }

    public static String formatDayMonth(LocalDate date) {
        return formatDayMonth(date.getDayOfMonth(), date.getMonth());
    }

    public static ObservableStringValue formatDayMonthProperty(LocalDate date) {
        return formatDayMonthProperty(date.getDayOfMonth(), date.getMonth());
    }


}
