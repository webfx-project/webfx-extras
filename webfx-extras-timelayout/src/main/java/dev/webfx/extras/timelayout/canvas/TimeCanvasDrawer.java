package dev.webfx.extras.timelayout.canvas;

import dev.webfx.extras.timelayout.ChildPosition;
import dev.webfx.extras.timelayout.TimeLayout;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * @author Bruno Salmon
 */
public class TimeCanvasDrawer<C, T> implements CanvasDrawer {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final TimeLayout<C, T> timeLayout;
    private final ChildDrawer<C, T> childDrawer;

    public TimeCanvasDrawer(Canvas canvas, TimeLayout<C, T> timeLayout, ChildDrawer<C, T> childDrawer) {
        this(canvas.getGraphicsContext2D(), timeLayout, childDrawer);
    }

    public TimeCanvasDrawer(GraphicsContext gc, TimeLayout<C, T> timeLayout, ChildDrawer<C, T> childDrawer) {
        this.gc = gc;
        this.canvas = gc.getCanvas();
        this.timeLayout = timeLayout;
        this.childDrawer = childDrawer;
    }

    @Override
    public Canvas getCanvas() {
        return canvas;
    }

    @Override
    public void draw(double width, double height, boolean clear) {
        if (clear)
            gc.clearRect(0, 0, width, height);
        draw(width, height, timeLayout, childDrawer, gc);
    }

    static <C, T> void draw(double width, double height, TimeLayout<C, T> timeLayout, ChildDrawer<C, T> childDrawer, GraphicsContext gc) {
        timeLayout.layout(width, height);
        ObservableList<C> children = timeLayout.getChildren();
        for (int i = 0; i < children.size(); i++) {
            C child = children.get(i);
            ChildPosition<T> p = timeLayout.getChildPosition(i);
            // Skipping canvas draw operations for children whose position is outside the canvas
            if (p.getX() + p.getWidth() < 0 || p.getX() > width || p.getY() + p.getHeight() < 0 || p.getY() > height)
                continue; // This improves performance, as canvas operations can take time (even outside canvas)
            gc.save();
            childDrawer.drawChild(child, p, gc);
            gc.restore();
        }
    }
}
