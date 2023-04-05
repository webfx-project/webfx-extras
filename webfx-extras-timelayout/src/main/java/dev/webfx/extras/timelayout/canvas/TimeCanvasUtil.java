package dev.webfx.extras.timelayout.canvas;

import dev.webfx.extras.timelayout.ChildPosition;
import dev.webfx.kit.launcher.WebFxKitLauncher;
import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;

/**
 * @author Bruno Salmon
 */
public final class TimeCanvasUtil {

    public static void fillRect(ChildPosition<?> p, Paint fill, GraphicsContext gc) {
        fillRect(p.getX(), p.getY(), p.getWidth(), p.getHeight(), fill, gc);
    }

    public static void fillRect(double x, double y, double width, double height, Paint fill, GraphicsContext gc) {
        gc.setFill(fill);
        gc.fillRect(x, y, width, height);
    }

    public static void strokeRect(ChildPosition<?> p, Paint stroke, GraphicsContext gc) {
        strokeRect(p.getX(), p.getY(), p.getWidth(), p.getHeight(), stroke, gc);
    }

    public static void strokeRect(double x, double y, double width, double height, Paint stroke, GraphicsContext gc) {
        gc.setStroke(stroke);
        gc.setLineWidth(1 / Screen.getPrimary().getOutputScaleX());
        gc.strokeRect(x, y, width, height);
    }

    public static void fillStrokeRect(ChildPosition<?> p, Paint fill, Paint stroke, GraphicsContext gc) {
        fillRect(p, fill, gc);
        strokeRect(p, stroke, gc);
    }

    public static void fillStrokeRect(double x, double y, double width, double height, Paint fill, Paint stroke, GraphicsContext gc) {
        fillRect(x, y, width, height, fill, gc);
        strokeRect(x, y, width, height, stroke, gc);
    }

    public static void fillText(ChildPosition<?> p, String text, Paint fill, VPos baseline, TextAlignment textAlignment, GraphicsContext gc) {
        fillText(p.getX(), p.getY(), p.getWidth(), p.getHeight(), text, fill, baseline, textAlignment, gc);
    }

    public static void fillText(double x, double y, double width, double height, String text, Paint fill, VPos baseline, TextAlignment textAlignment, GraphicsContext gc) {
        gc.save();
        // clip makes canvas operations slower, so we do it only when necessary
        Bounds textBounds = WebFxKitLauncher.measureText(text, gc.getFont());
        if (textBounds.getWidth() > width) {
            gc.beginPath();
            gc.moveTo(x, y);
            gc.lineTo(x + width, y);
            gc.lineTo(x + width, y + height);
            gc.lineTo(x, y + height);
            gc.closePath();
            gc.clip();
        }
        gc.setFill(fill);
        gc.setTextBaseline(baseline);
        gc.setTextAlign(textAlignment);
        if (x < 0 && x + width > 0) {
            width += x;
            x = 0;
        } else if (x < gc.getCanvas().getWidth() && x + width > gc.getCanvas().getWidth()) {
            width = gc.getCanvas().getWidth() - x;
        }
        x += textAlignment == TextAlignment.CENTER ? width / 2 : 5;
        y += (baseline == VPos.CENTER ? height / 2 : 0);
        gc.fillText(text, x, y);
        gc.restore();
    }

    public static void fillTopCenterText(ChildPosition<?> p, String text, Paint fill, GraphicsContext gc) {
        fillText(p, text, fill, VPos.TOP, TextAlignment.CENTER, gc);
    }

    public static void fillCenterLeftText(ChildPosition<?> p, String text, Paint fill, GraphicsContext gc) {
        fillText(p, text, fill, VPos.CENTER, TextAlignment.LEFT, gc);
    }

    public static void fillCenterText(ChildPosition<?> p, String text, Paint fill, GraphicsContext gc) {
        fillText(p, text, fill, VPos.CENTER, TextAlignment.CENTER, gc);
    }

}
