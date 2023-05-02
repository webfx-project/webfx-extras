package dev.webfx.extras.timelayout.canvas.generic;

import dev.webfx.kit.launcher.WebFxKitLauncher;
import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;

/**
 * @author Bruno Salmon
 */
public class CanvasUtil {

    public static void fillRect(double x, double y, double width, double height, double hPadding, Paint fill, double radius, GraphicsContext gc) {
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
        if (stroke == Color.TRANSPARENT)
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

    public static void fillText(double x, double y, double width, double height, double hPadding, String text, Paint fill, VPos baseline, TextAlignment textAlignment, GraphicsContext gc) {
        if (hPadding > 0) {
            x += hPadding;
            width -= 2 * hPadding;
        }
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
        gc.restore(); // this includes removing the clip path if set
    }
}
