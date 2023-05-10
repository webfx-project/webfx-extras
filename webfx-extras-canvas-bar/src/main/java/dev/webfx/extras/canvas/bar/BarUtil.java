package dev.webfx.extras.canvas.bar;

import dev.webfx.extras.geometry.Bounds;
import dev.webfx.kit.launcher.WebFxKitLauncher;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;

/**
 * @author Bruno Salmon
 */
public final class BarUtil {

    public static void fillRect(double x, double y, double width, double height, double hPadding, Paint fill, double radius, GraphicsContext gc) {
        if (fill == null || fill == Color.TRANSPARENT)
            return;
        gc.setFill(fill);
        if (hPadding > 0) {
            x += hPadding;
            width -= 2 * hPadding;
        }
        if (radius > 0)
            gc.fillRoundRect(x, y, width, height, radius, radius);
        else
            gc.fillRect(x, y, width, height);
    }

    public static void strokeRect(double x, double y, double width, double height, double hPadding, Paint stroke, double radius, GraphicsContext gc) {
        if (stroke == null || stroke == Color.TRANSPARENT)
            return;
        gc.setStroke(stroke);
        gc.setLineWidth(1 / Screen.getPrimary().getOutputScaleX());
        if (hPadding > 0) {
            x += hPadding;
            width -= 2 * hPadding;
        }
        if (radius > 0)
            gc.strokeRoundRect(x, y, width, height, radius, radius);
        else
            gc.strokeRect(x, y, width, height);
    }

    public static void fillStrokeRect(double x, double y, double width, double height, double hPadding, Paint fill, Paint stroke, double radius, GraphicsContext gc) {
        fillRect(x, y, width, height, hPadding, fill, radius, gc);
        strokeRect(x, y, width, height, hPadding, stroke, radius, gc);
    }

    public static void fillText(double x, double y, double width, double height, double hPadding, String text, boolean clipText, Paint fill, VPos baseline, TextAlignment textAlignment, GraphicsContext gc) {
        if (hPadding > 0) {
            x += hPadding;
            width -= 2 * hPadding;
        }
        double hTextPadding = 5; // hardcoded value for now
        gc.save();
        // clip makes canvas operations slower, so we do it only when necessary
        javafx.geometry.Bounds textBounds = WebFxKitLauncher.measureText(text, gc.getFont());
        double canvasWidth = gc.getCanvas().getWidth();
        double visibleBarWidth = Math.min(x + width, canvasWidth) - Math.max(x, 0);
        boolean textWider = textBounds.getWidth() + 2 * hTextPadding > visibleBarWidth;
        // Clipping the text when it is wider than the visible bar. Note: adding a clip path makes all canvas operation
        // much slower, so it's good to skip that step when not necessary (ie when the text is not wider than the bar).
        if (clipText && textWider)
            clipRect(x, y, width, height, gc);
        gc.setFill(fill);
        gc.setTextBaseline(baseline);
        // TODO: comment these different cases
        if (x < 0 && x + width > 0) {
            if (textWider) {
                x = x + width - hTextPadding;
                textAlignment = TextAlignment.RIGHT;
            } else {
                textAlignment = TextAlignment.CENTER;
            }
        } else if (x < canvasWidth && x + width > canvasWidth) {
            if (textWider) {
                x += hTextPadding;
                textAlignment = TextAlignment.LEFT;
            } else {
                textAlignment = TextAlignment.CENTER;
            }
        } else if (textWider) {
            x += hTextPadding;
            textAlignment = TextAlignment.LEFT;
        }
        if (textAlignment == TextAlignment.CENTER)
            x = (Math.max(x, 0) + Math.min(canvasWidth, x + width)) / 2;
        if (baseline == VPos.CENTER)
            y += height / 2;
        gc.setTextAlign(textAlignment);
        gc.fillText(text, x, y);
        gc.restore(); // this includes removing the clip path if set
    }

    public static void fillRect(Bounds b, double hPadding, Paint fill, double radius, GraphicsContext gc) {
        fillRect(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight(), hPadding, fill, radius, gc);
    }

    public static void strokeRect(Bounds b, double hPadding, Paint stroke, double radius, GraphicsContext gc) {
        strokeRect(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight(), hPadding, stroke, radius, gc);
    }

    public static void fillStrokeRect(Bounds b, double hPadding, Paint fill, Paint stroke, double radius, GraphicsContext gc) {
        fillRect(b, hPadding, fill, radius, gc);
        strokeRect(b, hPadding, stroke, radius, gc);
    }

    public static void fillText(Bounds b, double hPadding, String text, boolean clipText, Paint fill, VPos baseline, TextAlignment textAlignment, GraphicsContext gc) {
        fillText(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight(), hPadding, text, clipText, fill, baseline, textAlignment, gc);
    }

    public static void fillTopCenterText(Bounds b, double hPadding, String text, boolean clipText, Paint fill, GraphicsContext gc) {
        fillText(b, hPadding, text, clipText, fill, VPos.TOP, TextAlignment.CENTER, gc);
    }

    public static void fillCenterLeftText(Bounds b, double hPadding, String text, boolean clipText, Paint fill, GraphicsContext gc) {
        fillText(b, hPadding, text, clipText, fill, VPos.CENTER, TextAlignment.LEFT, gc);
    }

    public static void fillCenterText(Bounds b, double hPadding, String text, boolean clipText, Paint fill, GraphicsContext gc) {
        fillText(b, hPadding, text, clipText, fill, VPos.CENTER, TextAlignment.CENTER, gc);
    }

    public static void clipRect(double x, double y, double width, double height, GraphicsContext gc) {
        gc.beginPath();
        gc.moveTo(x, y);
        gc.lineTo(x + width, y);
        gc.lineTo(x + width, y + height);
        gc.lineTo(x, y + height);
        gc.lineTo(x, y);
        gc.closePath();
        gc.clip();
    }
}
