package dev.webfx.extras.canvas;

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
        drawCountProperty().addListener((observable, oldValue, newValue) -> {
            if (isDrawing())
                runnable.run();
        });
    }

    default void addOnAfterDraw(Runnable runnable) {
        drawCountProperty().addListener((observable, oldValue, newValue) -> {
            if (!isDrawing())
                runnable.run();
        });
    }

}
