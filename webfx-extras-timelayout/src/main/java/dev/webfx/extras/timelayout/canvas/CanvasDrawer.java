package dev.webfx.extras.timelayout.canvas;

import javafx.scene.canvas.Canvas;

public interface CanvasDrawer {

    Canvas getCanvas();

    default void redraw() {
        draw(true);
    }

    default void draw(boolean clear) {
        Canvas canvas = getCanvas();
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        draw(width, height, clear);
    }

    void draw(double width, double height, boolean clear);

}
