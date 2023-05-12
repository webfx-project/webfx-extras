package dev.webfx.extras.time.layout.gantt.impl;

import dev.webfx.extras.time.layout.impl.LayoutBounds;

import java.time.temporal.Temporal;

/**
 * @author Bruno Salmon
 */
final class GanttLayoutBounds<C, T extends Temporal> extends LayoutBounds<C, T> {

    private final GanttLayoutImpl<C, T> ganttLayout;
    private Object parent;
    private ParentRow<C> parentRow;
    private int rowIndexInParentRow = -1;

    public GanttLayoutBounds(GanttLayoutImpl<C, T> ganttLayout) {
        super(ganttLayout);
        this.ganttLayout = ganttLayout;
    }

    public Object getParent() {
        ganttLayout.checkSyncTree();
        return parent;
    }

    public void setParent(Object parent) {
        this.parent = parent;
    }

    public ParentRow<C> getParentRow() {
        ganttLayout.checkSyncTree();
        return parentRow;
    }

    public void setParentRow(ParentRow<C> parentRow) {
        this.parentRow = parentRow;
        if (parentRow != null)
            parentRow.addChild(this);
        rowIndexInParentRow = -1;
    }

    public int getRowIndexInParentRow() {
        if (rowIndexInParentRow == -1) {
            ganttLayout.checkSyncTree();
            rowIndexInParentRow = parentRow.computeChildTetrisRowIndex(this);
        }
        return rowIndexInParentRow;
    }

    boolean overlaps(GanttLayoutBounds<C, ?> other) {
        if (getMinX() <= other.getMinX()) // if this block starts before the other block,
            return getMaxX() >= other.getMinX(); // it overlaps the other block when its end reaches at least the start of that other block
        else // otherwise (ie if this blocks starts after the other block start),
            return other.getMaxX() >= getMinX();
    }

}
