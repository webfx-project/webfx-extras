package dev.webfx.extras.timelayout.canvas;

import dev.webfx.extras.bounds.Bounds;
import javafx.scene.canvas.GraphicsContext;

public interface ChildDrawer<C> {

    void drawChild(C child, Bounds b, GraphicsContext gc);

}
