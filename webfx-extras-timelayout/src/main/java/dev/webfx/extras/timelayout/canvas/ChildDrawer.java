package dev.webfx.extras.timelayout.canvas;

import dev.webfx.extras.timelayout.ChildPosition;
import javafx.scene.canvas.GraphicsContext;

public interface ChildDrawer<C, T> {

    void drawChild(C child, ChildPosition p, GraphicsContext gc);

}
