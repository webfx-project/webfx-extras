package dev.webfx.extras.time.format;

import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.platform.util.Strings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.util.StringConverter;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Bruno Salmon
 */
public final class LocalizedTime {

    private static final ObjectProperty<Locale> localeProperty = new SimpleObjectProperty<>(Locale.getDefault());

    public static Locale getLocale() {
        return localeProperty.getValue();
    }

    public static ObjectProperty<Locale> localeProperty() {
        return localeProperty;
    }

    public static void setLocale(Locale locale) {
        localeProperty.setValue(locale);
    }

    private static ObservableStringValue localeObservableStringValue(Supplier<String> supplier) {
        StringProperty stringProperty = new SimpleStringProperty();
        FXProperties.runNowAndOnPropertyChange(() -> stringProperty.set(supplier.get()), localeProperty);
        return stringProperty;
    }

    private static ObservableValue<DateTimeFormatter> localeDateTimeFormatterProperty(Supplier<DateTimeFormatter> supplier) {
        ObjectProperty<DateTimeFormatter> formatterProperty = new SimpleObjectProperty<>();
        FXProperties.runNowAndOnPropertyChange(() -> formatterProperty.set(supplier.get()), localeProperty);
        return formatterProperty;
    }

    private static ObservableStringValue formatObservableStringValue(ObservableValue<DateTimeFormatter> formatterProperty, Function<DateTimeFormatter, String> formatFunction) {
        StringProperty stringProperty = new SimpleStringProperty();
        FXProperties.runNowAndOnPropertyChange(dateTimeFormatter -> stringProperty.set(formatFunction.apply(dateTimeFormatter)), formatterProperty);
        return stringProperty;
    }

    // Date formatter

    public static DateTimeFormatter dateFormatter(FormatStyle formatStyle) {
        return dateFormatter((DateTimeFormatter.ofLocalizedDate(formatStyle)));
    }

    public static DateTimeFormatter dateFormatter(DateTimeFormatter dateFormatter) {
        return dateFormatter.withLocale(getLocale());
    }

    public static ObservableValue<DateTimeFormatter> dateFormatterProperty(FormatStyle formatStyle) {
        return localeDateTimeFormatterProperty(() -> dateFormatter(formatStyle));
    }

    public static ObservableValue<DateTimeFormatter> dateFormatterProperty(DateTimeFormatter dateFormatter) {
        return localeDateTimeFormatterProperty(() -> dateFormatter(dateFormatter));
    }


    // Date & time formatter

    public static DateTimeFormatter dateTimeFormatter(FormatStyle formatStyle) {
        return dateTimeFormatter(DateTimeFormatter.ofLocalizedDateTime(formatStyle));
    }

    public static DateTimeFormatter dateTimeFormatter(DateTimeFormatter dateTimeFormatter) {
        return dateTimeFormatter.withLocale(getLocale());
    }

    public static ObservableValue<DateTimeFormatter> dateTimeFormatterProperty(FormatStyle formatStyle) {
        return localeDateTimeFormatterProperty(() -> dateTimeFormatter(formatStyle));
    }

    public static ObservableValue<DateTimeFormatter> dateTimeFormatterProperty(DateTimeFormatter dateTimeFormatter) {
        return localeDateTimeFormatterProperty(() -> dateTimeFormatter(dateTimeFormatter));
    }


    // Time formatter

    public static DateTimeFormatter timeFormatter(FormatStyle formatStyle) {
        return timeFormatter(DateTimeFormatter.ofLocalizedTime(formatStyle));
    }

    public static DateTimeFormatter timeFormatter(DateTimeFormatter timeFormatter) {
        return timeFormatter.withLocale(getLocale());
    }

    public static ObservableValue<DateTimeFormatter> timeFormatterProperty(FormatStyle formatStyle) {
        return localeDateTimeFormatterProperty(() -> timeFormatter(formatStyle));
    }

    public static ObservableValue<DateTimeFormatter> timeFormatterProperty(DateTimeFormatter timeFormatter) {
        return localeDateTimeFormatterProperty(() -> timeFormatter(timeFormatter));
    }


    // LocalDate formatting

    public static String formatLocalDate(LocalDate date, FormatStyle formatStyle) {
        return formatLocalDate(date, dateFormatter(formatStyle));
    }

    public static String formatLocalDate(LocalDate date, DateTimeFormatter dateTimeFormatter) {
        return date.format(dateTimeFormatter);
    }

    public static ObservableStringValue formatLocalDateProperty(LocalDate date, FormatStyle formatStyle) {
        return formatLocalDateProperty(date, dateFormatterProperty(formatStyle));
    }

    public static ObservableStringValue formatLocalDateProperty(LocalDate date, ObservableValue<DateTimeFormatter> dateFormatterProperty) {
        return formatObservableStringValue(dateFormatterProperty, date::format);
    }


    // LocalDateTime formatting

    public static String formatLocalDateTime(LocalDateTime dateTime, FormatStyle formatStyle) {
        return formatLocalDateTime(dateTime, dateTimeFormatter(formatStyle));
    }

    public static String formatLocalDateTime(LocalDateTime dateTime, DateTimeFormatter dateTimeFormatter) {
        return dateTime.format(dateTimeFormatter);
    }

    public static ObservableStringValue formatLocalDateTimeProperty(LocalDateTime dateTime, FormatStyle formatStyle) {
        return formatLocalDateTimeProperty(dateTime, dateTimeFormatterProperty(formatStyle));
    }

    public static ObservableStringValue formatLocalDateTimeProperty(LocalDateTime dateTime, ObservableValue<DateTimeFormatter> dateTimeFormatterProperty) {
        return formatObservableStringValue(dateTimeFormatterProperty, dateTime::format);
    }


    // LocalTime formatting

    public static String formatLocalTime(LocalTime time, FormatStyle formatStyle) {
        return formatLocalTime(time, timeFormatter(formatStyle));
    }

    public static String formatLocalTime(LocalTime time, DateTimeFormatter timeFormatter) {
        return time.format(timeFormatter);
    }

    public static ObservableStringValue formatLocalTimeProperty(LocalTime time, FormatStyle formatStyle) {
        return formatLocalTimeProperty(time, timeFormatterProperty(formatStyle));
    }

    public static ObservableStringValue formatLocalTimeProperty(LocalTime time, ObservableValue<DateTimeFormatter> timeFormatterProperty) {
        return formatObservableStringValue(timeFormatterProperty, time::format);
    }


    // MonthDay formatting

    public static String formatMonthDay(MonthDay monthDay, FormatStyle formatStyle) {
        return formatMonthDay(monthDay, dateFormatter(formatStyle));
    }

    public static String formatMonthDay(MonthDay monthDay, DateTimeFormatter dateFormatter) {
        return clean(formatLocalDate(monthDay.atYear(9999), dateFormatter).replace("9999", ""));
    }

    private static String clean(String text) {
        return Strings.removeSuffix(text.trim(), ",");
    }

    public static ObservableStringValue formatMonthDayProperty(MonthDay monthDay, FormatStyle formatStyle) {
        return formatMonthDayProperty(monthDay, dateFormatterProperty(formatStyle));
    }

    public static ObservableStringValue formatMonthDayProperty(MonthDay monthDay, ObservableValue<DateTimeFormatter> dateFormatterProperty) {
        return formatObservableStringValue(dateFormatterProperty, dateTimeFormatter -> formatMonthDay(monthDay, dateTimeFormatter));
    }


    // DayOfWeek formatting

    public static String formatDayOfWeek(DayOfWeek dayOfWeek, TextStyle textStyle) {
        return dayOfWeek.getDisplayName(textStyle, getLocale());
    }

    public static ObservableStringValue formatDayOfWeekProperty(DayOfWeek dayOfWeek, TextStyle textStyle) {
        return localeObservableStringValue(() -> formatDayOfWeek(dayOfWeek, textStyle));
    }


    // Month formatting

    public static String formatMonth(Month month, TextStyle textStyle) {
        return month.getDisplayName(textStyle, getLocale());
    }

    public static ObservableStringValue formatMonthProperty(Month month, TextStyle textStyle) {
        return localeObservableStringValue(() -> formatMonth(month, textStyle));
    }


    // YearMonth formatting

    public static String formatYearMonth(YearMonth yearMonth, TextStyle textStyle) {
        //return yearMonth.format(getFormatter(formatStyle)); // raises exception Unsupported field: DayOfWeek
        return formatMonth(yearMonth.getMonth(), textStyle) + " " + yearMonth.getYear(); //
    }

    public static ObservableStringValue formatYearMonthProperty(YearMonth yearMonth, TextStyle textStyle) {
        return localeObservableStringValue(() -> formatYearMonth(yearMonth, textStyle));
    }

    //

    public static String inferLocalDatePattern(DateTimeFormatter dateTimeFormatter) {
        LocalDate sampleDate = LocalDate.of(2025, 11, 28);
        String format = sampleDate.format(dateTimeFormatter)
            .replace("2025", "yyyy")
            .replace("25", "yy")
            .replace("11", "MM")
            .replace("28", "dd");
        Character d = null, y = null;
        Locale locale = getLocale();
        switch (locale.getLanguage().toLowerCase()) {
            case "fr": d = 'j';
            case "es":
            case "pt":
                y = 'a';
                break;
        }
        if (d != null)
            format = format.replace('d', d);
        if (y != null)
            format = format.replace('y', y);
        return format;
    }

    public static ObservableStringValue inferLocalDatePatternProperty(ObservableValue<DateTimeFormatter> dateFormatterProperty) {
        return formatObservableStringValue(dateFormatterProperty, LocalizedTime::inferLocalDatePattern);
    }

    // Shorthand methods with alternative parameter types

    public static String formatMonthDay(LocalDate date, FormatStyle formatStyle) {
        return formatMonthDay(MonthDay.of(date.getMonth(), date.getDayOfMonth()), formatStyle);
    }

    public static ObservableStringValue formatMonthDayProperty(LocalDate date, FormatStyle formatStyle) {
        return formatMonthDayProperty(MonthDay.of(date.getMonth(), date.getDayOfMonth()), formatStyle);
    }

    public static String formatLocalDate(LocalDateTime dateTime, FormatStyle formatStyle) {
        return formatLocalDate(dateTime.toLocalDate(), formatStyle);
    }

    public static ObservableStringValue formatLocalDateProperty(LocalDateTime dateTime, FormatStyle formatStyle) {
        return formatLocalDateProperty(dateTime.toLocalDate(), formatStyle);
    }

    public static ObservableStringValue formatLocalDateTimeProperty(LocalDate date, LocalTime time, FormatStyle formatStyle) {
        if (time == null)
            return formatLocalDateProperty(date, formatStyle);
        if (date == null)
            return formatLocalTimeProperty(time, formatStyle);
        return formatLocalDateTimeProperty(LocalDateTime.of(date, time), formatStyle);
    }

    public static String formatLocalTime(LocalDate date, LocalTime time, FormatStyle formatStyle) {
        if (time == null)
            return formatLocalDate(date, formatStyle);
        if (date == null)
            return formatLocalTime(time, formatStyle);
        return formatLocalDateTime(LocalDateTime.of(date, time), formatStyle);
    }


    // Parsing API

    public static LocalDate parseLocalDate(String text, FormatStyle formatStyle) {
        return parseLocalDate(text, dateFormatter(formatStyle));
    }

    public static LocalDate parseLocalDate(String text, DateTimeFormatter dateFormatter) {
        return parseLocalDate(text, dateFormatter, true);
    }

    public static LocalDate parseLocalDate(String text, DateTimeFormatter dateFormatter, boolean silent) {
        try {
            return LocalDate.parse(text, dateFormatter);
        } catch (Exception e) {
            if (silent)
                return null;
            throw e;
        }
    }

    public static void bindLocalDateTextProperty(StringProperty textProperty, ObjectProperty<LocalDate> dateProperty, ObservableValue<DateTimeFormatter> dateFormatterProperty) {
        FXProperties.runNowAndOnPropertiesChange(() -> {
            LocalDate date = dateProperty.get();
            textProperty.set(date == null ? null : date.format(dateFormatterProperty.getValue()));
        }, dateProperty, dateFormatterProperty);
        FXProperties.runOnPropertyChange(text -> {
            LocalDate date = parseLocalDate(text, dateFormatterProperty.getValue(), true);
            if (date != null || text == null || text.isBlank())
                dateProperty.set(date);
        }, textProperty);
    }

    public static StringConverter<LocalDate> dateStringConverter(FormatStyle formatStyle) {
        return dateStringConverter(dateFormatter(formatStyle));
    }

    public static StringConverter<LocalDate> dateStringConverter(DateTimeFormatter dateTimeFormatter) {
        return new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return dateTimeFormatter.format(date);
            }

            @Override
            public LocalDate fromString(String text) {
                return parseLocalDate(text, dateTimeFormatter, false);
            }
        };
    }

}
