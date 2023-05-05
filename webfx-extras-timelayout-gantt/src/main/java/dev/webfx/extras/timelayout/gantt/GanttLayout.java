package dev.webfx.extras.timelayout.gantt;

import dev.webfx.extras.timelayout.ChildPosition;
import dev.webfx.extras.timelayout.impl.TimeLayoutBase;
import javafx.collections.ListChangeListener;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Bruno Salmon
 */
public class GanttLayout<C, T extends Temporal> extends TimeLayoutBase<C, T> {

    private Function<C, ?> childParentReader;
    private Function<C, ?> childGrandParentReader;
    private final Map<Object, ParentRow<C, T>> childToParentRowMap = new HashMap<>();
    private final List<ParentRow<C, T>> parentRows = new ArrayList<>();
    private final List<Object> grandParents = new ArrayList<>();
    private boolean tetrisPacking;
    private boolean childrenChanged;
    private ParentRow<C, T> lastParentRow;
    private int rowsCountBeforeLastParentRow;
    private Object lastGrandParent;
    public double grandParentHeight = 80;

    public GanttLayout() {
        setTimeProjector((time, start, exclusive) -> {
            T timeWindowStart = getTimeWindowStart();
            T timeWindowEnd = getTimeWindowEnd();
            if (timeWindowStart == null || timeWindowEnd == null)
                return 0;
            long totalDays = timeWindowStart.until(timeWindowEnd, ChronoUnit.DAYS) + 1;
            long daysToTime = timeWindowStart.until(time, ChronoUnit.DAYS);
            if (start && exclusive || !start && !exclusive)
                daysToTime++;
            return getWidth() * daysToTime / totalDays;
        });
    }

    public void setChildParentReader(Function<C, ?> childParentReader) {
        this.childParentReader = childParentReader;
    }

    public void setChildGrandParentReader(Function<C, ?> childGrandParentReader) {
        this.childGrandParentReader = childGrandParentReader;
    }

    public void setTetrisPacking(boolean tetrisPacking) {
        this.tetrisPacking = tetrisPacking;
    }

    @Override
    protected void onChildrenChanged(ListChangeListener.Change<? extends C> c) {
        childrenChanged = true;
        super.onChildrenChanged(c);
    }

    @Override
    protected void onBeforeLayoutChildren() {
        if (childrenChanged) {
            childToParentRowMap.values().forEach(parentRow -> parentRow.cleanCache(children));
            childrenChanged = false;
        }
        lastParentRow = null;
        rowsCountBeforeLastParentRow = 0;
        lastGrandParent = null;
        parentRows.clear();
        grandParents.clear();
    }

    @Override
    protected void updateChildPositionExtended(int childIndex, ChildPosition<T> p, C child, T startTime, T endTime, double startX, double endX) {
        Object parent = childParentReader == null ? null : childParentReader.apply(child);
        ParentRow<C, T> parentRow = getOrCreateParentRow(parent);
        if (parentRow != lastParentRow) {
            if (lastParentRow == null)
                parentRow.setY(getTopY());
            else {
                parentRow.setY(lastParentRow.getY() + lastParentRow.getHeight());
                rowsCountBeforeLastParentRow += lastParentRow.getRowsCount();
            }
            parentRow.setHeight(-1); // height will be computed on next call to parentRow.getHeight()
            lastParentRow = parentRow;
            if (childGrandParentReader != null) {
                Object grandParent = childGrandParentReader.apply(child);
                if (grandParent != lastGrandParent) {
                    lastGrandParent = grandParent;
                    rowsCountBeforeLastParentRow++;
                    grandParents.add(grandParent);
                    parentRow.setY(parentRow.getY() + grandParentHeight);
                }
            }
        }
        parentRow.setGrandParent(lastGrandParent);
        int rowIndexInParentRow = parentRow.computeChildRowIndex(childIndex, child, startTime, endTime, startX, endX, getWidth(), tetrisPacking);
        int rowIndex = rowsCountBeforeLastParentRow + rowIndexInParentRow;
        p.setRowIndex(rowIndex);
        p.setY(parentRow.getY() + rowIndexInParentRow * (getChildFixedHeight() + getVSpacing())); // will be reset by layout if fillHeight is true
    }

    @Override
    protected double computeHeight() {
        if (lastParentRow == null)
            super.getRowsCount();
        if (lastParentRow == null)
            return 0;
        return lastParentRow.getY() + lastParentRow.getHeight() - getTopY();
    }

    public ParentRow<C, T> getOrCreateParentRow(Object parent) {
        ParentRow<C, T> parentRow = childToParentRowMap.get(parent);
        if (parentRow == null) {
            childToParentRowMap.put(parent, parentRow = new ParentRow<>(this));
            parentRow.setParent(parent);
        }
        if (parentRows.isEmpty() || parentRows.get(parentRows.size() - 1) != parentRow)
            parentRows.add(parentRow);
        return parentRow;
    }

    // Note: getRowsCount() can be called when child positions are still invalid, so at this point packedRows is not
    // up-to-date.
    @Override
    public int getRowsCount() {
        if (childrenChanged) // happens when children have just been modified, but their position is still invalid,
            return grandParents.size() + super.getRowsCount(); // so we call the default implementation to update these positions (this will
        // update packedRows in the process through the successive calls to computeChildRowIndex()).
        // Otherwise, packedRows is up-to-date when reaching this point,
        return grandParents.size() + rowsCountBeforeLastParentRow + (lastParentRow == null ? 0 : lastParentRow.getRowsCount());
    }

    public List<ParentRow<C, T>> getParentRows() {
        return parentRows;
    }
}
