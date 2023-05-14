package dev.webfx.extras.time.layout.gantt.impl;

import dev.webfx.extras.geometry.MutableBounds;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bruno Salmon
 */
public final class GrandparentRow extends RowBounds<GrandparentRow> { //represents the enclosing row (headRow + parentRows)

    private final Object grandparent;
    private GrandparentRow aboveGrandparentRow;
    private final GanttLayoutImpl<?, ?> ganttLayout;
    private final MutableBounds headRow = new MutableBounds(); // represents the head row (first row above the parentRows)
    private final List<ParentRow<?>> parentRows = new ArrayList<>();
    private int vVersionHead;

    public GrandparentRow(Object grandparent, GrandparentRow aboveGrandparentRow, GanttLayoutImpl<?, ?> ganttLayout) {
        super(ganttLayout);
        this.grandparent = grandparent;
        this.aboveGrandparentRow = aboveGrandparentRow;
        this.ganttLayout = ganttLayout;
    }

    @Override
    protected void onRecyclingStart() {
        parentRows.clear();
        invalidateV();
        vVersionHead = -1;
    }

    void setAboveGrandparentRow(GrandparentRow aboveGrandparentRow) {
        checkRecycling(); // aboveGrandparentRow should be changed only during recycling
        this.aboveGrandparentRow = aboveGrandparentRow;
    }

    public Object getGrandparent() {
        return grandparent;
    }

    public <C> List<ParentRow<C>> getParentRows() {
        return (List<ParentRow<C>>) (List) parentRows;
    }

    public void addParent(ParentRow<?> parentRow) {
        parentRows.add(parentRow);
    }

    @Override
    protected void syncV() {
        double y;
        if (aboveGrandparentRow == null)
            y = ganttLayout.getTopY();
        else
            y = aboveGrandparentRow.getMaxY();
        setY(y);
        checkSyncVHead(y); // we can sync the head position at this point (this will also avoid possible infinite recursion)
        if (ganttLayout.isParentFixedHeight()) { // Faster case (we don't need to call each parent row)
            setHeight(headRow.getHeight() + parentRows.size() * ganttLayout.getParentFixedHeight());
        } else {
            setHeight(getLastParentMaxY() - y);
        }
    }

    private double getLastParentMaxY() {
        return parentRows.get(parentRows.size() - 1).getMaxY();
    }

    private void checkSyncVHead() {
        if (vVersionHead != ganttLayout.vVersion) {
            syncVHead();
            vVersionHead = ganttLayout.vVersion;
        }
    }

    private void syncVHead() {
        syncVHead(getY());
    }

    private void checkSyncVHead(double y) {
        if (vVersionHead != ganttLayout.vVersion) {
            syncVHead(y);
            vVersionHead = ganttLayout.vVersion;
        }
    }

    private void syncVHead(double y) {
        headRow.setX(getX());
        headRow.setWidth(getWidth());
        headRow.setY(y);
        headRow.setHeight(ganttLayout.getGrandparentHeight()); // actually head height
    }

    public MutableBounds getHeadRow() {
        checkSyncVHead();
        return headRow;
    }

}
