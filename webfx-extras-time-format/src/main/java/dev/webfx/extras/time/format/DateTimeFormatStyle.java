package dev.webfx.extras.time.format;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * @author Bruno Salmon
 */
public class DateTimeFormatStyle {

    private final FormatStyle dateStyle;
    private final FormatStyle timeStyle;
    private final DateTimeFormatter dateFormatter;
    private final DateTimeFormatter timeFormatter;

    public DateTimeFormatStyle(FormatStyle dateStyle, FormatStyle timeStyle) {
        this(dateStyle, timeStyle, null, null);
    }

    public DateTimeFormatStyle(DateTimeFormatter dateFormatter, DateTimeFormatter timeFormatter) {
        this(null, null, dateFormatter, timeFormatter);
    }

    public DateTimeFormatStyle(FormatStyle dateStyle, DateTimeFormatter timeFormatter) {
        this(dateStyle, null, null, timeFormatter);
    }

    private DateTimeFormatStyle(FormatStyle dateStyle, FormatStyle timeStyle, DateTimeFormatter dateFormatter, DateTimeFormatter timeFormatter) {
        this.dateStyle = dateStyle;
        this.timeStyle = timeStyle;
        this.dateFormatter = dateFormatter;
        this.timeFormatter = timeFormatter;
    }

    public FormatStyle getDateStyle() {
        return dateStyle;
    }

    public FormatStyle getTimeStyle() {
        return timeStyle;
    }

    public DateTimeFormatter getDateFormatter() {
        return dateFormatter;
    }

    public DateTimeFormatter getTimeFormatter() {
        return timeFormatter;
    }
}
