package dev.webfx.extras.timelayout.canvas;

import dev.webfx.extras.timelayout.CanLayout;
import dev.webfx.extras.timelayout.ChildPosition;
import dev.webfx.extras.timelayout.canvas.generic.CanvasPane;
import dev.webfx.extras.timelayout.canvas.generic.CanvasRefresher;
import dev.webfx.extras.timelayout.canvas.generic.CanvasUtil;
import dev.webfx.extras.timelayout.canvas.generic.VirtualCanvasPane;
import dev.webfx.kit.util.properties.FXProperties;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;

/**
 * @author Bruno Salmon
 */
public final class TimeCanvasUtil {

    public static void fillRect(ChildPosition<?> p, double hPadding, Paint fill, double radius, GraphicsContext gc) {
        CanvasUtil.fillRect(p.getX(), p.getY(), p.getWidth(), p.getHeight(), hPadding, fill, radius, gc);
    }

    public static void strokeRect(ChildPosition<?> p, double hPadding, Paint stroke, double radius, GraphicsContext gc) {
        CanvasUtil.strokeRect(p.getX(), p.getY(), p.getWidth(), p.getHeight(), hPadding, stroke, radius, gc);
    }

    public static void fillStrokeRect(ChildPosition<?> p, double hPadding, Paint fill, Paint stroke, double radius, GraphicsContext gc) {
        fillRect(p, hPadding, fill, radius, gc);
        strokeRect(p, hPadding, stroke, radius, gc);
    }

    public static void fillText(ChildPosition<?> p, double hPadding, String text, Paint fill, VPos baseline, TextAlignment textAlignment, GraphicsContext gc) {
        CanvasUtil.fillText(p.getX(), p.getY(), p.getWidth(), p.getHeight(), hPadding, text, fill, baseline, textAlignment, gc);
    }

    public static void fillTopCenterText(ChildPosition<?> p, double hPadding, String text, Paint fill, GraphicsContext gc) {
        fillText(p, hPadding, text, fill, VPos.TOP, TextAlignment.CENTER, gc);
    }

    public static void fillCenterLeftText(ChildPosition<?> p, double hPadding, String text, Paint fill, GraphicsContext gc) {
        fillText(p, hPadding, text, fill, VPos.CENTER, TextAlignment.LEFT, gc);
    }

    public static void fillCenterText(ChildPosition<?> p, double hPadding, String text, Paint fill, GraphicsContext gc) {
        fillText(p, hPadding, text, fill, VPos.CENTER, TextAlignment.CENTER, gc);
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

    public static VirtualCanvasPane createTimeVirtualCanvasPane(CanLayout layout, CanvasDrawer drawer, ObservableObjectValue<Bounds> viewportBoundsProperty, ObservableDoubleValue vvalueProperty) {
        VirtualCanvasPane virtualCanvasPane = new VirtualCanvasPane(drawer, createTimeCanvasRefresher(layout, drawer));
        virtualCanvasPane.activateVirtualCanvasMode(viewportBoundsProperty, vvalueProperty);
        FXProperties.runOnPropertiesChange(() -> virtualCanvasPane.setRequestedCanvasHeight(layout.getHeight()), layout.heightProperty());
        return virtualCanvasPane;
    }

}
