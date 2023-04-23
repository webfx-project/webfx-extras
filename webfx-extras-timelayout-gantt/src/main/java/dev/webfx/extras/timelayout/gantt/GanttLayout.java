package dev.webfx.extras.timelayout.gantt;

import dev.webfx.extras.timelayout.impl.TimeLayoutBase;
import dev.webfx.extras.timelayout.impl.TimeProjector;
import dev.webfx.extras.timelayout.util.TimeUtil;
import dev.webfx.extras.timelayout.util.YearWeek;
import javafx.collections.ListChangeListener;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.*;

/**
 * @author Bruno Salmon
 */
public final class GanttLayout <C, T extends Temporal> extends TimeLayoutBase<C, T> implements TimeProjector<T> {

    private final Map<C, Block<T>> cache = new HashMap<>();
    private final List<List<Block<T>>> packedRows = new ArrayList<>();
    private boolean cacheCleaningRequired;
    private boolean emptyRowsRemovalRequired;

    @Override
    protected void onChildrenChanged(ListChangeListener.Change<? extends C> c) {
        super.onChildrenChanged(c);
        cacheCleaningRequired = true;
    }

    @Override
    protected TimeProjector<T> getTimeProjector() {
        return this;
    }

    @Override
    public double timeToX(T time, boolean start, boolean exclusive, double layoutWidth) {
        T timeWindowStart = getTimeWindowStart();
        T timeWindowEnd = getTimeWindowEnd();
        if (timeWindowStart == null || timeWindowEnd == null)
            return 0;
        long totalDays = timeWindowStart.until(timeWindowEnd, ChronoUnit.DAYS) + 1;
        long daysToTime = timeWindowStart.until(time, ChronoUnit.DAYS);
        if (start && exclusive || !start && !exclusive)
            daysToTime++;
        return layoutWidth * daysToTime / totalDays;
    }

    @Override
    protected int computeChildColumnIndex(int childIndex, C child, T startTime, T endTime, double startX, double endX) {
        return 0;
    }

    @Override
    protected int computeChildRowIndex(int childIndex, C child, T startTime, T endTime, double startX, double endX) {
        if (child instanceof Temporal || child instanceof YearWeek)
            return 0;
        // Tetris block algorithm:
        if (childIndex == 0) { // => means that this is the first call of a new pass over the children
            cleanCacheIfRequired();
            removeEmptyRowsIfRequired();
        }
        Block<T> block = cache.get(child);
        if (block != null && Objects.equals(block.startTime, startTime) && Objects.equals(block.endTime, endTime)) {
            block.startX = startX;
            block.endX = endX;
            return block.rowIndex;
        }
        if (block != null) { // The block already existed but has moved! So we need to remove it now
            List<Block<T>> row = packedRows.get(block.rowIndex);
            row.remove(block);
            if (row.isEmpty())
                emptyRowsRemovalRequired = true;
        }
        Block<T> newBlock = new Block<>(childIndex, startTime, endTime, startX, endX);
        cache.put(child, newBlock);
        loop: for (int rowIndex = 0; rowIndex < packedRows.size(); rowIndex++) {
            List<Block<T>> row = packedRows.get(rowIndex);
            for (Block<T> b : row) {
                if (newBlock.intersects(b, layoutWidth > 0 && b.childIndex < childIndex))
                    continue loop;
            }
            newBlock.rowIndex = rowIndex;
            row.add(newBlock);
            return rowIndex;
        }
        return createNewRow(newBlock);
    }

    private int createNewRow(Block<T> block) {
        List<Block<T>> row = new ArrayList<>();
        row.add(block);
        block.rowIndex = packedRows.size();
        packedRows.add(row);
        return block.rowIndex;
    }

    // Note: getRowsCount() can be called when child positions are still invalid, so at this point packedRows is not
    // up-to-date.
    @Override
    public int getRowsCount() {
        if (cacheCleaningRequired) // happens when children have just been modified, but their position is still invalid,
            return super.getRowsCount(); // so we call the default implementation to update these positions (this will
        // update packedRows in the process through the successive calls to computeChildRowIndex()).
        // Otherwise, packedRows is up-to-date when reaching this point,
        removeEmptyRowsIfRequired(); // we might eventually need to do a last removal of empty rows
        return packedRows.size(); // and then, we can just return the number of packed rows (much faster).
    }

    private void cleanCacheIfRequired() {
        if (!cacheCleaningRequired)
            return;
        // Cleaning cache from children that are no longer listed
        for (Iterator<Map.Entry<C, Block<T>>> it = cache.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<C, Block<T>> entry = it.next();
            C cacheChild = entry.getKey();
            Block<T> cacheBlock = entry.getValue();
            // Fast check (when the child index remains the same)
            if (cacheBlock.childIndex < children.size() && children.get(cacheBlock.childIndex) == cacheChild)
                continue;
            // Slower check (when the child index changed)
            int newCacheChildIndex = children.indexOf(cacheChild);
            if (newCacheChildIndex >= 0) {
                cacheBlock.childIndex = newCacheChildIndex;
                continue;
            }
            it.remove();
            List<Block<T>> row = packedRows.get(cacheBlock.rowIndex);
            row.remove(cacheBlock);
            if (row.isEmpty())
                emptyRowsRemovalRequired = true;
        }
        cacheCleaningRequired = false;
    }

    private void removeEmptyRowsIfRequired() {
        if (!emptyRowsRemovalRequired)
            return;
        int removedRows = 0;
        for (int rowIndex = 0; rowIndex < packedRows.size(); rowIndex++) {
            List<Block<T>> row = packedRows.get(rowIndex);
            if (row.isEmpty()) {
                packedRows.remove(rowIndex--);
                removedRows++;
            } else if (removedRows > 0) {
                for (Block<T> block : row) block.rowIndex -= removedRows;
            }
        }
        emptyRowsRemovalRequired = false;
    }

    private static final class Block<T extends Temporal> {
        private int childIndex;
        private final T startTime;
        private final T endTime;
        private double startX;
        private double endX;
        private int rowIndex;

        private Block(int childIndex, T startTime, T endTime, double startX, double endX) {
            this.childIndex = childIndex;
            this.startTime = startTime;
            this.endTime = endTime;
            this.startX = startX;
            this.endX = endX;
        }

        private boolean intersects(Block<T> b, boolean canUseX) {
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

    // Static factory methods

    public static GanttLayout<Year, LocalDate> createYearLocalDateGanttLayout() {
        GanttLayout<Year, LocalDate> ganttLayout = new GanttLayout<>();
        ganttLayout.setInclusiveChildStartTimeReader(TimeUtil::getFirstDayOfYear);
        ganttLayout.setInclusiveChildEndTimeReader(TimeUtil::getLastDayOfYear);
        return ganttLayout;
    }

    public static GanttLayout<YearMonth, LocalDate> createYearMonthLocalDateGanttLayout() {
        GanttLayout<YearMonth, LocalDate> ganttLayout = new GanttLayout<>();
        ganttLayout.setInclusiveChildStartTimeReader(TimeUtil::getFirstDayOfMonth);
        ganttLayout.setInclusiveChildEndTimeReader(TimeUtil::getLastDayOfMonth);
        return ganttLayout;
    }

    public static GanttLayout<YearWeek, LocalDate> createYearWeekLocalDateGanttLayout() {
        GanttLayout<YearWeek, LocalDate> ganttLayout = new GanttLayout<>();
        ganttLayout.setInclusiveChildStartTimeReader(TimeUtil::getFirstDayOfWeek);
        ganttLayout.setInclusiveChildEndTimeReader(TimeUtil::getLastDayOfWeek);
        return ganttLayout;
    }

    public static GanttLayout<LocalDate, LocalDate> createDayLocalDateGanttLayout() {
        return new GanttLayout<>();
    }

}
