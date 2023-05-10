package dev.webfx.extras.time.layout.gantt;

import dev.webfx.extras.time.layout.LayoutBounds;

import java.time.temporal.Temporal;
import java.util.*;

/**
 * @author Bruno Salmon
 */
public final class ParentRow<C, T extends Temporal> {

    private final Object parent;
    private final GanttLayout<C, T> ganttLayout;
    private final Map<C, ChildBlock<T>> cache = new HashMap<>();
    private final List<List<ChildBlock<T>>> packedRows = new ArrayList<>();
    private boolean emptyRowsRemovalRequired;
    private GrandparentRow grandparentRow;
    final LayoutBounds rowPosition = new LayoutBounds();
    int firstChildIndex = -1;
    int lastChildIndex = -1;

    public ParentRow(Object parent, GanttLayout<C, T> ganttLayout) {
        this.parent = parent;
        this.ganttLayout = ganttLayout;
    }

    int computeChildRowIndex(int childIndex, C child, T startTime, T endTime, double startX, double endX, double layoutWidth) {
        if (!ganttLayout.isTetrisPacking()) {
            return 0;
        }
        // Tetris block algorithm:
        if (childIndex == 0) { // => means that this is the first call of a new pass over the children
            removeEmptyRowsIfRequired();
        }
        ChildBlock<T> block = cache.get(child);
        if (block != null && Objects.equals(block.startTime, startTime) && Objects.equals(block.endTime, endTime)) {
            block.startX = startX;
            block.endX = endX;
            return block.rowIndex;
        }
        if (block != null) { // The block already existed but has moved! So we need to remove it now
            List<ChildBlock<T>> row = packedRows.get(block.rowIndex);
            row.remove(block);
            if (row.isEmpty())
                emptyRowsRemovalRequired = true;
        }
        ChildBlock<T> newBlock = new ChildBlock<>(childIndex, startTime, endTime, startX, endX);
        cache.put(child, newBlock);
        loop: for (int rowIndex = 0; rowIndex < packedRows.size(); rowIndex++) {
            List<ChildBlock<T>> row = packedRows.get(rowIndex);
            for (ChildBlock<T> b : row) {
                if (newBlock.overlaps(b, layoutWidth > 0 && b.childIndex < childIndex))
                    continue loop;
            }
            newBlock.rowIndex = rowIndex;
            row.add(newBlock);
            return rowIndex;
        }
        return createNewRow(newBlock);
    }

    private int createNewRow(ChildBlock<T> block) {
        List<ChildBlock<T>> row = new ArrayList<>();
        row.add(block);
        block.rowIndex = packedRows.size();
        packedRows.add(row);
        return block.rowIndex;
    }

    public int getRowsCount() {
        if (!ganttLayout.isTetrisPacking())
            return 1;
        removeEmptyRowsIfRequired(); // we might eventually need to do a last removal of empty rows
        return packedRows.size(); // and then, we can just return the number of packed rows (much faster).
    }

    void cleanCache(List<C> children) {
        firstChildIndex = lastChildIndex = -1;
        // Cleaning cache from children that are no longer listed
        for (Iterator<Map.Entry<C, ChildBlock<T>>> it = cache.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<C, ChildBlock<T>> entry = it.next();
            C cacheChild = entry.getKey();
            ChildBlock<T> cacheBlock = entry.getValue();
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
            List<ChildBlock<T>> row = packedRows.get(cacheBlock.rowIndex);
            row.remove(cacheBlock);
            if (row.isEmpty())
                emptyRowsRemovalRequired = true;
        }
    }

    private void removeEmptyRowsIfRequired() {
        if (!emptyRowsRemovalRequired)
            return;
        int removedRows = 0;
        for (int rowIndex = 0; rowIndex < packedRows.size(); rowIndex++) {
            List<ChildBlock<T>> row = packedRows.get(rowIndex);
            if (row.isEmpty()) {
                packedRows.remove(rowIndex--);
                removedRows++;
            } else if (removedRows > 0) {
                for (ChildBlock<T> block : row) block.rowIndex -= removedRows;
            }
        }
        emptyRowsRemovalRequired = false;
    }

    public Object getParent() {
        return parent;
    }

    public GrandparentRow getGrandparentRow() {
        return grandparentRow;
    }

    void setGrandparentRow(GrandparentRow grandparentRow) {
        if (this.grandparentRow != grandparentRow) {
            this.grandparentRow = grandparentRow;
            grandparentRow.getParentRows().add(this);
        }
    }

    public LayoutBounds getRowPosition() {
        if (!rowPosition.isValid()) {
            int rowsCount = getRowsCount();
            rowPosition.setHeight(rowsCount == 0 ? 0 : rowsCount * (ganttLayout.getChildFixedHeight() + ganttLayout.getVSpacing()) - ganttLayout.getVSpacing());
            rowPosition.setValid(true);
        }
        return rowPosition;
    }

    List<C> getChildren() {
        if (firstChildIndex == -1)
            return Collections.emptyList();
        List<C> children = ganttLayout.getChildren();
        int fromIndex = Math.min(firstChildIndex, children.size());
        int toIndex = Math.min(lastChildIndex + 1, children.size());
        return children.subList(fromIndex, toIndex);
    }

    LayoutBounds getChildPosition(int i) {
        return ganttLayout.getChildPosition(firstChildIndex + i);
    }

}
