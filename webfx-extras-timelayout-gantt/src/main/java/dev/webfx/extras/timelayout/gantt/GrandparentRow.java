package dev.webfx.extras.timelayout.gantt;

import dev.webfx.extras.timelayout.LayoutPosition;

/**
 * @author Bruno Salmon
 */
public final class GrandparentRow {

    private final Object grandparent;
    private final LayoutPosition rowPosition = new LayoutPosition();

    public GrandparentRow(Object grandparent) {
        this.grandparent = grandparent;
    }

    public Object getGrandparent() {
        return grandparent;
    }

    public LayoutPosition getRowPosition() {
        return rowPosition;
    }
}
