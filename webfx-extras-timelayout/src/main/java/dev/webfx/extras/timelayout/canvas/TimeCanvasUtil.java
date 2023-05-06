package dev.webfx.extras.timelayout.canvas;

import dev.webfx.extras.timelayout.CanLayout;
import dev.webfx.extras.bounds.Bounds;
import dev.webfx.extras.timelayout.canvas.generic.CanvasPane;
import dev.webfx.extras.timelayout.canvas.generic.CanvasRefresher;
import dev.webfx.extras.timelayout.canvas.generic.CanvasUtil;
import dev.webfx.extras.timelayout.canvas.generic.VirtualCanvasPane;
import dev.webfx.kit.util.properties.FXProperties;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;

/**
 * @author Bruno Salmon
 */
public final class TimeCanvasUtil {

    public static void fillRect(Bounds b, double hPadding, Paint fill, double radius, GraphicsContext gc) {
        CanvasUtil.fillRect(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight(), hPadding, fill, radius, gc);
    }

    public static void strokeRect(Bounds b, double hPadding, Paint stroke, double radius, GraphicsContext gc) {
        CanvasUtil.strokeRect(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight(), hPadding, stroke, radius, gc);
    }

    public static void fillStrokeRect(Bounds b, double hPadding, Paint fill, Paint stroke, double radius, GraphicsContext gc) {
        fillRect(b, hPadding, fill, radius, gc);
        strokeRect(b, hPadding, stroke, radius, gc);
    }

    public static void fillText(Bounds b, double hPadding, String text, Paint fill, VPos baseline, TextAlignment textAlignment, GraphicsContext gc) {
        CanvasUtil.fillText(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight(), hPadding, text, fill, baseline, textAlignment, gc);
    }

    public static void fillTopCenterText(Bounds b, double hPadding, String text, Paint fill, GraphicsContext gc) {
        fillText(b, hPadding, text, fill, VPos.TOP, TextAlignment.CENTER, gc);
    }

    public static void fillCenterLeftText(Bounds b, double hPadding, String text, Paint fill, GraphicsContext gc) {
        fillText(b, hPadding, text, fill, VPos.CENTER, TextAlignment.LEFT, gc);
    }

    public static void fillCenterText(Bounds b, double hPadding, String text, Paint fill, GraphicsContext gc) {
        fillText(b, hPadding, text, fill, VPos.CENTER, TextAlignment.CENTER, gc);
    }

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
