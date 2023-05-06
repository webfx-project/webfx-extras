package dev.webfx.extras.time.layout.bar;

import java.time.LocalDate;

/**
 * @author Bruno Salmon
 */
public final class LocalDateBar<I> extends TimeBar<I, LocalDate> {

    public LocalDateBar(I instance, LocalDate startTime, LocalDate endTime) {
        super(instance, startTime, endTime);
    }

}
