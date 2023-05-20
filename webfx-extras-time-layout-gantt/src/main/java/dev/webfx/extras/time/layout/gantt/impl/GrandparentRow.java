package dev.webfx.extras.time.layout.gantt.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bruno Salmon
 */
public final class GrandparentRow extends EnclosingRow<GrandparentRow> { // represents the enclosing row (header + parentRows)

    private final Object grandparent;
    GrandparentRow aboveGrandparentRow;
    private final GanttLayoutImpl<?, ?> ganttLayout;
    final List<ParentRow<?>> parentRows = new ArrayList<>();

    public GrandparentRow(Object grandparent, GrandparentRow aboveGrandparentRow, GanttLayoutImpl<?, ?> ganttLayout) {
        super(ganttLayout);
        this.grandparent = grandparent;
        this.aboveGrandparentRow = aboveGrandparentRow;
        this.ganttLayout = ganttLayout;
    }

    @Override
    protected void onRecyclingStart() {
        super.onRecyclingStart();
        parentRows.clear();
    }

    void setAboveGrandparentRow(GrandparentRow aboveGrandparentRow) {
        throwExceptionIfNotRecycling(); // aboveGrandparentRow should be changed only during recycling
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
    protected void layoutHorizontally() {
        ganttLayout.layoutGrandparentRowHorizontally(this);
    }

    @Override
    protected void layoutVertically() {
        ganttLayout.layoutGrandparentRowVertically(this);
    }

    @Override
    protected void layoutHeaderHorizontally() {
        ganttLayout.layoutGrandparentHeaderHorizontally(this);
    }

    @Override
    protected void layoutHeaderVertically() {
        ganttLayout.layoutGrandparentHeaderVertically(this);
    }

    @Override
    protected boolean headerHorizontalVersionOp(boolean invalidate, boolean validate) {
        int freshVersion = invalidate ? -1 : ganttLayout.grandparentHeaderHorizontalVersion;
        if (invalidate || validate)
            headerHorizontalVersion = freshVersion;
        return headerHorizontalVersion == freshVersion;
    }

    double getLastParentMaxY() {
        return parentRows.get(parentRows.size() - 1).getMaxY();
    }

}
