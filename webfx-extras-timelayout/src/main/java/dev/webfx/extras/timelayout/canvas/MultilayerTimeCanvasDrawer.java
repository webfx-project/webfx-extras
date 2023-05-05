package dev.webfx.extras.timelayout.canvas;

import dev.webfx.extras.timelayout.MultilayerTimeLayout;
import dev.webfx.extras.timelayout.TimeLayout;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bruno Salmon
 */
public class MultilayerTimeCanvasDrawer<T> extends CanvasDrawerBase {

    private final MultilayerTimeLayout<T> multilayerTimeLayout;
    private final Map<TimeLayout<?, T>, ChildDrawer<?>> childDrawers = new HashMap<>();

    public MultilayerTimeCanvasDrawer(MultilayerTimeLayout<T> multilayerTimeLayout) {
        this(new Canvas(), multilayerTimeLayout);
    }

    public MultilayerTimeCanvasDrawer(Canvas canvas, MultilayerTimeLayout<T> multilayerTimeLayout) {
        super(canvas);
        this.multilayerTimeLayout = multilayerTimeLayout;
        // We automatically redraw the canvas on each new layout pass
        multilayerTimeLayout.addOnAfterLayout(this::markDrawAreaAsDirty);
    }

    public <C> void setLayerChildDrawer(TimeLayout<C, T> timeLayout, ChildDrawer<C> childDrawer) {
        childDrawers.put(timeLayout, childDrawer);
        // We automatically redraw the canvas when the child selection changes to reflect the new selection
        timeLayout.selectedChildProperty().addListener(observable -> markDrawAreaAsDirty());
    }

    @Override
    protected void drawObjectsInArea() {
        Bounds bounds = getDrawAreaOrCanvasBounds();
        multilayerTimeLayout.getLayers().forEach(layer -> {
            if (layer.isVisible()) {
                ChildDrawer<?> childDrawer = childDrawers.get(layer);
                TimeCanvasDrawer.drawVisibleChildren(bounds, getLayoutOriginX(), getLayoutOriginY(), (TimeLayout) layer, (ChildDrawer) childDrawer, gc);
            }
        });
    }

}
