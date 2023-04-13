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
        double canvasWidth = gc.getCanvas().getWidth();
        double visibleBarWidth = Math.min(x + width, canvasWidth) - Math.max(x, 0);
        boolean textWider = textBounds.getWidth() > visibleBarWidth;
        // Clipping the text when it is wider than the visible bar. Note: adding a clip path makes all canvas operation
        // much slower, so it's good to skip that step when not necessary (ie when the text is not wider than the bar).
        if (textWider) {
            gc.beginPath();
            gc.moveTo(x, y);
            gc.lineTo(x + width, y);
            gc.lineTo(x + width, y + height);
            gc.lineTo(x, y + height);
            gc.lineTo(x, y);
            gc.closePath();
            gc.clip();
        }
        gc.setFill(fill);
        gc.setTextBaseline(baseline);
        // TODO: comment these different cases
        if (x < 0 && x + width > 0) {
            if (textWider) {
                x = x + width - 5;
                textAlignment = TextAlignment.RIGHT;
            } else {
                textAlignment = TextAlignment.CENTER;
            }
        } else if (x < canvasWidth && x + width > canvasWidth) {
            if (textWider) {
                x += 5;
                textAlignment = TextAlignment.LEFT;
            } else {
                textAlignment = TextAlignment.CENTER;
            }
        } else if (textWider) {
            x += 5;
            textAlignment = TextAlignment.LEFT;
        }
        if (textAlignment == TextAlignment.CENTER)
            x = (Math.max(x, 0) + Math.min(canvasWidth, x + width)) / 2;
        if (baseline == VPos.CENTER)
            y += height / 2;
        gc.setTextAlign(textAlignment);
        gc.fillText(text, x, y);
        gc.restore(); // this includes removing the clip path is it was set
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
