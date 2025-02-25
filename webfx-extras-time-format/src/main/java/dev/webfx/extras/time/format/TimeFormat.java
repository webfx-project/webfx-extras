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

    public static String getMonthName(Month month) {
        return getProvider().getMonthName(month);
    }

    public static ObservableStringValue monthNameProperty(Month month) {
        return getProvider().monthNameProperty(month);
    }

    public static String getYearMonthName(YearMonth yearMonth) {
        return getProvider().getYearMonthName(yearMonth);
    }

    public static ObservableStringValue yearMonthNameProperty(YearMonth yearMonth) {
        return getProvider().yearMonthNameProperty(yearMonth);
    }


    public static String getDayOfWeekName(DayOfWeek dayOfWeek) {
        return getProvider().getDayOfWeekName(dayOfWeek);
    }

    public static ObservableStringValue dayOfWeekNameProperty(DayOfWeek dayOfWeek) {
        return getProvider().dayOfWeekNameProperty(dayOfWeek);
    }

    public static String formatDayAndMonth(int day, Month month) {
        return getProvider().formatDayAndMonth(day, month);
    }

    public static ObservableStringValue dayAndMonthProperty(int day, Month month) {
        return getProvider().dayAndMonthProperty(day, month);
    }

    public static String formatDayAndMonth(LocalDate date) {
        return formatDayAndMonth(date.getDayOfMonth(), date.getMonth());
    }

    public static ObservableStringValue dayAndMonthProperty(LocalDate date) {
        return dayAndMonthProperty(date.getDayOfMonth(), date.getMonth());
    }


}
