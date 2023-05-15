package dev.webfx.extras.time.layout.gantt.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Bruno Salmon
 */
public final class ParentRow<C> extends RowBounds<ParentRow<C>> {

    private final Object parent;
    ParentRow<C> aboveParentRow; // Note: will be null for first row under a grandparent row
    GrandparentRow grandparentRow;
    private final GanttLayoutImpl<C, ?> ganttLayout;
    private final List<GanttLayoutBounds<C, ?>> childrenPositions = new ArrayList<>();
    private List<List<GanttLayoutBounds<C, ?>>> tetrisRows, oldTetrisRows;


    public ParentRow(Object parent, ParentRow<C> aboveParentRow, GanttLayoutImpl<C, ?> ganttLayout) {
        super(ganttLayout);
        this.parent = parent;
        this.aboveParentRow = aboveParentRow;
        this.ganttLayout = ganttLayout;
    }

    @Override
    protected void onRecyclingStart() {
        aboveParentRow = null;
        grandparentRow = null;
        purgeChildren();
        invalidateV();
    }

    void purgeChildren() {
        childrenPositions.clear();
        oldTetrisRows = tetrisRows;
        tetrisRows = null;
        //if (ganttLayout.isTetrisPacking())
            invalidateV();
    }

    public void setAboveParentRow(ParentRow<C> aboveParentRow) {
        checkRecycling();
        this.aboveParentRow = aboveParentRow;
    }

    void addChild(GanttLayoutBounds<C, ?> cp) {
        childrenPositions.add(cp);
    }

    public int getRowsCount() { // Note: never returns 0, 1 is minimum
        if (!ganttLayout.isTetrisPacking())
            return 1;
        if (tetrisRows == null) {
            childrenPositions.forEach(GanttLayoutBounds::getRowIndexInParentRow);
        }
        return tetrisRows == null ? 1 : tetrisRows.size();
    }

    public Object getParent() {
        return parent;
    }

    void setGrandparentRow(GrandparentRow grandparentRow) {
        if (this.grandparentRow != grandparentRow) {
            this.grandparentRow = grandparentRow;
            if (grandparentRow != null)
                grandparentRow.addParent(this);
        }
    }

    @Override
    protected void syncV() {
        ganttLayout.syncParentV(this);
    }

    List<GanttLayoutBounds<C, ?>> getChildrenPositions() {
        return childrenPositions;
    }

    int computeChildTetrisRowIndex(GanttLayoutBounds<C, ?> cp) {
        if (!ganttLayout.isTetrisPacking())
            return 0;
        if (tetrisRows == null) {
            tetrisRows = new ArrayList<>();
            // Rebuilding the tetris rows from the previous version (if present) - ignoring the children that may have
            // disappeared - and inserting the remaining ones back to the new tetris rows in the same order, so the new
            // tetris rows look almost the same, giving some visual stability (if we don't do that, the children may be
            // inserted in a complete different order each time the user scroll over the dates, which is disturbing)
            if (oldTetrisRows != null) {
                int[] cpRowIndex = { -1 }; // to store the row index of the request child if we find it in the process
                for (List<GanttLayoutBounds<C, ?>> oldTetrisRow : oldTetrisRows) { // Iterating all old rows
                    for (GanttLayoutBounds<C, ?> oldCp : oldTetrisRow) { // Iterating all old children positions in the row
                        // Checking if that old child is still present in the new set
                        childrenPositions.stream().filter(newCp -> Objects.equals(newCp.getChild(), oldCp.getChild()))
                                .findFirst().ifPresent(newCp -> { // If yes, we reintroduce it in the new tetris rows
                                    // The following will create a recursive call, but not an infinite loop, because
                                    int rowIndex = newCp.getRowIndexInParentRow(); // will go to the second part now
                                    if (newCp == cp) // We store the result for the requested child
                                        cpRowIndex[0] = rowIndex;
                                });
                    }
                }
                oldTetrisRows = null; // We can forget the old tetris rows now
                // We return the result if the requested child was found in the process
                if (cpRowIndex[0] != -1)
                    return cpRowIndex[0];
                // Otherwise, if it's a new child, we keep going
            }
        }
        // We try to find a row where the child can fit without overlapping the other children already present on that row
        loop: for (int tetrisRowIndex = 0; tetrisRowIndex < tetrisRows.size(); tetrisRowIndex++) { // iterate all rows
            List<GanttLayoutBounds<C, ?>> tetrisRow = tetrisRows.get(tetrisRowIndex); // getting the row
            for (GanttLayoutBounds<C, ?> other : tetrisRow) { // iterate all children already present on that row
                if (cp == other) // shouldn't really happen, but just in case the requested child is already present,
                    return tetrisRowIndex; // we return its row (no change)
                if (cp.overlaps(other)) // If it overlaps another child,
                    continue loop; // we continue to iterate to the new row
            }
            // We reach this point if the child didn't overlap any of the existing children on that row
            tetrisRow.add(cp); // So we can add it to that row,
            return tetrisRowIndex; // and return that row index
        }
        // We reach this point when the child couldn't be inserted in any of the existing rows,
        List<GanttLayoutBounds<C, ?>> tetrisRow = new ArrayList<>(); // so we need to create a new row for it
        tetrisRow.add(cp); // and insert it in that row
        tetrisRows.add(tetrisRow); // and insert that row after the other rows
        return tetrisRows.size() - 1; // and return that last row index
    }

}
