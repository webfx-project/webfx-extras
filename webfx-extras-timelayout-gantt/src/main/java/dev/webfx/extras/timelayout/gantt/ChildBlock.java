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

    boolean overlaps(ChildBlock<T> other, boolean canUseX) {
        if (canUseX) { // faster if we can use X directly (when this & other block positions are already computed)
            if (startX <= other.startX) // if this block starts before the other block,
                return endX >= other.startX; // it overlaps the other block when its end reaches at least the start of that other block
            else // otherwise (ie if this blocks starts after the other block start),
                return other.endX >= startX;
        }
        // if we can't use X, we use the times instead => there are the same conditions but using time:
        if (startTime.until(other.startTime, ChronoUnit.DAYS) >= 0)
            return endTime.until(other.startTime, ChronoUnit.DAYS) <= 0;
        else
            return other.endTime.until(startTime, ChronoUnit.DAYS) <= 0;
    }
}
