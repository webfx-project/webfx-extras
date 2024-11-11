package dev.webfx.extras.time.layout;

import dev.webfx.kit.util.properties.FXProperties;
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
        FXProperties.runOnPropertyChange(() -> {
            if (isLayouting())
                runnable.run();
        }, layoutCountProperty());
    }

    default void addOnAfterLayout(Runnable runnable) {
        FXProperties.runOnPropertyChange(() -> {
            if (!isLayouting())
                runnable.run();
        }, layoutCountProperty());
    }

}
