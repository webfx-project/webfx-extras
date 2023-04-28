package dev.webfx.extras.timelayout.bar;

import java.time.LocalDate;

/**
 * @author Bruno Salmon
 */
public final class LocalDateBar<I> extends TimeBar<I, LocalDate> {

    public LocalDateBar(I block, LocalDate startTime, LocalDate endTime) {
        super(block, startTime, endTime);
    }

}
