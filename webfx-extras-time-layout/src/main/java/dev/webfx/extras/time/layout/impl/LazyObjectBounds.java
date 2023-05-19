package dev.webfx.extras.time.layout.impl;

/**
 * This class allows lazy computation of ObjectBounds. When these bounds are accessed (via getX(), getWidth(), getY(),
 * getHeight(), etc...), the values are computed at that time if not already done or if they became invalid() due to
 * some changes in the TimeLayout. Subclasses must implement layoutHorizontally() and layoutVertically() to respectively
 * compute the horizontal (x & width) and vertical (y & height) values. They will probably do it by calling a method of
 * TimeLayout specialized for that object (because TimeLayout is the central computing point that holds all the numerous
 * fields that this computation may depend on). The validity of the horizontal and vertical values are simply checked
 * against the version numbers of the TimeLayout.
 *
 * This class is a common base class for both children and decorative objects (parent / grandparent rows & headers), but
 * children will have additional properties such end/start times & row/column indexes (see ChildBounds).
 *
 * @author Bruno Salmon
 */
public abstract class LazyObjectBounds<O> extends ObjectBounds<O> {

    protected final TimeLayoutBase<?, ?> timeLayout; // used here only for version matching. <O> is different from <C> for decorative objects, that's why the TimeLayout generics are not specified here
    protected int horizontalVersion, verticalVersion; // version numbers of this LazyObjectBounds instance

    public LazyObjectBounds(TimeLayoutBase<?, ?> timeLayout) {
        this.timeLayout = timeLayout;
        invalidateObject();
    }

    public final void setObject(O object) {
        if (object != getObject()) {
            invalidateObject();
            super.setObject(object);
        }
    }

    public void invalidateObject() {
        invalidateHorizontalLayout();
        invalidateVerticalLayout();
    }

    public final void invalidateHorizontalLayout() {
        horizontalVersionOp(true, false);
    }

    public final boolean isHorizontalLayoutValid() {
        return horizontalVersionOp(false, false);
    }

    public final void validateHorizontalLayout() {
        horizontalVersionOp(false, true);
    }

    protected boolean horizontalVersionOp(boolean invalidate, boolean validate) { // central method to ease override
        int freshVersion = invalidate ? -1 : timeLayout.horizontalVersion;
        if (invalidate || validate)
            horizontalVersion = freshVersion;
        return horizontalVersion == freshVersion;
    }

    public final void invalidateVerticalLayout() {
        verticalVersionOp(true, false);
    }

    public final boolean isVerticalLayoutValid() {
        return verticalVersionOp(false, false);
    }

    public final void validateVerticalLayout() {
        verticalVersionOp(false, true);
    }

    protected boolean verticalVersionOp(boolean invalidate, boolean validate) { // central method to ease override
        int freshVersion = invalidate ? -1 : timeLayout.verticalVersion;
        if (invalidate || validate)
            verticalVersion = freshVersion;
        return verticalVersion == freshVersion;
    }

    @Override
    public final double getX() {
        checkLazyHorizontalLayout();
        return super.getX();
    }

    @Override
    public final double getWidth() {
        checkLazyHorizontalLayout();
        return super.getWidth();
    }

    private void checkLazyHorizontalLayout() {
        if (!isHorizontalLayoutValid()) {
            layoutHorizontally();
            validateHorizontalLayout();
        }
    }

    protected abstract void layoutHorizontally();

    @Override
    public final double getY() {
        checkLazyVerticalLayout();
        return super.getY();
    }

    @Override
    public final double getHeight() {
        checkLazyVerticalLayout();
        return super.getHeight();
    }

    private void checkLazyVerticalLayout() {
        if (!isVerticalLayoutValid()) {
            layoutVertically();
            validateVerticalLayout();
        }
    }

    protected abstract void layoutVertically();

}
