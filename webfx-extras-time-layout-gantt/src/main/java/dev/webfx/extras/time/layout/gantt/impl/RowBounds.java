package dev.webfx.extras.time.layout.gantt.impl;

import dev.webfx.extras.time.layout.TimeLayout;
import dev.webfx.extras.time.layout.impl.LayoutBounds;
import dev.webfx.extras.time.layout.impl.TimeLayoutBase;

/**
 * @author Bruno Salmon
 */
public abstract class RowBounds<THIS extends RowBounds<THIS>> extends LayoutBounds<THIS, Object> {

    protected boolean recycling;

    public RowBounds(TimeLayout<?, ?> timeLayout) {
        super((TimeLayoutBase<THIS, Object>) timeLayout); // Not true, but generics don't matter as we will just access basic fields (width, height, etc...)
        setChild((THIS) this); // The child is the row itself (a bit hacky, but convenient for compatibility with childProcessor-based utility methods)
    }

    public void setRecycling(boolean recycling) {
        if (recycling != this.recycling) {
            this.recycling = recycling;
            if (recycling)
                onRecyclingStart();
            else
                onRecyclingEnd();
        }
    }

    public boolean isRecycling() {
        return recycling;
    }

    protected void onRecyclingStart() { }

    protected void onRecyclingEnd() { }

    protected void checkRecycling() {
        if (!recycling)
            throw new IllegalStateException("This operation can be done only during recycling state");
    }

    protected void syncT() { } // start/end time fields are never requested on rows

    @Override
    protected void syncH() {
        setX(0);
        setWidth(timeLayout.getWidth());
    }

    @Override
    protected abstract void syncV(); // must be overridden by subclasses to set row y & height

}