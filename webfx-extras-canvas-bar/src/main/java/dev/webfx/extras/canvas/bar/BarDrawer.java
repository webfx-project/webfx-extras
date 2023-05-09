package dev.webfx.extras.canvas.bar;

import dev.webfx.extras.geometry.Bounds;
import dev.webfx.extras.geometry.FXBoundsWrapper;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * @author Bruno Salmon
 */
public class BarDrawer {

    private double hPadding;
    private double radius;
    private Paint backgroundFill;
    private Paint stroke;
    private String topText, middleText, bottomText;
    private Font textFont, topTextFont, middleTextFont, bottomTextFont;
    private Paint textFill;
    private boolean clipText = true;

    public BarDrawer sethPadding(double hPadding) {
        this.hPadding = hPadding;
        return this;
    }

    public BarDrawer setRadius(double radius) {
        this.radius = radius;
        return this;
    }

    public BarDrawer setBackgroundFill(Paint backgroundFill) {
        this.backgroundFill = backgroundFill;
        return this;
    }

    public BarDrawer setStroke(Paint stroke) {
        this.stroke = stroke;
        return this;
    }

    public BarDrawer setTopText(String topText) {
        this.topText = topText;
        return this;
    }

    public BarDrawer setMiddleText(String middleText) {
        this.middleText = middleText;
        return this;
    }

    public BarDrawer setBottomText(String bottomText) {
        this.bottomText = bottomText;
        return this;
    }

    public BarDrawer setTextFont(Font textFont) {
        this.textFont = textFont;
        return this;
    }

    public BarDrawer setTopTextFont(Font topTextFont) {
        this.topTextFont = topTextFont;
        return this;
    }

    public BarDrawer setMiddleTextFont(Font middleTextFont) {
        this.middleTextFont = middleTextFont;
        return this;
    }

    public BarDrawer setBottomTextFont(Font bottomTextFont) {
        this.bottomTextFont = bottomTextFont;
        return this;
    }

    public BarDrawer setTextFill(Paint textFill) {
        this.textFill = textFill;
        return this;
    }

    public BarDrawer setClipText(boolean clipText) {
        this.clipText = clipText;
        return this;
    }

    public void drawBar(Bounds b, GraphicsContext gc) {
        if (stroke != null)
            BarUtil.fillStrokeRect(b, hPadding, backgroundFill, stroke, radius, gc);
        else
            BarUtil.fillRect(b, hPadding, backgroundFill, radius, gc);
        boolean hasMiddleText = middleText != null, hasTopText = topText != null, hasBottomText = bottomText != null, hasTopOrBottomText = hasTopText || hasBottomText, hasText = hasMiddleText || hasTopOrBottomText;
        if (hasText && (!clipText || getTextAreaWidth(b) > 5)) { // Unnecessary to draw text when doesn't fit (this skip makes a big performance improvement on big zoom out over many events - because the text clip operation is time-consuming)
            if (hasMiddleText) {
                setFirstNonNullFont(middleTextFont, textFont, gc);
                BarUtil.fillCenterText(b, hPadding, middleText, clipText, textFill, gc);
            }
            if (hasTopOrBottomText) {
                double h = b.getHeight(), h2 = h / 2, vPadding = h / 16;
                if (hasTopText) {
                    setFirstNonNullFont(topTextFont, textFont, gc);
                    BarUtil.fillText(b.getMinX(), b.getMinY() + vPadding, b.getWidth(), h2, hPadding, topText, clipText, textFill, VPos.CENTER, TextAlignment.CENTER, gc);
                }
                if (hasBottomText) {
                    setFirstNonNullFont(bottomTextFont, textFont, gc);
                    BarUtil.fillText(b.getMinX(), b.getMinY() + h2, b.getWidth(), h2 - vPadding, hPadding, bottomText, clipText, textFill, VPos.CENTER, TextAlignment.CENTER, gc);
                }
            }
        }
    }

    public double getTextAreaWidth(Bounds b) {
        return b.getWidth() - 2 * hPadding - 5;
    }

    private void setFirstNonNullFont(Font font1, Font font2, GraphicsContext gc) {
        gc.setFont(font1 != null ? font1 : font2);
    }

    // JavaFX bounds compatible API

    private static final FXBoundsWrapper FX_BOUNDS_WRAPPER = new FXBoundsWrapper();

    public void drawBar(javafx.geometry.Bounds b, GraphicsContext gc) {
        FX_BOUNDS_WRAPPER.setFxBounds(b);
        drawBar(FX_BOUNDS_WRAPPER, gc);
    }

    public double getTextAreaWidth(javafx.geometry.Bounds b) {
        FX_BOUNDS_WRAPPER.setFxBounds(b);
        return getTextAreaWidth(FX_BOUNDS_WRAPPER);
    }


}
