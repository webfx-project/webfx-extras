package dev.webfx.extras.timelayout.bar;

import dev.webfx.extras.timelayout.ChildPosition;
import dev.webfx.extras.timelayout.canvas.TimeCanvasUtil;
import dev.webfx.extras.timelayout.canvas.generic.CanvasUtil;
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

    public void sethPadding(double hPadding) {
        this.hPadding = hPadding;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setBackgroundFill(Paint backgroundFill) {
        this.backgroundFill = backgroundFill;
    }

    public void setStroke(Paint stroke) {
        this.stroke = stroke;
    }

    public void setTopText(String topText) {
        this.topText = topText;
    }

    public void setMiddleText(String middleText) {
        this.middleText = middleText;
    }

    public void setBottomText(String bottomText) {
        this.bottomText = bottomText;
    }

    public void setTextFont(Font textFont) {
        this.textFont = textFont;
    }

    public void setTopTextFont(Font topTextFont) {
        this.topTextFont = topTextFont;
    }

    public void setMiddleTextFont(Font middleTextFont) {
        this.middleTextFont = middleTextFont;
    }

    public void setBottomTextFont(Font bottomTextFont) {
        this.bottomTextFont = bottomTextFont;
    }

    public void setTextFill(Paint textFill) {
        this.textFill = textFill;
    }

    public void drawBar(ChildPosition<?> p, GraphicsContext gc) {
        if (stroke != null)
            TimeCanvasUtil.fillStrokeRect(p, hPadding, backgroundFill, stroke, radius, gc);
        else
            TimeCanvasUtil.fillRect(p, hPadding, backgroundFill, radius, gc);
        boolean hasMiddleText = middleText != null, hasTopText = topText != null, hasBottomText = bottomText != null, hasTopOrBottomText = hasTopText || hasBottomText, hasText = hasMiddleText || hasTopOrBottomText;
        if (hasText && getTextAreaWidth(p) > 5) { // Unnecessary to draw text when doesn't fit (this skip makes a big performance improvement on big zoom out over many events - because the text clip operation is time-consuming)
            if (hasMiddleText) {
                setFirstNonNullFont(middleTextFont, textFont, gc);
                TimeCanvasUtil.fillCenterText(p, hPadding, middleText, textFill, gc);
            }
            if (hasTopOrBottomText) {
                double h = p.getHeight(), h2 = h / 2, vPadding = h / 16;
                if (hasTopText) {
                    setFirstNonNullFont(topTextFont, textFont, gc);
                    CanvasUtil.fillText(p.getX(), p.getY() + vPadding, p.getWidth(), h2, hPadding, topText, textFill, VPos.CENTER, TextAlignment.CENTER, gc);
                }
                if (hasBottomText) {
                    setFirstNonNullFont(bottomTextFont, textFont, gc);
                    CanvasUtil.fillText(p.getX(), p.getY() + h2, p.getWidth(), h2 - vPadding, hPadding, bottomText, textFill, VPos.CENTER, TextAlignment.CENTER, gc);
                }
            }
        }
    }

    public double getTextAreaWidth(ChildPosition<?> p) {
        return p.getWidth() - 2 * hPadding - 5;
    }

    private void setFirstNonNullFont(Font font1, Font font2, GraphicsContext gc) {
        gc.setFont(font1 != null ? font1 : font2);
    }

}
