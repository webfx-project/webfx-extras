package dev.webfx.extras.time.layout.canvas;

import dev.webfx.extras.canvas.impl.CanvasDrawerBase;
import dev.webfx.extras.canvas.layer.ChildDrawer;
import dev.webfx.extras.canvas.layer.interact.CanvasInteractionManager;
import dev.webfx.extras.canvas.layer.interact.HasCanvasInteractionManager;
import dev.webfx.extras.geometry.Bounds;
import dev.webfx.extras.layer.interact.TranslatedCanSelectChild;
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

    public TimeCanvasDrawer(TimeLayout<C, T> timeLayout, ChildDrawer<C> childDrawer) {
        this(new Canvas(), timeLayout, childDrawer);
    }

    public TimeCanvasDrawer(Canvas canvas, TimeLayout<C, T> timeLayout, ChildDrawer<C> childDrawer) {
        super(canvas);
        this.timeLayout = timeLayout;
        this.childDrawer = childDrawer;
        this.temporalUnit = timeLayout.getTimeProjector().getTemporalUnit();
        timeLayout.setCanvasDirtyMarker(this::markDrawAreaAsDirty);
    }

    @Override
    public CanvasInteractionManager getCanvasInteractionManager() {
        if (canvasInteractionManager == null) {
            canvasInteractionManager = new CanvasInteractionManager(getCanvas());
            canvasInteractionManager.addHandler(new TimeCanvasInteractionHandler<>(timeLayout, temporalUnit, new TranslatedCanSelectChild<>(timeLayout, this::getOriginX, this::getOriginY)));
        }
        return canvasInteractionManager;
    }

    @Override
    protected void drawObjectsInArea() {
        drawVisibleChildren(getDrawAreaOrCanvasBounds(), getOriginX(), getOriginY(), timeLayout, childDrawer, gc);
    }

    static <C, T> void drawVisibleChildren(javafx.geometry.Bounds drawAreaBounds, double originX, double originY, TimeLayout<C, T> timeLayout, ChildDrawer<C> childDrawer, GraphicsContext gc) {
        //Console.log("drawVisibleChildren() - " + timeLayout);
        // Translating the canvas to consider the effect of the possible layout origin coordinate changes
        gc.save();
        gc.translate(-originX, -originY);
        // Then processing the visible children
        timeLayout.processVisibleChildren(drawAreaBounds, originX, originY, (child, b) -> {
            gc.save();
            // Before drawing the child, clipping it to the parent row clip bounds if necessary. Ex: if a parent row is
            // collapsed (fully or partially), then the child should not be drawn outside the parent row bounds.
            Bounds parentRowClipBounds = timeLayout.getClippingParentRowBounds(child); // returns null if fully expanded, or the bounds of the parent row if partially collapsed
            if (parentRowClipBounds != null) {
                gc.beginPath();
                // we remove timeLayout.getVSpacing() from the height, otherwise we can see the start of the second row
                // when the parent row is fully collapsed (which is not what we want).
                gc.rect(parentRowClipBounds.getMinX(), parentRowClipBounds.getMinY(), parentRowClipBounds.getWidth(), parentRowClipBounds.getHeight() - timeLayout.getVSpacing());
                gc.clip();
            }
            childDrawer.drawChild(child, b, gc);
            gc.restore();
        });
        // Restoring the canvas context (rolls back the translation)
        gc.restore();
    }
}
