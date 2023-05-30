package dev.webfx.extras.time.layout.calendar;

import dev.webfx.extras.time.layout.impl.ChildBounds;
import dev.webfx.extras.time.layout.impl.TimeLayoutBase;
import dev.webfx.extras.time.projector.DayOfWeekProjector;
import dev.webfx.extras.time.projector.TimeProjectorUtil;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * @author Bruno Salmon
 */
public class CalendarLayout<C, T> extends TimeLayoutBase<C, T> {

    public CalendarLayout() {
        setTimeProjector(new DayOfWeekProjector<>(this::getWidth));
        javafx.collections.ObservableList<C> children = null; // This is just to force the WebFX CLI to add the dependency to javafx-base
    }

    @Override
    public void computeChildColumnIndex(ChildBounds<C, T> cb) {
        DayOfWeek dayOfWeek = null;
        T startTime = cb.getStartTime();
        if (startTime instanceof DayOfWeek)
            dayOfWeek = (DayOfWeek) startTime;
        else {
            LocalDate localDate = TimeProjectorUtil.timeToLocalDate(startTime);
            if (localDate != null)
                dayOfWeek = localDate.getDayOfWeek();
        }
        cb.setColumnIndex(dayOfWeek == null ? 0 : TimeProjectorUtil.getDayOfWeekColumn(dayOfWeek));
    }

    @Override
    public void computeChildRowIndex(ChildBounds<C, T> cb) {
        T startTime = cb.getStartTime();
        int rowIndex;
        if (startTime instanceof DayOfWeek || startTime instanceof YearMonth)
            rowIndex = 0;
        else {
            LocalDate localDate = TimeProjectorUtil.timeToLocalDate(startTime);
            int dayOfMonth0 = localDate.getDayOfMonth() - 1; // Starting with 0 for computation convenience
            DayOfWeek dayOfWeek = localDate.getDayOfWeek();
            int dayOfFirstWeek0 = dayOfMonth0 % 7; // Still has same dayOfWeek
            int firstOfMonthColumnShift = (dayOfWeek.ordinal() - dayOfFirstWeek0 + 7 /* to ensure not negative */) % 7;
            rowIndex = (dayOfMonth0 + firstOfMonthColumnShift) / 7;
        }
        cb.setRowIndex(rowIndex);
    }

}
