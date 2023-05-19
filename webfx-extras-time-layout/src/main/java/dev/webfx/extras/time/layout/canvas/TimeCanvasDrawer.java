package dev.webfx.extras.time.layout.canvas;

import dev.webfx.extras.canvas.impl.CanvasDrawerBase;
import dev.webfx.extras.canvas.layer.ChildDrawer;
import dev.webfx.extras.canvas.layer.interact.CanvasInteractionManager;
import dev.webfx.extras.canvas.layer.interact.HasCanvasInteractionManager;
import dev.webfx.extras.time.layout.TimeLayout;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;

/**
 * @author Bruno Salmon
 */
public class TimeCanvasDrawer<C, T extends Temporal> extends CanvasDrawerBase implements HasCanvasInteractionManager {
    private final TimeLayout<C, T> timeLayout;
    private final ChildDrawer<C> childDrawer;
    private final TemporalUnit temporalUnit;
    private CanvasInteractionManager canvasInteractionManager;

    public TimeCanvasDrawer(TimeLayout<C, T> timeLayout, ChildDrawer<C> childDrawer, TemporalUnit temporalUnit) {
        this(new Canvas(), timeLayout, childDrawer, temporalUnit);
    }

    public TimeCanvasDrawer(Canvas canvas, TimeLayout<C, T> timeLayout, ChildDrawer<C> childDrawer, TemporalUnit temporalUnit) {
        super(canvas);
        this.timeLayout = timeLayout;
        this.childDrawer = childDrawer;
        this.temporalUnit = temporalUnit;
        timeLayout.addOnAfterLayout(this::markDrawAreaAsDirty);
        timeLayout.selectedChildProperty().addListener(observable -> markDrawAreaAsDirty());
    }

    @Override
    public CanvasInteractionManager getCanvasInteractionManager() {
        if (canvasInteractionManager == null) {
            canvasInteractionManager = new CanvasInteractionManager(getCanvas());
            canvasInteractionManager.addHandler(new TimeCanvasInteractionHandler<>(timeLayout, temporalUnit, timeLayout));
        }
        return canvasInteractionManager;
    }

    @Override
    protected void drawObjectsInArea() {
        drawVisibleChildren(getDrawAreaOrCanvasBounds(), getLayoutOriginX(), getLayoutOriginY(), timeLayout, childDrawer, gc);
    }

    static <C, T> void drawVisibleChildren(javafx.geometry.Bounds drawAreaBounds, double layoutOriginX, double layoutOriginY, TimeLayout<C, T> timeLayout, ChildDrawer<C> childDrawer, GraphicsContext gc) {
        // Translating the canvas to consider the effect of the possible layout origin coordinate changes
        gc.save();
        gc.translate(-layoutOriginX, -layoutOriginY);
        timeLayout.processVisibleChildren(drawAreaBounds, layoutOriginX, layoutOriginY, (child, b) -> {
            gc.save();
            childDrawer.drawChild(child, b, gc);
            gc.restore();
        });
        // Restoring the canvas context (rolls back the translation)
        gc.restore();
    }
}
