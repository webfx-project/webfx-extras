package dev.webfx.extras.time.layout.gantt;

import dev.webfx.extras.time.layout.LayoutBounds;
import dev.webfx.extras.time.layout.TimeLayoutUtil;
import dev.webfx.extras.time.layout.TimeProjector;
import dev.webfx.extras.time.layout.impl.TimeLayoutBase;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Bruno Salmon
 */
public class GanttLayout<C, T extends Temporal> extends TimeLayoutBase<C, T> {

    private Function<C, Double> childTetrisMinWidthReader;
    private Function<C, ?> childParentReader;
    private Function<C, ?> childGrandparentReader;
    private Function<Object, ?> parentGrandparentReader;
    private final Map<Object, ParentRow<C, T>> childToParentRowMap = new HashMap<>();
    private final ObservableList<Object> parents = FXCollections.observableArrayList();
    private final List<ParentRow<C, T>> parentRows = new ArrayList<>();
    private final List<GrandparentRow> grandparentRows = new ArrayList<>();
    private boolean parentsProvided;
    private boolean tetrisPacking;
    private boolean childrenChanged;
    private boolean layoutCompleted;
    private ParentRow<C, T> lastParentRow;
    private int rowsCountBeforeLastParentRow;
    private GrandparentRow lastGrandparentRow;
    private double parentWidth;
    private double grandparentHeight = 80; // arbitrary non-null default value (so grandparent rows will appear even if
    // the application code forgot to call setGrandparentHeight())

    public GanttLayout() {
        setTimeProjector(new TimeProjector<>() {
            @Override
            public double timeToX(T time, boolean start, boolean exclusive) {
                T timeWindowStart = getTimeWindowStart();
                T timeWindowEnd = getTimeWindowEnd();
                if (timeWindowStart == null || timeWindowEnd == null)
                    return 0;
                long totalDays = timeWindowStart.until(timeWindowEnd, ChronoUnit.DAYS) + 1;
                long daysToTime = timeWindowStart.until(time, ChronoUnit.DAYS);
                if (start && exclusive || !start && !exclusive)
                    daysToTime++;
                return getWidth() * daysToTime / totalDays;
            }

            @Override
            public T xToTime(double x) {
                T timeWindowStart = getTimeWindowStart();
                T timeWindowEnd = getTimeWindowEnd();
                if (timeWindowStart == null || timeWindowEnd == null)
                    return null;
                long totalDays = timeWindowStart.until(timeWindowEnd, ChronoUnit.DAYS) + 1;
                return (T) timeWindowStart.plus((long) (x * totalDays / getWidth()), ChronoUnit.DAYS);
            }
        });
        parents.addListener((ListChangeListener<Object>) c -> {
            // If the parents are provided externally,
            if (parentsProvided && !parentRows.isEmpty()) {
                syncParentRowsFromParents();
            }
        });
    }

    public <P> ObservableList<P> getParents() {
        return (ObservableList<P>) parents;
    }

    public GanttLayout<C, T> setParentsProvided(boolean parentsProvided) {
        this.parentsProvided = parentsProvided;
        return this;
    }

    public GanttLayout<C, T> setChildTetrisMinWidthReader(Function<C, Double> childTetrisMinWidthReader) {
        this.childTetrisMinWidthReader = childTetrisMinWidthReader;
        return this;
    }

    public GanttLayout<C, T> setChildParentReader(Function<C, ?> childParentReader) {
        this.childParentReader = childParentReader;
        return this;
    }

    public GanttLayout<C, T> setChildGrandparentReader(Function<C, ?> childGrandparentReader) {
        this.childGrandparentReader = childGrandparentReader;
        return this;
    }

    public <P> GanttLayout<C, T> setParentGrandparentReader(Function<P, ?> parentGrandparentReader) {
        this.parentGrandparentReader = (Function<Object, ?>) parentGrandparentReader;
        return this;
    }

    public double getParentWidth() {
        return parentWidth;
    }

    public GanttLayout<C, T> setParentWidth(double parentWidth) {
        this.parentWidth = parentWidth;
        return this;
    }

    public double getGrandparentHeight() {
        return grandparentHeight;
    }

    public GanttLayout<C, T> setGrandparentHeight(double grandparentHeight) {
        this.grandparentHeight = grandparentHeight;
        return this;
    }

    public boolean isTetrisPacking() {
        return tetrisPacking;
    }

    public GanttLayout<C, T> setTetrisPacking(boolean tetrisPacking) {
        this.tetrisPacking = tetrisPacking;
        return this;
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
        layoutCompleted = false;
    }

    @Override
    protected void updateChildPositionExtended(int childIndex, LayoutBounds cp, C child, T startTime, T endTime, double startX, double endX) {
        // Getting the parent from the child (if no getter is provided => 1 single parent = null)
        Object parent = childParentReader == null ? null : childParentReader.apply(child);
        // Getting or creating the parent row associated (this call also updates firstChildIndex and lastChildIndex in ParentRow)
        ParentRow<C, T> parentRow = getOrCreateParentRow(parent, childIndex);
        // Getting the row position of that parent row (not yet initialised if it was newly created)
        LayoutBounds pp = parentRow.rowPosition; // Not parentRow.getRowPosition() as this would update dirty height (not ready yet for that)
        // Because the children are supposed to be sorted by parent, a change in the parent row means we finished to
        // populate the last parent row, so we now have all information to set its vertical position
        if (parentRow != lastParentRow) {
            // If it was the first parent row, we set its vertical position to topY (a TimeLayout property)
            if (lastParentRow == null)
                pp.setY(getTopY());
            else { // otherwise (if there was a previous row before)
                // We set its vertical position to the bottom of the previous row
                pp.setY(lastParentRow.getRowPosition().getMaxY());
                // We also update rowsCountBeforeLastParentRow by adding the number of rows in that previous parent row
                rowsCountBeforeLastParentRow += lastParentRow.getRowsCount();
            }
            // We also set the parent position in invalid state, which means the height needs to be computed on next
            pp.setValid(false); // call to parentRow.getRowPosition()
            // We finished with the last parent row (unless there is a change in grandparent - see below)
            lastParentRow = parentRow; // so we now consider this parent row as the last for next time
            // We are now checking if that parent row is under a new grandparent, as we will need to shift it down in
            // that case. So first, we get that grandparent instance (if getter provided)
            Object grandparent =
                    childGrandparentReader  != null ? childGrandparentReader .apply(child)  :
                    parentGrandparentReader != null ? parentGrandparentReader.apply(parent) : null;
            // If there is a change in the grandparent, we need to create a new grandparent row
            if (grandparent == null)
                lastGrandparentRow = null;
            else if (lastGrandparentRow == null || !Objects.equals(grandparent, lastGrandparentRow.getGrandparent())) {
                // We create a new GrandparentRow instance for that new grandparent, and add it to the list
                GrandparentRow grandparentRow = new GrandparentRow(grandparent);
                grandparentRows.add(grandparentRow);
                // We get its row position instance, in order to set its vertical position and height
                LayoutBounds gp = grandparentRow.getRowPosition();
                // Its vertical position is the one computed so far for the parent row (that we will shift just after)
                gp.setY(pp.getY());
                // We set its height
                gp.setHeight(grandparentHeight);
                // We shift the parent row down (at the bottom of the new grandparent row)
                pp.setY(gp.getMaxY());
                // We finished with the last parent row (unless there is a change in grandparent - see below)
                lastGrandparentRow = grandparentRow; // so we now consider this grandparent row as the last for next time
                // Finally we increase rowsCountBeforeLastParentRow to count that new grandparent row
                rowsCountBeforeLastParentRow++;
            }
        }
        parentRow.setGrandparentRow(lastGrandparentRow);
        // Eventually adjusting the values to pass to the parent row if a childTetrisMinWidthReader is set
        if (childTetrisMinWidthReader != null) {
            double minWidth = childTetrisMinWidthReader.apply(child);
            if (endX - startX < minWidth) {
                startX = (startX + endX) / 2 - minWidth / 2;
                endX = startX + minWidth;
                startTime = getTimeProjector().xToTime(startX);
                endTime = getTimeProjector().xToTime(endX);
            }
        }
        int rowIndexInParentRow = parentRow.computeChildRowIndex(childIndex, child, startTime, endTime, startX, endX, getWidth());
        int rowIndex = rowsCountBeforeLastParentRow + rowIndexInParentRow;
        cp.setRowIndex(rowIndex);
        cp.setY(pp.getY() + rowIndexInParentRow * (getChildFixedHeight() + getVSpacing()));
    }

    @Override
    protected void onAfterLayoutChildren() {
        // If the parents are not provided externally, we populate them automatically (collected from the parents rows)
        if (!parentsProvided)
            syncParentsFromParentRows();
        else if (!parents.isEmpty())
            syncParentRowsFromParents();
        layoutCompleted = true;
    }

    private void syncParentsFromParentRows() {
        //parents.setAll(parentRows.stream().map(ParentRow::getParent).collect(Collectors.toList()));
    }

    private void syncParentRowsFromParents() {
        // We create a new list of parent rows that will have the same size and order as the provided parents. Therefore:
        // 1) new empty parent rows may appear for parents that had no known children so far
        // 2) existing parent rows may now be in a different order, so we need to update their vertical position
        // 3) Some existing parent rows may disappear (if not listed again in the provided parent for any reason)
        List<ParentRow<C, T>> newParentRows = parents.stream().map(this::syncParentRowFromParent).collect(Collectors.toList());
        rowsCountBeforeLastParentRow += newParentRows.size() - parentRows.size();
        // We now update the
        lastParentRow = null;
        lastGrandparentRow = null;
        double y = getTopY();
        for (int newParentIndex = 0; newParentIndex < newParentRows.size(); newParentIndex++) {
            ParentRow<C, T> parentRow = newParentRows.get(newParentIndex);
            LayoutBounds pp = parentRow.getRowPosition();
            GrandparentRow grandparentRow = parentRow.getGrandparentRow();
            if (grandparentRow != lastGrandparentRow) {
                LayoutBounds gp = grandparentRow.getRowPosition();
                gp.setY(y);
                y = gp.getMaxY();
                lastGrandparentRow = grandparentRow;
            }
            if (parentRow.parentIndex == -1) { // New empty parent row => we set it a height = 1 children height
                pp.setHeight(getChildFixedHeight());
            } else /*if (parentRow.parentIndex != newParentIndex)*/ {
                double deltaY = y - pp.getY(); // TODO: deltaRow
                if (deltaY != 0) {
                    for (int childIndex = parentRow.firstChildIndex; childIndex <= parentRow.lastChildIndex; childIndex++) {
                        LayoutBounds cp = getChildPosition(childIndex);
                        cp.setY(cp.getY() + deltaY);
                    }
                }
            }
            pp.setY(y);
            y += pp.getHeight();
            parentRow.parentIndex = newParentIndex;
            lastParentRow = parentRow;
        }
        parentRows.clear();
        parentRows.addAll(newParentRows);
    }

    private ParentRow<C, T> syncParentRowFromParent(Object parent) {
        ParentRow<C, T> parentRow = parentRows.stream().filter(pr -> Objects.equals(parent, pr.getParent())).findFirst().orElse(null);
        if (parentRow == null)
            parentRow = new ParentRow<>(parent, -1, this);
        return parentRow;
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
        if (parentRow == null) {
            childToParentRowMap.put(parent, parentRow = new ParentRow<>(parent, parentRows.size(), this));
            parentRows.add(parentRow);
        }
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
        if (!layoutCompleted) // happens when children have just been modified, but their position is still invalid,
            super.getRowsCount(); // so we call the default implementation to update these positions (this will
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
