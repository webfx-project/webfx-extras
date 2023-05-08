package dev.webfx.extras.time.layout.gantt;

import dev.webfx.extras.time.layout.LayoutBounds;
import dev.webfx.extras.time.layout.TimeLayoutUtil;
import dev.webfx.extras.time.layout.impl.TimeLayoutBase;
import javafx.collections.ListChangeListener;
import javafx.geometry.Bounds;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author Bruno Salmon
 */
public class GanttLayout<C, T extends Temporal> extends TimeLayoutBase<C, T> {

    private Function<C, ?> childParentReader;
    private Function<C, ?> childGrandparentReader;
    private final Map<Object, ParentRow<C, T>> childToParentRowMap = new HashMap<>();
    private final List<ParentRow<C, T>> parentRows = new ArrayList<>();
    private final List<GrandparentRow> grandparentRows = new ArrayList<>();
    private boolean tetrisPacking;
    private boolean childrenChanged;
    private ParentRow<C, T> lastParentRow;
    private int rowsCountBeforeLastParentRow;
    private GrandparentRow lastGrandparentRow;
    private double parentWidth;
    private double grandparentHeight = 80;

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

    public void setChildGrandparentReader(Function<C, ?> childGrandparentReader) {
        this.childGrandparentReader = childGrandparentReader;
    }

    public double getParentWidth() {
        return parentWidth;
    }

    public void setParentWidth(double parentWidth) {
        this.parentWidth = parentWidth;
    }

    public double getGrandparentHeight() {
        return grandparentHeight;
    }

    public void setGrandparentHeight(double grandparentHeight) {
        this.grandparentHeight = grandparentHeight;
    }

    public boolean isTetrisPacking() {
        return tetrisPacking;
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
        parentRows.clear();
        lastGrandparentRow = null;
        grandparentRows.clear();
        rowsCountBeforeLastParentRow = 0;
    }

    @Override
    protected void updateChildPositionExtended(int childIndex, LayoutBounds cp, C child, T startTime, T endTime, double startX, double endX) {
        Object parent = childParentReader == null ? null : childParentReader.apply(child);
        ParentRow<C, T> parentRow = getOrCreateParentRow(parent, childIndex);
        LayoutBounds pp = parentRow.rowPosition; // Not parentRow.getRowPosition() as this would update dirty height (not the right time to do it yet)
        if (parentRow != lastParentRow) {
            if (lastParentRow == null)
                pp.setY(getTopY());
            else {
                pp.setY(lastParentRow.getRowPosition().getMaxY());
                rowsCountBeforeLastParentRow += lastParentRow.getRowsCount();
            }
            pp.setValid(false); // height will be computed on next call to parentRow.getRowPosition()
            lastParentRow = parentRow;
            if (childGrandparentReader != null) {
                Object grandparent = childGrandparentReader.apply(child);
                if (grandparent == null)
                    lastGrandparentRow = null;
                else if (lastGrandparentRow == null || grandparent != lastGrandparentRow.getGrandparent()) {
                    GrandparentRow grandparentRow = new GrandparentRow(grandparent);
                    LayoutBounds gp = grandparentRow.getRowPosition();
                    gp.setY(pp.getY());
                    gp.setHeight(grandparentHeight);
                    grandparentRows.add(grandparentRow);
                    lastGrandparentRow = grandparentRow;
                    rowsCountBeforeLastParentRow++;
                    pp.setY(gp.getMaxY());
                }
            }
        }
        parentRow.setGrandparentRow(lastGrandparentRow);
        int rowIndexInParentRow = parentRow.computeChildRowIndex(childIndex, child, startTime, endTime, startX, endX, getWidth());
        int rowIndex = rowsCountBeforeLastParentRow + rowIndexInParentRow;
        cp.setRowIndex(rowIndex);
        cp.setY(pp.getY() + rowIndexInParentRow * (getChildFixedHeight() + getVSpacing()));
    }

    @Override
    protected double computeHeight() {
        if (lastParentRow == null)
            super.getRowsCount();
        if (lastParentRow == null)
            return 0;
        return lastParentRow.getRowPosition().getMaxY() - getTopY();
    }

    private ParentRow<C, T> getOrCreateParentRow(Object parent, int childIndex) {
        ParentRow<C, T> parentRow = childToParentRowMap.get(parent);
        if (parentRow == null)
            childToParentRowMap.put(parent, parentRow = new ParentRow<>(parent, this));
        if (parentRow.firstChildIndex == -1)
            parentRow.firstChildIndex = childIndex;
        parentRow.lastChildIndex = childIndex;
        if (parentRows.isEmpty() || parentRows.get(parentRows.size() - 1) != parentRow)
            parentRows.add(parentRow);
        return parentRow;
    }

    // Note: getRowsCount() can be called when child positions are still invalid, so at this point packedRows is not
    // up-to-date.
    @Override
    public int getRowsCount() {
        if (childrenChanged) // happens when children have just been modified, but their position is still invalid,
            return grandparentRows.size() + super.getRowsCount(); // so we call the default implementation to update these positions (this will
        // update packedRows in the process through the successive calls to computeChildRowIndex()).
        // Otherwise, packedRows is up-to-date when reaching this point,
        return grandparentRows.size() + rowsCountBeforeLastParentRow + (lastParentRow == null ? 0 : lastParentRow.getRowsCount());
    }

    public List<ParentRow<C, T>> getParentRows() {
        return parentRows;
    }

    public List<GrandparentRow> getGrandparentRows() {
        return grandparentRows;
    }

    @Override
    public void processVisibleChildrenNow(Bounds visibleArea, double layoutOriginX, double layoutOriginY, BiConsumer<C, LayoutBounds> childProcessor) {
        for (ParentRow<C, T> parentRow : parentRows) {
            LayoutBounds pp = parentRow.getRowPosition();
            if (pp.getY() - layoutOriginY > visibleArea.getMaxY())
                break;
            if (pp.getMaxY() - layoutOriginY < visibleArea.getMinY())
                continue;
            TimeLayoutUtil.processVisibleChildren(
                    parentRow.getChildren(),
                    parentRow::getChildPosition,
                    visibleArea, layoutOriginX, layoutOriginY, childProcessor);
        }
    }
}
