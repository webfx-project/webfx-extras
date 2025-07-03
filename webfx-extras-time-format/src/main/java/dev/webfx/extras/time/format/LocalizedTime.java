package dev.webfx.extras.time.format;

import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.platform.console.Console;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Bruno Salmon
 */
public final class LocalizedTime {

    // Hotfix for Spring Festival 2025 in US timezone (looks like a bug in GWT-time)
    private static final boolean GWT_TIME_DAY_OF_WEEK_BUG_DETECTED = "Thursday".equalsIgnoreCase(DayOfWeek.FRIDAY.getDisplayName(TextStyle.FULL, new Locale("en")));
    private static final Map<String, String> GWT_TIME_DAY_OF_WEEK_BUG_FIX_MAP_FULL = new HashMap<>();
    private static final Map<String, String> GWT_TIME_DAY_OF_WEEK_BUG_FIX_MAP_SHORT = new HashMap<>();
    static {
        Console.log("⚛️⚛️⚛️⚛️⚛️ GWT_TIME_BUG_DETECTED = " + GWT_TIME_DAY_OF_WEEK_BUG_DETECTED);
    }

    private static String fixGwtTime(String format) {
        // Hotfix for Spring Festival 2025 in US timezone (looks like a bug in GWT-time)
        if (GWT_TIME_DAY_OF_WEEK_BUG_DETECTED) {
            String f = format;
            format = fixGwtTime(format, GWT_TIME_DAY_OF_WEEK_BUG_FIX_MAP_FULL);
            if (Objects.equals(f, format)) // Correction must be applied only once
                format = fixGwtTime(format, GWT_TIME_DAY_OF_WEEK_BUG_FIX_MAP_SHORT);
        }
        return format;
    }

    private static String fixGwtTime(String format, Map<String, String> fixMap) {
        for (Map.Entry<String, String> fixEntry : fixMap.entrySet()) {
            String f = format;
            format = format.replace(fixEntry.getKey(), fixEntry.getValue());
            if (!f.equals(format)) { // Correction must be applied only once
                break;
            }
        }
        return format;
    }

    private static final ObjectProperty<Locale> localeProperty = new SimpleObjectProperty<>(Locale.getDefault()) {
        @Override
        protected void invalidated() {
            Locale locale = get();
            GWT_TIME_DAY_OF_WEEK_BUG_FIX_MAP_FULL.clear();
            for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
                GWT_TIME_DAY_OF_WEEK_BUG_FIX_MAP_FULL.put(dayOfWeek.getDisplayName(TextStyle.FULL, locale), dayOfWeek.plus(1).getDisplayName(TextStyle.FULL, locale));
                GWT_TIME_DAY_OF_WEEK_BUG_FIX_MAP_SHORT.put(dayOfWeek.getDisplayName(TextStyle.SHORT, locale), dayOfWeek.plus(1).getDisplayName(TextStyle.SHORT, locale));
            }
        }
    };

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

    public static DateTimeFormatter dateFormatter(FormatStyle dateFormatStyle) {
        return dateFormatter((DateTimeFormatter.ofLocalizedDate(dateFormatStyle)));
    }

    public static DateTimeFormatter dateFormatter(String datePattern) {
        return dateFormatter(DateTimeFormatter.ofPattern(datePattern));
    }

    public static DateTimeFormatter dateFormatter(DateTimeFormatter dateFormatter) {
        return dateFormatter.withLocale(getLocale());
    }

    public static DateTimeFormatter dateFormatter(LocalizedFormat dateFormat) {
        DateTimeFormatter dateTimeFormatter = dateFormat.getDateTimeFormatter();
        if (dateTimeFormatter != null)
            return dateFormatter(dateTimeFormatter);
        FormatStyle formatStyle = dateFormat.getFormatStyle();
        if (formatStyle != null)
            return dateFormatter(formatStyle);
        return dateFormatter(dateFormat.getPattern());
    }

    public static DateTimeFormatter dateFormatter(LocalizedDateTimeFormat dateTimeFormat) {
        return dateFormatter(dateTimeFormat.getDateFormat());
    }

    public static ObservableValue<DateTimeFormatter> dateFormatterProperty(FormatStyle dateFormatStyle) {
        return localeDateTimeFormatterProperty(() -> dateFormatter(dateFormatStyle));
    }

    public static ObservableValue<DateTimeFormatter> dateFormatterProperty(String datePattern) {
        return localeDateTimeFormatterProperty(() -> dateFormatter(datePattern));
    }

    public static ObservableValue<DateTimeFormatter> dateFormatterProperty(DateTimeFormatter dateFormatter) {
        return localeDateTimeFormatterProperty(() -> dateFormatter(dateFormatter));
    }

    public static ObservableValue<DateTimeFormatter> dateFormatterProperty(LocalizedFormat dateFormat) {
        return localeDateTimeFormatterProperty(() -> dateFormatter(dateFormat));
    }

    public static ObservableValue<DateTimeFormatter> dateFormatterProperty(LocalizedDateTimeFormat dateTimeFormat) {
        return localeDateTimeFormatterProperty(() -> dateFormatter(dateTimeFormat));
    }

    // Date & time formatter

    public static DateTimeFormatter dateTimeFormatter(FormatStyle dateTimeFormatStyle) {
        return dateTimeFormatter(DateTimeFormatter.ofLocalizedDateTime(dateTimeFormatStyle));
    }

    public static DateTimeFormatter dateTimeFormatter(String dateTimePattern) {
        return dateTimeFormatter(DateTimeFormatter.ofPattern(dateTimePattern));
    }

    public static DateTimeFormatter dateTimeFormatter(DateTimeFormatter dateTimeFormatter) {
        return dateTimeFormatter.withLocale(getLocale());
    }

    public static DateTimeFormatter dateTimeFormatter(LocalizedFormat dateFormat, LocalizedFormat timeFormat) {
        FormatStyle dateStyle = dateFormat.getFormatStyle();
        FormatStyle timeStyle = timeFormat.getFormatStyle();
        if (dateStyle != null && timeStyle != null)
            return dateTimeFormatter(DateTimeFormatter.ofLocalizedDateTime(dateStyle, timeStyle));
        String datePattern = inferLocalDatePattern(dateFormat, false);
        String timePattern = inferLocalTimePattern(timeFormat, false);
        return dateFormatter(DateTimeFormatter.ofPattern(datePattern + " " + timePattern));
    }

    public static DateTimeFormatter dateTimeFormatter(LocalizedDateTimeFormat dateTimeFormat) {
        return dateTimeFormatter(dateTimeFormat.getDateFormat(), dateTimeFormat.getTimeFormat());
    }

    public static ObservableValue<DateTimeFormatter> dateTimeFormatterProperty(FormatStyle dateTimeFormatStyle) {
        return localeDateTimeFormatterProperty(() -> dateTimeFormatter(dateTimeFormatStyle));
    }

    public static ObservableValue<DateTimeFormatter> dateTimeFormatterProperty(String dateTimePattern) {
        return localeDateTimeFormatterProperty(() -> dateTimeFormatter(dateTimePattern));
    }

    public static ObservableValue<DateTimeFormatter> dateTimeFormatterProperty(DateTimeFormatter dateTimeFormatter) {
        return localeDateTimeFormatterProperty(() -> dateTimeFormatter(dateTimeFormatter));
    }

    public static ObservableValue<DateTimeFormatter> dateTimeFormatterProperty(LocalizedFormat dateFormat, LocalizedFormat timeFormat) {
        return localeDateTimeFormatterProperty(() -> dateTimeFormatter(dateFormat, timeFormat));
    }

    public static ObservableValue<DateTimeFormatter> dateTimeFormatterProperty(LocalizedDateTimeFormat dateTimeFormat) {
        return localeDateTimeFormatterProperty(() -> dateTimeFormatter(dateTimeFormat));
    }

    // Time formatter

    public static DateTimeFormatter timeFormatter(FormatStyle timeFormatStyle) {
        return timeFormatter(DateTimeFormatter.ofLocalizedTime(timeFormatStyle));
    }

    public static DateTimeFormatter timeFormatter(String timePattern) {
        return timeFormatter(DateTimeFormatter.ofPattern(timePattern));
    }

    public static DateTimeFormatter timeFormatter(DateTimeFormatter timeFormatter) {
        return timeFormatter.withLocale(getLocale());
    }

    public static DateTimeFormatter timeFormatter(LocalizedFormat timeFormat) {
        DateTimeFormatter dateTimeFormatter = timeFormat.getDateTimeFormatter();
        if (dateTimeFormatter != null)
            return timeFormatter(dateTimeFormatter);
        FormatStyle formatStyle = timeFormat.getFormatStyle();
        if (formatStyle != null)
            return timeFormatter(formatStyle);
        return timeFormatter(timeFormat.getPattern());
    }

    public static DateTimeFormatter timeFormatter(LocalizedDateTimeFormat dateTimeFormat) {
        return timeFormatter(dateTimeFormat.getTimeFormat());
    }

    public static ObservableValue<DateTimeFormatter> timeFormatterProperty(FormatStyle timeFormatStyle) {
        return localeDateTimeFormatterProperty(() -> timeFormatter(timeFormatStyle));
    }

    public static ObservableValue<DateTimeFormatter> timeFormatterProperty(String timePattern) {
        return localeDateTimeFormatterProperty(() -> timeFormatter(timePattern));
    }

    public static ObservableValue<DateTimeFormatter> timeFormatterProperty(DateTimeFormatter timeFormatter) {
        return localeDateTimeFormatterProperty(() -> timeFormatter(timeFormatter));
    }

    public static ObservableValue<DateTimeFormatter> timeFormatterProperty(LocalizedFormat timeFormat) {
        return localeDateTimeFormatterProperty(() -> timeFormatter(timeFormat));
    }

    public static ObservableValue<DateTimeFormatter> timeFormatterProperty(LocalizedDateTimeFormat dateTimeFormat) {
        return localeDateTimeFormatterProperty(() -> timeFormatter(dateTimeFormat));
    }


    // LocalDate formatting

    public static String formatLocalDate(LocalDate date, FormatStyle dateFormatStyle) {
        return formatLocalDate(date, dateFormatter(dateFormatStyle));
    }

    public static String formatLocalDate(LocalDate date, String datePattern) {
        return formatLocalDate(date, dateFormatter(datePattern));
    }

    public static String formatLocalDate(LocalDate date, DateTimeFormatter dateFormatter) {
        return date == null ? null : date.format(dateFormatter);
    }

    public static String formatLocalDate(LocalDate date, LocalizedFormat dateFormat) {
        return formatLocalDate(date, dateFormatter(dateFormat));
    }

    public static String formatLocalDate(LocalDate date, LocalizedDateTimeFormat dateTimeFormat) {
        return formatLocalDate(date, dateFormatter(dateTimeFormat));
    }

    public static ObservableStringValue formatLocalDateProperty(LocalDate date, FormatStyle dateFormatStyle) {
        return formatLocalDateProperty(date, dateFormatterProperty(dateFormatStyle));
    }

    public static ObservableStringValue formatLocalDateProperty(LocalDate date, String datePattern) {
        return formatLocalDateProperty(date, dateFormatterProperty(datePattern));
    }

    public static ObservableStringValue formatLocalDateProperty(LocalDate date, DateTimeFormatter dateFormatter) {
        return formatLocalDateProperty(date, dateFormatterProperty(dateFormatter));
    }

    public static ObservableStringValue formatLocalDateProperty(LocalDate date, LocalizedFormat dateFormat) {
        return formatLocalDateProperty(date, dateFormatterProperty(dateFormat));
    }

    public static ObservableStringValue formatLocalDateProperty(LocalDate date, LocalizedDateTimeFormat dateTimeFormat) {
        return formatLocalDateProperty(date, dateFormatterProperty(dateTimeFormat));
    }

    public static ObservableStringValue formatLocalDateProperty(LocalDate date, ObservableValue<DateTimeFormatter> dateFormatterProperty) {
        return formatObservableStringValue(dateFormatterProperty, dtf -> {
            String format = date.format(dtf);
            return fixGwtTime(format);
        });
    }


    // LocalDateTime formatting

    public static String formatLocalDateTime(LocalDateTime dateTime, FormatStyle dateTimeFormatStyle) {
        return formatLocalDateTime(dateTime, dateTimeFormatter(dateTimeFormatStyle));
    }

    public static String formatLocalDateTime(LocalDateTime dateTime, String dateTimePattern) {
        return formatLocalDateTime(dateTime, dateTimeFormatter(dateTimePattern));
    }

    public static String formatLocalDateTime(LocalDateTime dateTime, DateTimeFormatter dateTimeFormatter) {
        // May raise an "Unable to extract ZoneId from temporal" exception if the dateTime is not associated with a zone
        return dateTime == null ? null : dateTime.atZone(ZoneId.of("GMT")).format(dateTimeFormatter(dateTimeFormatter)).replace("GMT", "");
    }

    public static String formatLocalDateTime(LocalDateTime dateTime, LocalizedFormat dateFormat, LocalizedFormat timeFormat) {
        return formatLocalDateTime(dateTime, dateTimeFormatter(dateFormat, timeFormat));
    }

    public static String formatLocalDateTime(LocalDateTime dateTime, LocalizedDateTimeFormat dateTimeFormat) {
        return formatLocalDateTime(dateTime, dateTimeFormatter(dateTimeFormat));
    }

    public static ObservableStringValue formatLocalDateTimeProperty(LocalDateTime dateTime, FormatStyle dateTimeFormatStyle) {
        return formatLocalDateTimeProperty(dateTime, dateTimeFormatterProperty(dateTimeFormatStyle));
    }

    public static ObservableStringValue formatLocalDateTimeProperty(LocalDateTime dateTime, String dateTimePattern) {
        return formatLocalDateTimeProperty(dateTime, dateTimeFormatterProperty(dateTimePattern));
    }

    public static ObservableStringValue formatLocalDateTimeProperty(LocalDateTime dateTime, DateTimeFormatter dateTimeFormatter) {
        return formatLocalDateTimeProperty(dateTime, dateTimeFormatterProperty(dateTimeFormatter));
    }

    public static ObservableStringValue formatLocalDateTimeProperty(LocalDateTime dateTime, LocalizedFormat dateFormat, LocalizedFormat timeFormat) {
        return formatLocalDateTimeProperty(dateTime, dateTimeFormatterProperty(dateFormat, timeFormat));
    }

    public static ObservableStringValue formatLocalDateTimeProperty(LocalDateTime dateTime, LocalizedDateTimeFormat dateTimeFormat) {
        return formatLocalDateTimeProperty(dateTime, dateTimeFormatterProperty(dateTimeFormat));
    }

    public static ObservableStringValue formatLocalDateTimeProperty(LocalDateTime dateTime, ObservableValue<DateTimeFormatter> dateTimeFormatterProperty) {
        return formatObservableStringValue(dateTimeFormatterProperty, dateTimeFormatter -> formatLocalDateTime(dateTime, dateTimeFormatter));
    }

    // LocalTime formatting

    public static String formatLocalTime(LocalTime time, FormatStyle timeFormatStyle) {
        return formatLocalTime(time, timeFormatter(timeFormatStyle));
    }

    public static String formatLocalTime(LocalTime time, String timePattern) {
        return formatLocalTime(time, timeFormatter(timePattern));
    }

    public static String formatLocalTime(LocalTime time, DateTimeFormatter timeFormatter) {
        return time == null ? null : time.format(timeFormatter(timeFormatter));
    }

    public static String formatLocalTime(LocalTime time, LocalizedFormat timeFormat) {
        return formatLocalTime(time, timeFormatter(timeFormat));
    }

    public static String formatLocalTime(LocalTime time, LocalizedDateTimeFormat dateTimeFormat) {
        return formatLocalTime(time, timeFormatter(dateTimeFormat));
    }

    public static ObservableStringValue formatLocalTimeProperty(LocalTime time, FormatStyle timeFormatStyle) {
        return formatLocalTimeProperty(time, timeFormatterProperty(timeFormatStyle));
    }

    public static ObservableStringValue formatLocalTimeProperty(LocalTime time, String timePattern) {
        return formatLocalTimeProperty(time, timeFormatterProperty(timePattern));
    }

    public static ObservableStringValue formatLocalTimeProperty(LocalTime time, DateTimeFormatter timeFormatter) {
        return formatLocalTimeProperty(time, timeFormatterProperty(timeFormatter));
    }

    public static ObservableStringValue formatLocalTimeProperty(LocalTime time, LocalizedFormat timeFormat) {
        return formatLocalTimeProperty(time, timeFormatterProperty(timeFormat));
    }

    public static ObservableStringValue formatLocalTimeProperty(LocalTime time, LocalizedDateTimeFormat dateTimeFormat) {
        return formatLocalTimeProperty(time, timeFormatterProperty(dateTimeFormat));
    }

    public static ObservableStringValue formatLocalTimeProperty(LocalTime time, ObservableValue<DateTimeFormatter> timeFormatterProperty) {
        return formatObservableStringValue(timeFormatterProperty, timeFormatter -> formatLocalTime(time, timeFormatter));
    }


    // LocalTime range formatting

    public static String formatLocalTimeRange(LocalTime startTime, LocalTime endTime, FormatStyle timeFormatStyle) {
        return formatLocalTimeRange(startTime, endTime, timeFormatter(timeFormatStyle));
    }

    public static String formatLocalTimeRange(LocalTime startTime, LocalTime endTime, String timePattern) {
        return formatLocalTimeRange(startTime, endTime, timeFormatter(timePattern));
    }

    public static String formatLocalTimeRange(LocalTime startTime, LocalTime endTime, DateTimeFormatter timeFormatter) {
        return formatLocalTime(startTime, timeFormatter) + " - " + formatLocalTime(endTime, timeFormatter);
    }

    public static String formatLocalTimeRange(LocalTime startTime, LocalTime endTime, LocalizedFormat timeFormat) {
        return formatLocalTimeRange(startTime, endTime, timeFormatter(timeFormat));
    }

    public static String formatLocalTimeRange(LocalTime startTime, LocalTime endTime, LocalizedDateTimeFormat dateTimeFormat) {
        return formatLocalTimeRange(startTime, endTime, timeFormatter(dateTimeFormat));
    }

    public static ObservableStringValue formatLocalTimeRangeProperty(LocalTime startTime, LocalTime endTime, FormatStyle timeFormatStyle) {
        return formatLocalTimeRangeProperty(startTime, endTime, timeFormatterProperty(timeFormatStyle));
    }

    public static ObservableStringValue formatLocalTimeRangeProperty(LocalTime startTime, LocalTime endTime, String timePattern) {
        return formatLocalTimeRangeProperty(startTime, endTime, timeFormatterProperty(timePattern));
    }

    public static ObservableStringValue formatLocalTimeRangeProperty(LocalTime startTime, LocalTime endTime, DateTimeFormatter timeFormatter) {
        return formatLocalTimeRangeProperty(startTime, endTime, timeFormatterProperty(timeFormatter));
    }

    public static ObservableStringValue formatLocalTimeRangeProperty(LocalTime startTime, LocalTime endTime, LocalizedFormat timeFormat) {
        return formatLocalTimeRangeProperty(startTime, endTime, timeFormatterProperty(timeFormat));
    }

    public static ObservableStringValue formatLocalTimeRangeProperty(LocalTime startTime, LocalTime endTime, LocalizedDateTimeFormat dateTimeFormat) {
        return formatLocalTimeRangeProperty(startTime, endTime, timeFormatterProperty(dateTimeFormat));
    }

    public static ObservableStringValue formatLocalTimeRangeProperty(LocalTime startTime, LocalTime endTime, ObservableValue<DateTimeFormatter> timeFormatterProperty) {
        return formatObservableStringValue(timeFormatterProperty, timeFormatter -> formatLocalTimeRange(startTime, endTime, timeFormatter));
    }


    // MonthDay formatting (with explicit year) - year is important to have the correct day of the week for formats displaying it

    // Temporarily hardcoded patterns for FormatStyle.SHORT in 5 languages. TODO: should be injected by i18n
    private static final Map<Locale, String> MONTH_DAY_SHORT_PATTERN = Map.of(
        new Locale("en"), "EEE, MMMM d",
        new Locale("de"), "EEE, d. MMMM",
        new Locale("fr"), "EEE d MMMM",
        new Locale("es"), "EEE d 'de' MMMM",
        new Locale("pt"), "EEE, d 'de' MMMM"
    );
    public static String formatMonthDay(MonthDay monthDay, int year, FormatStyle dateFormatStyle) {
        if (dateFormatStyle == FormatStyle.SHORT) {
            String shortPattern = MONTH_DAY_SHORT_PATTERN.get(getLocale());
            if (shortPattern != null) {
                return formatMonthDay(monthDay, year, shortPattern);
            }
        }
        return formatMonthDay(monthDay, year, dateFormatter(dateFormatStyle));
    }

    public static String formatMonthDay(MonthDay monthDay, int year, String datePattern) {
        return formatMonthDay(monthDay, year, dateFormatter(datePattern));
    }

    public static String formatMonthDay(MonthDay monthDay, int year, DateTimeFormatter dateFormatter) {
        return fixGwtTime(clean(formatLocalDate(monthDay.atYear(year), dateFormatter).replace(String.valueOf(year), "")));
    }

    public static String formatMonthDay(MonthDay monthDay, int year, LocalizedFormat dateFormat) {
        return formatMonthDay(monthDay, year, dateFormatter(dateFormat));
    }

    public static String formatMonthDay(MonthDay monthDay, int year, LocalizedDateTimeFormat dateTimeFormat) {
        return formatMonthDay(monthDay, year, dateFormatter(dateTimeFormat));
    }

    public static ObservableStringValue formatMonthDayProperty(MonthDay monthDay, int year, FormatStyle dateFormatStyle) {
        //return formatMonthDayProperty(monthDay, year, dateFormatterProperty(dateFormatStyle));
        return localeObservableStringValue(() -> formatMonthDay(monthDay, year, dateFormatStyle));
    }

    public static ObservableStringValue formatMonthDayProperty(MonthDay monthDay, int year, String datePattern) {
        return formatMonthDayProperty(monthDay, year, dateFormatterProperty(datePattern));
    }

    public static ObservableStringValue formatMonthDayProperty(MonthDay monthDay, int year, DateTimeFormatter dateFormatter) {
        return formatMonthDayProperty(monthDay, year, dateFormatterProperty(dateFormatter));
    }

    public static ObservableStringValue formatMonthDayProperty(MonthDay monthDay, int year, LocalizedFormat dateFormat) {
        return formatMonthDayProperty(monthDay, year, dateFormatterProperty(dateFormat));
    }

    public static ObservableStringValue formatMonthDayProperty(MonthDay monthDay, int year, LocalizedDateTimeFormat dateTimeFormat) {
        return formatMonthDayProperty(monthDay, year, dateFormatterProperty(dateTimeFormat));
    }

    public static ObservableStringValue formatMonthDayProperty(MonthDay monthDay, int year, ObservableValue<DateTimeFormatter> dateFormatterProperty) {
        return formatObservableStringValue(dateFormatterProperty, dateTimeFormatter -> formatMonthDay(monthDay, year, dateTimeFormatter));
    }

    private static String clean(String text) {
        return Strings.removeSuffix(Strings.removeSuffix(text.trim(), ","), " de");
    }

    // MonthDay formatting (with implicit year = this year) - also handy for formats not displaying the day of the week

    public static String formatMonthDay(MonthDay monthDay, FormatStyle dateFormatStyle) {
        return formatMonthDay(monthDay, thisYear(), dateFormatStyle);
    }

    public static String formatMonthDay(MonthDay monthDay, String datePattern) {
        return formatMonthDay(monthDay, thisYear(), datePattern);
    }

    public static String formatMonthDay(MonthDay monthDay, DateTimeFormatter dateFormatter) {
        return formatMonthDay(monthDay, thisYear(), dateFormatter);
    }

    public static String formatMonthDay(MonthDay monthDay, LocalizedFormat dateFormat) {
        return formatMonthDay(monthDay, thisYear(), dateFormat);
    }

    public static String formatMonthDay(MonthDay monthDay, LocalizedDateTimeFormat dateTimeFormat) {
        return formatMonthDay(monthDay, thisYear(), dateTimeFormat);
    }

    public static ObservableStringValue formatMonthDayProperty(MonthDay monthDay, FormatStyle dateFormatStyle) {
        return formatMonthDayProperty(monthDay, thisYear(), dateFormatStyle);
    }

    public static ObservableStringValue formatMonthDayProperty(MonthDay monthDay, String datePattern) {
        return formatMonthDayProperty(monthDay, thisYear(), datePattern);
    }

    public static ObservableStringValue formatMonthDayProperty(MonthDay monthDay, DateTimeFormatter dateFormatter) {
        return formatMonthDayProperty(monthDay, thisYear(), dateFormatter);
    }

    public static ObservableStringValue formatMonthDayProperty(MonthDay monthDay, LocalizedFormat dateFormat) {
        return formatMonthDayProperty(monthDay, thisYear(), dateFormat);
    }

    public static ObservableStringValue formatMonthDayProperty(MonthDay monthDay, LocalizedDateTimeFormat dateTimeFormat) {
        return formatMonthDayProperty(monthDay, thisYear(), dateTimeFormat);
    }

    public static ObservableStringValue formatMonthDayProperty(MonthDay monthDay, ObservableValue<DateTimeFormatter> dateFormatterProperty) {
        return formatMonthDayProperty(monthDay, thisYear(), dateFormatterProperty);
    }

    private static int thisYear() {
        return LocalDate.now().getYear();
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
        return yearMonth == null ? null : formatMonth(yearMonth.getMonth(), textStyle) + " " + yearMonth.getYear(); //
    }

    public static ObservableStringValue formatYearMonthProperty(YearMonth yearMonth, TextStyle textStyle) {
        return localeObservableStringValue(() -> formatYearMonth(yearMonth, textStyle));
    }

    // Infer date pattern

    public static String inferLocalDatePattern(String datePattern, boolean ui) {
        if (ui) {
            Character d = null, y = null;
            Locale locale = getLocale();
            switch (locale.getLanguage().toLowerCase()) {
                case "fr":
                    d = 'j';
                case "es":
                case "pt":
                    y = 'a';
                    break;
            }
            if (d != null)
                datePattern = datePattern.replace('d', d);
            if (y != null)
                datePattern = datePattern.replace('y', y);
        }
        return datePattern;
    }

    public static String inferLocalDatePattern(FormatStyle dateFormatStyle, boolean ui) {
        return inferLocalDatePattern(dateFormatter(dateFormatStyle), ui);
    }

    public static String inferLocalDatePattern(LocalizedFormat dateFormat, boolean ui) {
        String pattern = dateFormat.getPattern();
        if (pattern != null)
            return inferLocalDatePattern(pattern, ui);
        return inferLocalDatePattern(dateFormatter(dateFormat), ui);
    }

    public static String inferLocalDatePattern(DateTimeFormatter dateFormatter, boolean ui) {
        LocalDate sampleDate = LocalDate.of(2025, 11, 28);
        Month month = sampleDate.getMonth();
        DayOfWeek dayOfWeek = sampleDate.getDayOfWeek();
        String pattern = sampleDate.format(dateFormatter)
            .replace(formatMonth(month, TextStyle.FULL), "MMMM")
            .replace(formatMonth(month, TextStyle.FULL_STANDALONE), "MMMM")
            .replace(formatMonth(month, TextStyle.SHORT), "MMM")
            .replace(formatMonth(month, TextStyle.SHORT_STANDALONE), "MMM")
            .replace(formatDayOfWeek(dayOfWeek, TextStyle.FULL), "EEEE")
            .replace(formatDayOfWeek(dayOfWeek, TextStyle.FULL_STANDALONE), "EEEE")
            .replace(formatDayOfWeek(dayOfWeek, TextStyle.SHORT), "EEE")
            .replace(formatDayOfWeek(dayOfWeek, TextStyle.SHORT_STANDALONE), "EEE")
            .replace("2025", "yyyy")
            .replace("25", "yy")
            .replace("11", "MM")
            .replace("28", "dd")
            ;
        return inferLocalDatePattern(pattern, ui);
    }

    public static ObservableStringValue inferLocalDatePatternProperty(ObservableValue<DateTimeFormatter> dateFormatterProperty, boolean forUserDisplay) {
        return formatObservableStringValue(dateFormatterProperty, dateTimeFormatter ->  inferLocalDatePattern(dateTimeFormatter, forUserDisplay));
    }

    // Infer time pattern

    public static String inferLocalTimePattern(String pattern, boolean ui) {
        return pattern;
    }

    public static String inferLocalTimePattern(FormatStyle timeFormatStyle, boolean ui) {
        return inferLocalTimePattern(timeFormatter(timeFormatStyle), ui);
    }

    public static String inferLocalTimePattern(LocalizedFormat timeFormat, boolean ui) {
        String pattern = timeFormat.getPattern();
        if (pattern != null)
            return inferLocalTimePattern(pattern, ui);
        return inferLocalTimePattern(timeFormatter(timeFormat), ui);
    }

    public static String inferLocalTimePattern(DateTimeFormatter timeFormatter, boolean ui) {
        LocalTime sampleTime = LocalTime.of(22, 55, 44);
        String pattern = sampleTime.format(timeFormatter)
            .replace("22", "HH")
            .replace("55", "mm")
            .replace("44", "ss")
            ;
        return inferLocalTimePattern(pattern, ui);
    }

    // Shorthand methods with alternative parameter types

    public static String formatMonthDay(LocalDate date, FormatStyle dateFormatStyle) {
        return formatMonthDay(MonthDay.of(date.getMonth(), date.getDayOfMonth()), date.getYear(), dateFormatStyle);
    }

    public static ObservableStringValue formatMonthDayProperty(LocalDate date, FormatStyle dateFormatStyle) {
        return formatMonthDayProperty(MonthDay.of(date.getMonth(), date.getDayOfMonth()), date.getYear(), dateFormatStyle);
    }

    public static String formatLocalDate(LocalDateTime dateTime, FormatStyle dateFormatStyle) {
        return formatLocalDate(dateTime.toLocalDate(), dateFormatStyle);
    }

    public static ObservableStringValue formatLocalDateProperty(LocalDateTime dateTime, FormatStyle dateFormatStyle) {
        return formatLocalDateProperty(dateTime.toLocalDate(), dateFormatStyle);
    }

    public static ObservableStringValue formatLocalDateTimeProperty(LocalDate date, LocalTime time, FormatStyle dateTimeFormatStyle) {
        if (time == null)
            return formatLocalDateProperty(date, dateTimeFormatStyle);
        if (date == null)
            return formatLocalTimeProperty(time, dateTimeFormatStyle);
        return formatLocalDateTimeProperty(LocalDateTime.of(date, time), dateTimeFormatStyle);
    }

    public static String formatLocalTimeRange(LocalDate date, LocalTime time, FormatStyle dateTimeFormatStyle) {
        if (time == null)
            return formatLocalDate(date, dateTimeFormatStyle);
        if (date == null)
            return formatLocalTime(time, dateTimeFormatStyle);
        return formatLocalDateTime(LocalDateTime.of(date, time), dateTimeFormatStyle);
    }

    public static LocalTime durationToLocalTime(Duration duration) {
        return LocalTime.ofNanoOfDay(duration.getNano());
    }


    // Parsing API

    public static LocalDate parseLocalDate(String text, FormatStyle dateFormatStyle) {
        return parseLocalDate(text, dateFormatter(dateFormatStyle));
    }

    public static LocalDate parseLocalDate(String text, String datePattern) {
        return parseLocalDate(text, dateFormatter(datePattern));
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

    public static StringConverter<LocalDate> dateStringConverter(FormatStyle dateFormatStyle) {
        return dateStringConverter(dateFormatter(dateFormatStyle));
    }

    public static StringConverter<LocalDate> dateStringConverter(String datePattern) {
        return dateStringConverter(dateFormatter(datePattern));
    }

    public static StringConverter<LocalDate> dateStringConverter(DateTimeFormatter dateTimeFormatter) {
        return new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return formatLocalDate(date, dateTimeFormatter);
            }

            @Override
            public LocalDate fromString(String text) {
                return parseLocalDate(text, dateTimeFormatter, false);
            }
        };
    }

}
