package dev.webfx.extras.time.format;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * @author Bruno Salmon
 */
public final class LocalizedDateTimeFormat {

    private final LocalizedFormat dateFormat;
    private final LocalizedFormat localizedFormat;

    public LocalizedDateTimeFormat(FormatStyle dateFormatStyle, FormatStyle timeFormatStyle) {
        this(new LocalizedFormat(dateFormatStyle), new LocalizedFormat(timeFormatStyle));
    }

    public LocalizedDateTimeFormat(FormatStyle dateFormatStyle, String timePattern) {
        this(new LocalizedFormat(dateFormatStyle), new LocalizedFormat(timePattern));
    }

    public LocalizedDateTimeFormat(FormatStyle dateFormatStyle, DateTimeFormatter timeFormatter) {
        this(new LocalizedFormat(dateFormatStyle), new LocalizedFormat(timeFormatter));
    }

    public LocalizedDateTimeFormat(String datePattern, FormatStyle timeFormatStyle) {
        this(new LocalizedFormat(datePattern), new LocalizedFormat(timeFormatStyle));
    }

    public LocalizedDateTimeFormat(String datePattern, String timePattern) {
        this(new LocalizedFormat(datePattern), new LocalizedFormat(timePattern));
    }

    public LocalizedDateTimeFormat(String datePattern, DateTimeFormatter timeFormatter) {
        this(new LocalizedFormat(datePattern), new LocalizedFormat(timeFormatter));
    }

    public LocalizedDateTimeFormat(DateTimeFormatter dateFormatter, FormatStyle timeFormatStyle) {
        this(new LocalizedFormat(dateFormatter), new LocalizedFormat(timeFormatStyle));
    }

    public LocalizedDateTimeFormat(DateTimeFormatter dateFormatter, String timePattern) {
        this(new LocalizedFormat(dateFormatter), new LocalizedFormat(timePattern));
    }

    public LocalizedDateTimeFormat(DateTimeFormatter dateFormatter, DateTimeFormatter timeFormatter) {
        this(new LocalizedFormat(dateFormatter), new LocalizedFormat(timeFormatter));
    }

    public LocalizedDateTimeFormat(LocalizedFormat dateFormat, LocalizedFormat localizedFormat) {
        this.dateFormat = dateFormat;
        this.localizedFormat = localizedFormat;
    }

    public LocalizedFormat getDateFormat() {
        return dateFormat;
    }

    public LocalizedFormat getTimeFormat() {
        return localizedFormat;
    }
}
