package dev.webfx.extras.time.layout.canvas;

import dev.webfx.extras.canvas.CanvasDrawer;
import dev.webfx.extras.canvas.pane.CanvasPane;
import dev.webfx.extras.canvas.pane.CanvasRefresher;
import dev.webfx.extras.canvas.pane.VirtualCanvasPane;
import dev.webfx.extras.time.layout.CanLayout;
import dev.webfx.kit.util.properties.FXProperties;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableObjectValue;

/**
 * @author Bruno Salmon
 */
public final class TimeCanvasUtil {

    public static CanvasPane createTimeCanvasPane(CanLayout layout, CanvasDrawer drawer) {
        CanvasPane canvasPane = new CanvasPane(drawer, createTimeCanvasRefresher(layout, drawer));
        FXProperties.runOnPropertiesChange(() -> canvasPane.setRequestedCanvasHeight(layout.getHeight()), layout.heightProperty());
        return canvasPane;
    }

    private static CanvasRefresher createTimeCanvasRefresher(CanLayout layout, CanvasDrawer drawer) {
        return (virtualCanvasWidth, virtualCanvasHeight, virtualViewPortY, canvasSizeChanged) -> {
            int drawCount = drawer.getDrawCount();
            drawer.setLayoutOriginY(virtualViewPortY);
            if (canvasSizeChanged)
                layout.resize(virtualCanvasWidth, virtualCanvasHeight); // May cause a layout and also possibly a redraw
            if (canvasSizeChanged && drawer.getDrawCount() == drawCount)
                drawer.markDrawAreaAsDirty();
        };
    }

    public static VirtualCanvasPane createTimeVirtualCanvasPane(CanLayout layout, CanvasDrawer drawer, ObservableObjectValue<javafx.geometry.Bounds> viewportBoundsProperty, ObservableDoubleValue vvalueProperty) {
        VirtualCanvasPane virtualCanvasPane = new VirtualCanvasPane(drawer, createTimeCanvasRefresher(layout, drawer));
        virtualCanvasPane.activateVirtualCanvasMode(viewportBoundsProperty, vvalueProperty);
        FXProperties.runOnPropertiesChange(() -> virtualCanvasPane.setRequestedCanvasHeight(layout.getHeight()), layout.heightProperty());
        return virtualCanvasPane;
    }

}
