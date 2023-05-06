package dev.webfx.extras.time.layout.gantt;

import dev.webfx.extras.time.layout.LayoutBounds;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bruno Salmon
 */
public final class GrandparentRow {

    private final Object grandparent;
    private final LayoutBounds rowPosition = new LayoutBounds();
    private final List<ParentRow<?, ?>> parentRows = new ArrayList<>();

    public GrandparentRow(Object grandparent) {
        this.grandparent = grandparent;
    }

    public Object getGrandparent() {
        return grandparent;
    }

    public LayoutBounds getRowPosition() {
        return rowPosition;
    }

    public List<ParentRow<?, ?>> getParentRows() {
        return parentRows;
    }
}
