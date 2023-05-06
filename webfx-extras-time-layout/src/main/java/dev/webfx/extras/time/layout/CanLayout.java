package dev.webfx.extras.time.layout;

import dev.webfx.extras.geometry.HasWidthHeightProperties;

public interface CanLayout extends HasWidthHeightProperties, HasLayoutCountProperty {

    void layout();

    default void layout(double width, double height) {
        resize(width, height);
        layoutIfDirty();
    }

    default void layoutIfDirty() {
        if (isLayoutDirty() && !isLayouting())
            layout();
    }

    void markLayoutAsDirty();

    boolean isLayoutDirty();

}
