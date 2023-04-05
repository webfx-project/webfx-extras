package dev.webfx.extras.timelayout.canvas;

import dev.webfx.extras.timelayout.ChildPosition;
import javafx.scene.canvas.GraphicsContext;

public interface ChildCanvasDrawer<C, T> {

    void drawChild(C child, ChildPosition<T> childPosition, GraphicsContext gc);

}
