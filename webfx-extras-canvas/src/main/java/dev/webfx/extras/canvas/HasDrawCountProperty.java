package dev.webfx.extras.canvas;

import dev.webfx.kit.util.properties.FXProperties;
import javafx.beans.value.ObservableIntegerValue;

public interface HasDrawCountProperty {

    ObservableIntegerValue drawCountProperty();

    default int getDrawCount() {
        return Math.abs(drawCountProperty().get());
    }

    default boolean isDrawing() {
        return drawCountProperty().get() < 0;
    }

    default void addOnBeforeDraw(Runnable runnable) {
        FXProperties.runOnPropertyChange(() -> {
            if (isDrawing())
                runnable.run();
        }, drawCountProperty());
    }

    default void addOnAfterDraw(Runnable runnable) {
        FXProperties.runOnPropertyChange(() -> {
            if (!isDrawing())
                runnable.run();
        }, drawCountProperty());
    }

}
