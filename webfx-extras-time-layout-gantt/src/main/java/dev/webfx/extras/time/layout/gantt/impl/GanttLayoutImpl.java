package dev.webfx.extras.time.layout.gantt.impl;

import dev.webfx.extras.geometry.Bounds;
import dev.webfx.extras.time.layout.TimeProjector;
import dev.webfx.extras.time.layout.gantt.GanttLayout;
import dev.webfx.extras.time.layout.impl.LayoutBounds;
import dev.webfx.extras.time.layout.impl.TimeLayoutBase;
import dev.webfx.extras.time.layout.impl.TimeLayoutUtil;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

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
public class GanttLayoutImpl<C, T extends Temporal> extends TimeLayoutBase<C, T> implements GanttLayout<C, T> {

    // Input fields
    private Function<C, ?> childParentReader;
    private Function<C, ?> childGrandparentReader;
    private Function<Object, ?> parentGrandparentReader;
    private Function<C, Double> childTetrisMinWidthReader;
    private double parentWidth;
    private double grandparentHeight = 80; // arbitrary non-null default value (so grandparent rows will appear even if
    // the application code forgot to call setGrandparentHeight())
    private boolean tetrisPacking;
    private final BooleanProperty parentsProvidedProperty = new SimpleBooleanProperty() {
        @Override
        protected void invalidated() {
            if (get())
                syncTreeFromProvidedParents();
            invalidateChildrenTree();
        }
    };
    private final ObservableList<Object> parents = FXCollections.observableArrayList();

    // Output fields
    private final List<ParentRow<C>> parentRows = new ArrayList<>();
    private final List<GrandparentRow> grandparentRows = new ArrayList<>();
    private Map<Object, ParentRow<C>> parentToParentRowMap = new HashMap<>(); // "final" between 2 recycling
    private Map<Object, GrandparentRow> grandparentToGrandparentRowMap = new HashMap<>(); // "final" between 2 recycling

    // Internal version management
    int childrenTreeVersion, builtChildrenTreeVersion; // impact the whole tree (grandparents + parents + children)
    int providedTreeVersion, builtProvidedTreeVersion; // impact grandparents + parents only (used only if parents are provided)

    // Fields used during tree sync
    private Map<Object, GrandparentRow> oldGrandparentToGrandparentRowMap;
    private Map<Object, ParentRow<C>> oldParentToParentRowMap;
    private GrandparentRow lastGrandparentRow;
    private ParentRow<C> lastParentRow;

    public GanttLayoutImpl() {
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
        parents.addListener((ListChangeListener<Object>) c -> invalidateProvidedTree());
    }

    @Override
    protected LayoutBounds<C, T> createChildLayoutBounds() {
        return new GanttLayoutBounds<>(this);
    }

    @Override
    public GanttLayoutImpl<C, T> setChildParentReader(Function<C, ?> childParentReader) {
        this.childParentReader = childParentReader;
        invalidateChildrenTree();
        return this;
    }

    @Override
    public GanttLayoutImpl<C, T> setChildGrandparentReader(Function<C, ?> childGrandparentReader) {
        this.childGrandparentReader = childGrandparentReader;
        invalidateChildrenTree();
        return this;
    }

    @Override
    public <P> GanttLayoutImpl<C, T> setParentGrandparentReader(Function<P, ?> parentGrandparentReader) {
        this.parentGrandparentReader = (Function<Object, ?>) parentGrandparentReader;
        invalidateChildrenTree();
        return this;
    }

    @Override
    public GanttLayoutImpl<C, T> setChildTetrisMinWidthReader(Function<C, Double> childTetrisMinWidthReader) {
        this.childTetrisMinWidthReader = childTetrisMinWidthReader;
        return this;
    }

    @Override
    public GanttLayoutImpl<C, T> setParentWidth(double parentWidth) {
        this.parentWidth = parentWidth;
        return this;
    }

    @Override
    public double getParentWidth() {
        return parentWidth;
    }

    @Override
    public GanttLayoutImpl<C, T> setGrandparentHeight(double grandparentHeight) {
        this.grandparentHeight = grandparentHeight;
        invalidateV();
        return this;
    }

    @Override
    public double getGrandparentHeight() {
        return grandparentHeight;
    }

    @Override
    public GanttLayoutImpl<C, T> setTetrisPacking(boolean tetrisPacking) {
        this.tetrisPacking = tetrisPacking;
        invalidateChildrenTree();
        return this;
    }

    @Override
    public boolean isTetrisPacking() {
        return tetrisPacking;
    }

    @Override
    public BooleanProperty parentsProvidedProperty() {
        return parentsProvidedProperty;
    }

    @Override
    public GanttLayoutImpl<C, T> setParentsProvided(boolean parentsProvided) {
        parentsProvidedProperty.set(parentsProvided);
        return this;
    }

    @Override
    public boolean isParentsProvided() {
        return parentsProvidedProperty.get();
    }

    @Override
    public <P> ObservableList<P> getParents() {
        return (ObservableList<P>) parents;
    }

    boolean isParentFixedHeight() {
        return isChildFixedHeight() && !isTetrisPacking();
    }

    double getParentFixedHeight() {
        return getChildFixedHeight();
    }

    @Override
    protected void onChildrenChanged(ListChangeListener.Change<? extends C> c) {
        super.onChildrenChanged(c);
        invalidateChildrenTree();
    }

    private void invalidateChildrenTree() {
        childrenTreeVersion++;
        invalidateV(); // for positions that may be recycled
    }

    private void invalidateProvidedTree() {
        providedTreeVersion++;
        invalidateChildrenTree();
    }

    void checkSyncTree() {
        checkSyncProvidedTree();
        checkSyncChildrenTree();
    }

    private void checkSyncChildrenTree() {
        if (builtChildrenTreeVersion != childrenTreeVersion) {
            syncTreeFromChildren();
            builtChildrenTreeVersion = childrenTreeVersion;
        }
    }

    private void checkSyncProvidedTree() {
        if (isParentsProvided() && builtProvidedTreeVersion != providedTreeVersion) {
            syncTreeFromProvidedParents();
            builtProvidedTreeVersion = providedTreeVersion;
        }
    }

    private void syncTreeFromChildren() {
        if (childrenPositions == null) // may happen when parents are provided before the children
            return;
        startSync(false);
        // Now we begin the loop by iterating over all children (positions)
        childrenPositions.stream().map(lb -> (GanttLayoutBounds<C,T>) lb).forEach(this::syncChildBranches);
        endSync();
    }

    private void syncTreeFromProvidedParents() {
        // We create a new list of parent rows that will have the same size and order as the provided parents. Therefore:
        // 1) new empty parent rows may appear for parents that had no known children so far
        // 2) existing parent rows may now be in a different order, so we need to update their vertical position
        // 3) Some existing parent rows may disappear (if not listed again in the provided parent for any reason)
        startSync(true);
        parents.forEach(this::syncProvidedParentBranches);
        endSync();
        invalidateChildrenTree();
    }

    private void startSync(boolean syncFromProvidedParents) {
        oldGrandparentToGrandparentRowMap = grandparentToGrandparentRowMap;
        oldParentToParentRowMap = parentToParentRowMap;
        // Same with parentRows, except when parents are provided (as it stays identical in that case)
        if (isParentsProvided() && !syncFromProvidedParents) {
            parentRows.forEach(ParentRow::purgeChildren);
        } else {
            // We clear the list of grandparent rows, as we will refresh it (eventually with same instances)
            grandparentRows.clear();
            parentRows.clear();
            grandparentToGrandparentRowMap = new HashMap<>();
            parentToParentRowMap = new HashMap<>();
        }
        // Clearing the cache (so they keep only live instances), but keeping a reference to old cache to allow recycling
        lastParentRow = null;
        lastGrandparentRow = null;
    }

    private void endSync() {
        grandparentRows.forEach(gpr -> gpr.setRecycling(false));
        parentRows.forEach(pr -> pr.setRecycling(false));
        rowsCount = -1;
        oldGrandparentToGrandparentRowMap = null;
        oldParentToParentRowMap = null;
        lastParentRow = null;
        lastGrandparentRow = null;
    }

    private void syncProvidedParentBranches(Object parent) {
        Object grandparent = parentGrandparentReader != null ? parentGrandparentReader.apply(parent) : null;
        syncGrandparentBranch(grandparent);
        syncParentBranches(parent, true);
    }

    private void syncChildBranches(GanttLayoutBounds<C, T> cp) {
        // Reading parent & grandparent of that child
        Object parent = childParentReader == null ? null : childParentReader.apply(cp.getChild());
        Object grandparent = childGrandparentReader != null ? childGrandparentReader.apply(cp.getChild()) :
                parent != null && parentGrandparentReader != null ? parentGrandparentReader.apply(parent) : null;
        // Getting or creating the grandparent row if grandparent exists
        if (!isParentsProvided())
            syncGrandparentBranch(grandparent);
        // Now same for parent row, however we accept null parents (children with null parents will be on the same
        // row with no parent on the left). First we check if the parent row already exists
        syncParentBranches(parent, false);
        cp.setParent(parent);
        cp.setParentRow(lastParentRow); // will add this child to parentRow as well
    }

    private void syncGrandparentBranch(Object grandparent) {
        // Getting or creating the grandparent row if grandparent exists
        GrandparentRow grandparentRow = null;
        if (grandparent != null) {
            // Checking if it already exists
            grandparentRow = grandparentToGrandparentRowMap.get(grandparent);
            // Creating it if not
            if (grandparentRow == null) {
                // Trying to recycle a previous instance
                grandparentRow = oldGrandparentToGrandparentRowMap.get(grandparent);
                // If not found, we create a new instance
                if (grandparentRow == null)
                    grandparentRow = new GrandparentRow(grandparent, lastGrandparentRow, this);
                    // Otherwise, we recycle that instance (may still have correct data)
                else if (!grandparentRow.isRecycling()) { // First time we reuse it in that pass
                    grandparentRow.setRecycling(true); // Entering in recycling mode
                    grandparentRow.setAboveGrandparentRow(lastGrandparentRow); // Resetting aboveGrandparentRow
                }
                // Now that we have a grandparent row (either new or recycled), we memorise it in the cache
                grandparentToGrandparentRowMap.put(grandparent, grandparentRow);
                // And add it to the grandparent rows
                grandparentRows.add(grandparentRow);
                // A new grandparent row introduce a break in the parent rows (next parent row should have aboveParentRow = null
                lastParentRow = null;
            }
        }
        lastGrandparentRow = grandparentRow;
    }

    private void syncParentBranches(Object parent, boolean syncFromProvidedParents) {
        ParentRow<C> parentRow = parentToParentRowMap.get(parent); // Note that provided parent are already in that cache
        if (!isParentsProvided() || syncFromProvidedParents) {
            if (parentRow == null) { // We create a new parent row if it doesn't exist
                parentRow = oldParentToParentRowMap.get(parent);
                if (parentRow == null)
                    parentRow = new ParentRow<>(parent, lastParentRow, this);
                else if (!parentRow.isRecycling()) { // First use for recycling
                    parentRow.setRecycling(true);
                    parentRow.setAboveParentRow(lastParentRow);
                }
                parentToParentRowMap.put(parent, parentRow);
                parentRows.add(parentRow);
            }
            parentRow.setGrandparentRow(lastGrandparentRow); // Will add parentRow to grandparent as well
        }
        lastParentRow = parentRow;
    }

    @Override
    public void syncChildRowIndex(LayoutBounds<C, T> cp) {
        GanttLayoutBounds<C, T> gcp = (GanttLayoutBounds<C, T>) cp;
        cp.setRowIndex(gcp.getRowIndexInParentRow()); // TODO: add previous rows from previous parents
    }

    @Override
    public void syncChildV(LayoutBounds<C, T> cp) {
        // 1) Computing child height
        GanttLayoutBounds<C, T> gcp = (GanttLayoutBounds<C, T>) cp; // Always a GanttLayoutBounds instance
        double childHeight = 0;
        if (isChildFixedHeight())
            childHeight = getChildFixedHeight();
        cp.setHeight(childHeight);
        // 2) Computing child y position
        double vSpacing = getVSpacing();
        int childRowIndex = gcp.getRowIndexInParentRow();
        cp.setY(gcp.getParentRow().getY() // top position of enclosing parent row
                + vSpacing // top spacing
                + (childHeight + vSpacing) * childRowIndex // vertical shift of that particular child
        );
    }

    void syncParentV(ParentRow<C> pr) {
        // 1) Computing parent row height
        double height;
        if (isParentFixedHeight()) {
            height = getParentFixedHeight();
        } else if (isChildFixedHeight()) {
            double childHeight = getChildFixedHeight();
            double vSpacing = getVSpacing();
            height = vSpacing // top spacing
                    + (childHeight + vSpacing) * pr.getRowsCount(); // total of children in that parent row
         } else
            height = 0; // TODO: what to do in this case?
        // 2) Computing parent row y position
        pr.setHeight(height);
        double y;
        if (pr.aboveParentRow != null)
            y = pr.aboveParentRow.getMaxY();
        else if (pr.grandparentRow != null)
            y = pr.grandparentRow.getHeadRow().getMaxY();
        else
            y = getTopY();
        pr.setY(y);
    }

    void syncGrandparentV(GrandparentRow gpr) {
        // 1) Computing grandparent row y position
        double y;
        if (gpr.aboveGrandparentRow == null)
            y = getTopY();
        else
            y = gpr.aboveGrandparentRow.getMaxY();
        gpr.setY(y);
        // We can sync the head position at this point (this will also avoid possible infinite recursion)
        gpr.checkSyncVHead(y);
        // 2) Computing grandparent height
        if (isParentFixedHeight()) { // Faster case (we don't need to call each parent row)
            gpr.setHeight(gpr.headRow.getHeight() + gpr.parentRows.size() * getParentFixedHeight());
        } else {
            gpr.setHeight(gpr.getLastParentMaxY() - y);
        }
    }

    @Override
    protected double computeHeight() {
        checkSyncTree();
        RowBounds<?> lastEnclosingRow = null;
        if (!grandparentRows.isEmpty()) // equivalent but more efficient than last parent row
            lastEnclosingRow = grandparentRows.get(grandparentRows.size() - 1);
        else if (!parentRows.isEmpty()) // last parent row ok when no grandparent
            lastEnclosingRow = parentRows.get(parentRows.size() - 1);
        // Returning the bottom of the last enclosing row (or 0 if none)
        return lastEnclosingRow != null ? lastEnclosingRow.getMaxY() : 0;
    }

    @Override
    public int getRowsCount() {
        checkSyncTree();
        if (rowsCount == -1) {
            rowsCount = grandparentRows.size();
            if (isTetrisPacking())
                rowsCount += parentRows.stream().mapToInt(ParentRow::getRowsCount).sum();
            else
                rowsCount += parentRows.size();
        }
        return rowsCount;
    }

    public List<ParentRow<C>> getParentRows() {
        return parentRows;
    }

    public List<GrandparentRow> getGrandparentRows() {
        return grandparentRows;
    }

    @Override
    protected void processVisibleChildrenNow(javafx.geometry.Bounds visibleArea, double layoutOriginX, double layoutOriginY, BiConsumer<C, Bounds> childProcessor) {
        checkSyncTree();
        if (!grandparentRows.isEmpty())
            processVisibleChildrenInGrandparentRows(grandparentRows, visibleArea, layoutOriginX, layoutOriginY, childProcessor);
        else
            processVisibleChildrenInParentRows(parentRows, visibleArea, layoutOriginX, layoutOriginY, childProcessor);
    }

    private void processVisibleChildrenInGrandparentRows(List<GrandparentRow> grandparentRows, javafx.geometry.Bounds visibleArea, double layoutOriginX, double layoutOriginY, BiConsumer<C, Bounds> childProcessor) {
        TimeLayoutUtil.processVisibleChildrenLayoutBounds(
                grandparentRows,
                true, false, visibleArea, layoutOriginX, layoutOriginY,
                (grandparentRow, b) -> processVisibleChildrenInParentRows(grandparentRow.getParentRows(), visibleArea, layoutOriginX, layoutOriginY, childProcessor)
        );
    }

    private void processVisibleChildrenInParentRows(List<ParentRow<C>> parentRows, javafx.geometry.Bounds visibleArea, double layoutOriginX, double layoutOriginY, BiConsumer<C, Bounds> childProcessor) {
        TimeLayoutUtil.processVisibleChildrenLayoutBounds(
                parentRows,
                true, false, visibleArea, layoutOriginX, layoutOriginY,
                (parentRow, b) -> processVisibleChildrenInParentRow(parentRow, visibleArea, layoutOriginX, layoutOriginY, childProcessor));
    }

    private void processVisibleChildrenInParentRow(ParentRow<C> parentRow, javafx.geometry.Bounds visibleArea, double layoutOriginX, double layoutOriginY, BiConsumer<C, Bounds> childProcessor) {
        TimeLayoutUtil.processVisibleChildrenLayoutBounds(
                parentRow.getChildrenPositions(),
                false, true, visibleArea, layoutOriginX, layoutOriginY,
                childProcessor);
    }

}
