package dev.webfx.extras.canvas.layer;

import dev.webfx.extras.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;

public interface ChildDrawer<T> {

    void drawChild(T child, Bounds b, GraphicsContext gc);

}
