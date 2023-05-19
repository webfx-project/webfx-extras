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

    public void setObject(O object) {
        if (object != getObject()) {
            invalidateObject();
            super.setObject(object);
        }
    }

    public void invalidateObject() {
        invalidateHorizontalLayout();
        invalidateVerticalLayout();
    }

    public void invalidateHorizontalLayout() {
        horizontalVersion = -1;
    }

    public boolean isHorizontalLayoutValid() {
        return horizontalVersion == timeLayout.horizontalVersion;
    }

    public void validateHorizontalLayout() {
        horizontalVersion = timeLayout.horizontalVersion;
    }

    public void invalidateVerticalLayout() {
        verticalVersion = -1;
    }

    public boolean isVerticalLayoutValid() {
        return verticalVersion == timeLayout.verticalVersion;
    }

    public void validateVerticalLayout() {
        verticalVersion = timeLayout.verticalVersion;
    }

    @Override
    public double getX() {
        checkLazyHorizontalLayout();
        return super.getX();
    }

    @Override
    public double getWidth() {
        checkLazyHorizontalLayout();
        return super.getWidth();
    }

    protected void checkLazyHorizontalLayout() {
        if (!isHorizontalLayoutValid()) {
            layoutHorizontally();
            validateHorizontalLayout();
        }
    }

    protected abstract void layoutHorizontally();

    @Override
    public double getY() {
        checkLazyVerticalLayout();
        return super.getY();
    }

    @Override
    public double getHeight() {
        checkLazyVerticalLayout();
        return super.getHeight();
    }

    protected void checkLazyVerticalLayout() {
        if (!isVerticalLayoutValid()) {
            layoutVertically();
            validateVerticalLayout();
        }
    }

    protected abstract void layoutVertically();

}
