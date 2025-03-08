package dev.webfx.extras.time.format;

import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.platform.util.Strings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * @author Bruno Salmon
 */
public final class LocalizedTimeFormat {

    private static final ObjectProperty<Locale> localeProperty = new SimpleObjectProperty<>(Locale.getDefault());

    private static DateTimeFormatter getDateFormatter(FormatStyle formatStyle) {
        return DateTimeFormatter
            .ofLocalizedDate(formatStyle)
            .withLocale(getLocale());
    }

    private static DateTimeFormatter getDateTimeFormatter(FormatStyle formatStyle) {
        return DateTimeFormatter
            .ofLocalizedDateTime(formatStyle)
            .withLocale(getLocale());
    }

    private static DateTimeFormatter getTimeFormatter(FormatStyle formatStyle) {
        return DateTimeFormatter
            .ofLocalizedTime(formatStyle)
            .withLocale(getLocale());
    }

    private static ObservableStringValue localeObservableStringValue(Supplier<String> supplier) {
        StringProperty stringProperty = new SimpleStringProperty();
        FXProperties.runNowAndOnPropertyChange(() -> stringProperty.set(supplier.get()), localeProperty);
        return stringProperty;
    }

    public static Locale getLocale() {
        return localeProperty.getValue();
    }

    public static ObjectProperty<Locale> localeProperty() {
        return localeProperty;
    }

    public static void setLocale(Locale locale) {
        localeProperty.setValue(locale);
    }

    public static String formatLocalDate(LocalDate date, FormatStyle formatStyle) {
        return getDateFormatter(formatStyle).format(date);
    }

    public static ObservableStringValue formatLocalDateProperty(LocalDate date, FormatStyle formatStyle) {
        return localeObservableStringValue(() -> formatLocalDate(date, formatStyle));
    }

    public static String formatLocalDateTime(LocalDateTime dateTime, FormatStyle formatStyle) {
        return getDateTimeFormatter(formatStyle).format(dateTime);
    }

    public static ObservableStringValue formatLocalDateTimeProperty(LocalDateTime dateTime, FormatStyle formatStyle) {
        return localeObservableStringValue(() -> formatLocalDateTime(dateTime, formatStyle));
    }

    public static String formatLocalTime(LocalTime time, FormatStyle formatStyle) {
        return getTimeFormatter(formatStyle).format(time);
    }

    public static ObservableStringValue formatLocalTimeProperty(LocalTime time, FormatStyle formatStyle) {
        return localeObservableStringValue(() -> formatLocalTime(time, formatStyle));
    }

    public static String formatMonthDay(MonthDay monthDay, FormatStyle formatStyle) {
        //return monthDay.format(getFormatter(formatStyle)); // raises exception Unsupported field: DayOfWeek
        return clean(formatLocalDate(monthDay.atYear(9999), formatStyle).replace("9999", ""));
    }

    private static String clean(String text) {
        return Strings.removeSuffix(text.trim(), ",");
    }

    public static ObservableStringValue formatMonthDayProperty(MonthDay monthDay, FormatStyle formatStyle) {
        return localeObservableStringValue(() -> formatMonthDay(monthDay, formatStyle));
    }

    public static String formatDayOfWeek(DayOfWeek dayOfWeek, TextStyle textStyle) {
        return dayOfWeek.getDisplayName(textStyle, getLocale());
    }

    public static ObservableStringValue formatDayOfWeekProperty(DayOfWeek dayOfWeek, TextStyle textStyle) {
        return localeObservableStringValue(() -> formatDayOfWeek(dayOfWeek, textStyle));
    }

    public static String formatYearMonth(YearMonth yearMonth, TextStyle textStyle) {
        //return yearMonth.format(getFormatter(formatStyle)); // raises exception Unsupported field: DayOfWeek
        return formatMonth(yearMonth.getMonth(), textStyle) + " " + yearMonth.getYear(); //
    }

    public static ObservableStringValue formatYearMonthProperty(YearMonth yearMonth, TextStyle textStyle) {
        return localeObservableStringValue(() -> formatYearMonth(yearMonth, textStyle));
    }

    public static String formatMonth(Month month, TextStyle textStyle) {
        return month.getDisplayName(textStyle, getLocale());
    }

    public static ObservableStringValue formatMonthProperty(Month month, TextStyle textStyle) {
        return localeObservableStringValue(() -> formatMonth(month, textStyle));
    }

    // Alternative parameters

    public static String formatMonthDay(LocalDate date, FormatStyle formatStyle) {
        return formatMonthDay(MonthDay.of(date.getMonth(), date.getDayOfMonth()), formatStyle);
    }

    public static ObservableStringValue formatMonthDayProperty(LocalDate date, FormatStyle formatStyle) {
        return formatMonthDayProperty(MonthDay.of(date.getMonth(), date.getDayOfMonth()), formatStyle);
    }

}
