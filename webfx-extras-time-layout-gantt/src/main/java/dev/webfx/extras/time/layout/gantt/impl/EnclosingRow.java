package dev.webfx.extras.time.layout.gantt.impl;

import dev.webfx.extras.geometry.MutableBounds;
import dev.webfx.extras.time.layout.TimeLayout;
import dev.webfx.extras.time.layout.impl.LazyObjectBounds;
import dev.webfx.extras.time.layout.impl.TimeLayoutBase;

/**
 * A base class for enclosing rows (parent & grandparent row). Its bounds represent the enclosing bounds that contain
 * all elements inside (the header plus all the content such as the children for a parent row). The header is a separate
 * LazyObjectBounds that can be in different positions (see HeaderPosition) inside that enclosing row and that will be
 * drawn by the application.
 *
 * @author Bruno Salmon
 */
public abstract class EnclosingRow<THIS extends EnclosingRow<THIS>> extends LazyObjectBounds<THIS> { // enclosing row = header + content

    protected LazyObjectBounds<Object> header;
    protected int headerHorizontalVersion = -1;
    protected boolean recycling;

    public EnclosingRow(TimeLayout<?, ?> timeLayout) {
        super((TimeLayoutBase<?, ?>) timeLayout);
        setObject((THIS) this); // The object is the row itself (convenient for compatibility with TimeLayoutUtil methods)
        header = new LazyObjectBounds<>((TimeLayoutBase<?, ?>) timeLayout) {
            @Override
            protected void layoutHorizontally() {
                layoutHeaderHorizontally(); // delegated to EnclosingRow.layoutHeaderHorizontally() so it can easily be overridden by superclasses
            }

            @Override
            protected void layoutVertically() {
                layoutHeaderVertically(); // delegated to EnclosingRow.layoutHeaderVertically() so it can easily be overridden by superclasses
            }

            @Override
            protected boolean horizontalVersionOp(boolean invalidate, boolean validate) {
                return headerHorizontalVersionOp(invalidate, validate);
            }
        };
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

    protected void onRecyclingStart() {
        header.invalidateObject();
        invalidateVerticalLayout();
    }

    protected void onRecyclingEnd() { }

    protected void throwExceptionIfNotRecycling() {
        if (!recycling)
            throw new IllegalStateException("This operation can be done only during recycling state");
    }

    public MutableBounds getHeader() {
        return header;
    }

    protected abstract boolean headerHorizontalVersionOp(boolean invalidate, boolean validate);

    protected abstract void layoutHeaderHorizontally();

    protected abstract void layoutHeaderVertically();


}