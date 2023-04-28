package dev.webfx.extras.timelayout.gantt;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

/**
 * @author Bruno Salmon
 */
final class ChildBlock<T extends Temporal> {
    int childIndex;
    final T startTime;
    final T endTime;
    double startX;
    double endX;
    int rowIndex;

    ChildBlock(int childIndex, T startTime, T endTime, double startX, double endX) {
        this.childIndex = childIndex;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startX = startX;
        this.endX = endX;
    }

    boolean intersects(ChildBlock<T> b, boolean canUseX) {
        if (canUseX) { // faster
            if (startX < b.startX)
                return endX > b.startX;
            return b.endX > startX;
        }
        if (startTime.until(b.startTime, ChronoUnit.DAYS) > 0) // If this block starts before b,
            return endTime.until(b.startTime, ChronoUnit.DAYS) < 0; // they overlap if this blocks ends after b starts
        // Otherwise (if this block starts after b)
        return b.endTime.until(startTime, ChronoUnit.DAYS) < 0; // they overlap if b ends after this block starts
    }
}
