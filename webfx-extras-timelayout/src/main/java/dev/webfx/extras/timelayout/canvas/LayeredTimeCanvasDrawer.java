package dev.webfx.extras.timelayout.canvas;

import dev.webfx.extras.timelayout.LayeredTimeLayout;
import dev.webfx.extras.timelayout.TimeLayout;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bruno Salmon
 */
public class LayeredTimeCanvasDrawer<T> {

    private final Canvas canvas;
    private final GraphicsContext gc;
    private final LayeredTimeLayout<T> layeredTimeLayout;
    private final Map<TimeLayout<?, T>, ChildCanvasDrawer<?, T>> childCanvasDrawers = new HashMap<>();

    public LayeredTimeCanvasDrawer(Canvas canvas, LayeredTimeLayout<T> layeredTimeLayout) {
        this(canvas.getGraphicsContext2D(), layeredTimeLayout);
    }

    public LayeredTimeCanvasDrawer(GraphicsContext gc, LayeredTimeLayout<T> layeredTimeLayout) {
        this.canvas = gc.getCanvas();
        this.gc = gc;
        this.layeredTimeLayout = layeredTimeLayout;
    }

    public <C> void setLayerChildCanvasDrawer(TimeLayout<C, T> timeLayout, ChildCanvasDrawer<C, T> childCanvasDrawer) {
        childCanvasDrawers.put(timeLayout, childCanvasDrawer);
    }

    public void draw(boolean clearCanvas) {
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        if (clearCanvas)
            gc.clearRect(0, 0, width, height);
        layeredTimeLayout.getLayers().forEach(layer -> {
            ChildCanvasDrawer<?, T> childCanvasDrawer = childCanvasDrawers.get(layer);
            TimeCanvasDrawer.draw(width, height, (TimeLayout) layer, (ChildCanvasDrawer) childCanvasDrawer, gc);
        });
    }

}
