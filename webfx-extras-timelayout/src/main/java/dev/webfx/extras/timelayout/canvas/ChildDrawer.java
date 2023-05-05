package dev.webfx.extras.timelayout.canvas;

import dev.webfx.extras.timelayout.LayoutPosition;
import javafx.scene.canvas.GraphicsContext;

public interface ChildDrawer<C> {

    void drawChild(C child, LayoutPosition p, GraphicsContext gc);

}
