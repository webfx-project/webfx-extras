package dev.webfx.extras.time.layout;

import javafx.beans.value.ObservableIntegerValue;

public interface HasLayoutCountProperty {

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
