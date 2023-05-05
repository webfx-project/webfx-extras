package dev.webfx.extras.timelayout.gantt;

import dev.webfx.extras.timelayout.LayoutPosition;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bruno Salmon
 */
public final class GrandparentRow {

    private final Object grandparent;
    private final LayoutPosition rowPosition = new LayoutPosition();
    private final List<ParentRow<?, ?>> parentRows = new ArrayList<>();

    public GrandparentRow(Object grandparent) {
        this.grandparent = grandparent;
    }

    public Object getGrandparent() {
        return grandparent;
    }

    public LayoutPosition getRowPosition() {
        return rowPosition;
    }

    public List<ParentRow<?, ?>> getParentRows() {
        return parentRows;
    }
}
