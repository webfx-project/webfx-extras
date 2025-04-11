package dev.webfx.extras.time.format;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * @author Bruno Salmon
 */
public final class LocalizedFormat {

    private final FormatStyle formatStyle;
    private final String pattern;
    private final DateTimeFormatter dateTimeFormatter;

    public LocalizedFormat(FormatStyle formatStyle) {
        this(formatStyle, null, null);
    }

    public LocalizedFormat(String pattern) {
        this(null, pattern, null);
    }

    public LocalizedFormat(DateTimeFormatter dateTimeFormatter) {
        this(null, null, dateTimeFormatter);
    }

    private LocalizedFormat(FormatStyle formatStyle, String pattern, DateTimeFormatter dateTimeFormatter) {
        this.formatStyle = formatStyle;
        this.pattern = pattern;
        this.dateTimeFormatter = dateTimeFormatter;
    }

    public FormatStyle getFormatStyle() {
        return formatStyle;
    }

    public String getPattern() {
        return pattern;
    }

    public DateTimeFormatter getDateTimeFormatter() {
        return dateTimeFormatter;
    }
}
