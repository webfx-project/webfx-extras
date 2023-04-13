package dev.webfx.extras.timelayout.canvas;

import dev.webfx.extras.timelayout.ChildPosition;
import dev.webfx.extras.timelayout.TimeLayout;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * @author Bruno Salmon
 */
public class TimeCanvasDrawer<C, T> {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final TimeLayout<C, T> timeLayout;
    private final ChildCanvasDrawer<C, T> childCanvasDrawer;

    public TimeCanvasDrawer(Canvas canvas, TimeLayout<C, T> timeLayout, ChildCanvasDrawer<C, T> childCanvasDrawer) {
        this(canvas.getGraphicsContext2D(), timeLayout, childCanvasDrawer);
    }

    public TimeCanvasDrawer(GraphicsContext gc, TimeLayout<C, T> timeLayout, ChildCanvasDrawer<C, T> childCanvasDrawer) {
        this.gc = gc;
        this.canvas = gc.getCanvas();
        this.timeLayout = timeLayout;
        this.childCanvasDrawer = childCanvasDrawer;
    }

    public void draw(boolean clearCanvas) {
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        if (clearCanvas)
            gc.clearRect(0, 0, width, height);
        draw(width, height, timeLayout, childCanvasDrawer, gc);
    }

    static <C, T> void draw(double width, double height, TimeLayout<C, T> timeLayout, ChildCanvasDrawer<C, T> childCanvasDrawer, GraphicsContext gc) {
        timeLayout.layout(width, height);
        ObservableList<C> children = timeLayout.getChildren();
        for (int i = 0; i < children.size(); i++) {
            C child = children.get(i);
            ChildPosition<T> p = timeLayout.getChildPosition(i);
            // Skipping canvas draw operations for children whose position is outside the canvas
            if (p.getX() + p.getWidth() < 0 || p.getX() > width || p.getY() + p.getHeight() < 0 || p.getY() > height)
                continue; // This improves performance, as canvas operations can take time (even outside canvas)
            gc.save();
            childCanvasDrawer.drawChild(child, p, gc);
            gc.restore();
        }
    }
}
