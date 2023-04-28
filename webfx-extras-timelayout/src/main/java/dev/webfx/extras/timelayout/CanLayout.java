package dev.webfx.extras.timelayout;

import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableIntegerValue;

public interface CanLayout {

    DoubleProperty widthProperty();

    default double getWidth() {
        return widthProperty().get();
    }

    default void setWidth(double width) {
        widthProperty().set(width);
    }

    DoubleProperty heightProperty();

    default double getHeight() {
        return heightProperty().get();
    }

    default void setHeight(double height) {
        heightProperty().set(height);
    }

    default void resize(double width, double height) {
        setHeight(height); // Shouldn't trigger a layout pass
        setWidth(width);   // May trigger a layout pass, which may adjust back the height
    }

    void markLayoutAsDirty();

    boolean isLayoutDirty();

    default void layout(double width, double height) {
        resize(width, height);
        layoutIfDirty();
    }

    default void layoutIfDirty() {
        if (isLayoutDirty() && !isLayouting())
            layout();
    }

    void layout();

    ObservableIntegerValue layoutCountProperty();

    default int getLayoutCount() {
        return Math.abs(layoutCountProperty().get());
    }
    default boolean isLayouting() {
        return layoutCountProperty().get() < 0;
    }

    default void addOnBeforeLayout(Runnable runnable) {
        layoutCountProperty().addListener((observable, oldValue, newValue) -> {
            if (isLayouting())
                runnable.run();
        });
    }

    default void addOnAfterLayout(Runnable runnable) {
        layoutCountProperty().addListener((observable, oldValue, newValue) -> {
            if (!isLayouting())
                runnable.run();
        });
    }

}
