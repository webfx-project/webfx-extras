package dev.webfx.extras.time.layout.canvas;

import dev.webfx.extras.canvas.impl.CanvasDrawerBase;
import dev.webfx.extras.canvas.layer.ChildDrawer;
import dev.webfx.extras.canvas.layer.interact.CanvasInteractionManager;
import dev.webfx.extras.canvas.layer.interact.HasCanvasInteractionManager;
import dev.webfx.extras.time.layout.MultilayerTimeLayout;
import dev.webfx.extras.time.layout.TimeLayout;
import dev.webfx.platform.util.collection.Collections;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bruno Salmon
 */
public class MultilayerTimeCanvasDrawer<T extends Temporal> extends CanvasDrawerBase implements HasCanvasInteractionManager {

    private final MultilayerTimeLayout<T> multilayerTimeLayout;
    private final Map<TimeLayout<?, T>, ChildDrawer<?>> childDrawers = new HashMap<>();
    private final TemporalUnit temporalUnit;
    private CanvasInteractionManager canvasInteractionManager;

    public MultilayerTimeCanvasDrawer(MultilayerTimeLayout<T> multilayerTimeLayout, TemporalUnit temporalUnit) {
        this(new Canvas(), multilayerTimeLayout, temporalUnit);
    }

    public MultilayerTimeCanvasDrawer(Canvas canvas, MultilayerTimeLayout<T> multilayerTimeLayout, TemporalUnit temporalUnit) {
        super(canvas);
        this.multilayerTimeLayout = multilayerTimeLayout;
        this.temporalUnit = temporalUnit;
        // We automatically redraw the canvas on each new layout pass
        multilayerTimeLayout.setCanvasDirtyMarker(this::markDrawAreaAsDirty);
        // We apply the same translation animation as the time layout
        TimeCanvasUtil.bindTranslateXAnimation(multilayerTimeLayout, this);
    }

    public <C> void setLayerChildDrawer(TimeLayout<C, T> timeLayout, ChildDrawer<C> childDrawer) {
        childDrawers.put(timeLayout, childDrawer);
        // We automatically redraw the canvas when the child selection changes to reflect the new selection
        timeLayout.setCanvasDirtyMarker(this::markDrawAreaAsDirty);
    }

    @Override
    protected void drawObjectsInArea() {
        Bounds bounds = getDrawAreaOrCanvasBounds();
        // Using safe Collections.forEach() - otherwise ConcurrentModificationException have been noticed.
        Collections.forEach(multilayerTimeLayout.getLayers(), layer -> {
            if (layer.isVisible()) {
                ChildDrawer<?> childDrawer = childDrawers.get(layer);
                TimeCanvasDrawer.drawVisibleChildren(bounds, getOriginX(), getOriginY(), (TimeLayout) layer, (ChildDrawer) childDrawer, gc);
            }
        });
    }

    @Override
    public CanvasInteractionManager getCanvasInteractionManager() {
        if (canvasInteractionManager == null) {
            canvasInteractionManager = new CanvasInteractionManager(getCanvas());
            canvasInteractionManager.addHandler(new TimeCanvasInteractionHandler<>(multilayerTimeLayout, temporalUnit, multilayerTimeLayout));
        }
        return canvasInteractionManager;
    }
}
