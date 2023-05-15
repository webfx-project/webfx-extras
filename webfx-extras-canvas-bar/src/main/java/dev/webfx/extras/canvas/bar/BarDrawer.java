package dev.webfx.extras.canvas.bar;

import dev.webfx.extras.geometry.Bounds;
import dev.webfx.extras.geometry.FXBoundsWrapper;
import dev.webfx.kit.launcher.WebFxKitLauncher;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;

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
    private TextAlignment textAlignment = TextAlignment.CENTER;
    private boolean clipText = true;
    private Image iconImage;
    private SVGPath iconSvgPath;
    private double iconSvgWidth, iconSvgHeight; // temporary settings provided by application until WebFX can correctly measure SVGPath width & height
    private Pos iconPos;
    private HPos iconHAlignment;
    private VPos iconVAlignement;
    private double iconTranslateX;
    private double iconTranslateY;

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

    public BarDrawer setTextAlignment(TextAlignment textAlignment) {
        this.textAlignment = textAlignment;
        return this;
    }

    public BarDrawer setClipText(boolean clipText) {
        this.clipText = clipText;
        return this;
    }

    public BarDrawer setIconImage(Image iconImage) {
        this.iconImage = iconImage;
        return this;
    }

    public BarDrawer setIconSVGPath(SVGPath iconSvgPath, double iconSvgWidth, double iconSvgHeight) {
        this.iconSvgPath = iconSvgPath;
        this.iconSvgWidth = iconSvgWidth;
        this.iconSvgHeight = iconSvgHeight;
        return this;
    }

    public BarDrawer setIconPos(Pos iconPos) {
        this.iconPos = iconPos;
        return this;
    }

    public BarDrawer setIconHAlignment(HPos iconHAlignment) {
        this.iconHAlignment = iconHAlignment;
        return this;
    }

    public BarDrawer setIconVAlignement(VPos iconVAlignement) {
        this.iconVAlignement = iconVAlignement;
        return this;
    }

    // Icon image API
    public BarDrawer setIcon(Image iconImage, Pos iconPos, HPos iconHAlignment, VPos iconVAlignement, double iconTranslateX, double iconTranslateY) {
        this.iconTranslateX = iconTranslateX;
        this.iconTranslateY = iconTranslateY;
        return setIconImage(iconImage)
                .setIconPos(iconPos)
                .setIconHAlignment(iconHAlignment)
                .setIconVAlignement(iconVAlignement);
    }

    public BarDrawer setIcon(Image iconImage, Pos iconPos, HPos iconHAlignment, VPos iconVAlignement) {
        return setIcon(iconImage, iconPos, iconHAlignment, iconVAlignement, 0, 0);
    }

    // Icon SVG path API

    public BarDrawer setIcon(SVGPath iconSvgPath, double iconSvgWidth, double iconSvgHeight, Pos iconPos, HPos iconHAlignment, VPos iconVAlignement, double iconTranslateX, double iconTranslateY) {
        this.iconTranslateX = iconTranslateX;
        this.iconTranslateY = iconTranslateY;
        return setIconSVGPath(iconSvgPath, iconSvgWidth, iconSvgHeight)
                .setIconPos(iconPos)
                .setIconHAlignment(iconHAlignment)
                .setIconVAlignement(iconVAlignement);
    }

    public BarDrawer setIcon(SVGPath iconSvgPath, double iconSvgWidth, double iconSvgHeight, Pos iconPos, HPos iconHAlignment, VPos iconVAlignement) {
        return setIcon(iconSvgPath, iconSvgWidth, iconSvgHeight, iconPos, iconHAlignment, iconVAlignement, 0, 0);
    }

    // Alternative Icon SVG path API with string content + fill

    public BarDrawer setIcon(String iconSvgPathContent, Paint fill, double iconSvgWidth, double iconSvgHeight, Pos iconPos, HPos iconHAlignment, VPos iconVAlignement, double iconTranslateX, double iconTranslateY) {
        SVGPath iconSvgPath = new SVGPath();
        iconSvgPath.setContent(iconSvgPathContent);
        iconSvgPath.setFill(fill);
        return setIcon(iconSvgPath, iconSvgWidth, iconSvgHeight, iconPos, iconHAlignment, iconVAlignement, iconTranslateX, iconTranslateY);
    }

    public BarDrawer setIcon(String iconSvgPathContent, Paint fill, double iconSvgWidth, double iconSvgHeight, Pos iconPos, HPos iconHAlignment, VPos iconVAlignement) {
        return setIcon(iconSvgPathContent, fill, iconSvgWidth, iconSvgHeight, iconPos, iconHAlignment, iconVAlignement, 0, 0);
    }

    public BarDrawer drawBar(Bounds b, GraphicsContext gc) {
        return drawBackground(b, gc)
                .drawIcon(b, gc)
                .drawTexts(b, gc);
    }

    public BarDrawer drawBackground(Bounds b, GraphicsContext gc) {
        if (stroke != null)
            fillStrokeRect(b, hPadding, backgroundFill, stroke, radius, gc);
        else
            fillRect(b, hPadding, backgroundFill, radius, gc);
        return this;
    }

    public BarDrawer drawIcon(Bounds b, GraphicsContext gc) {
        if ((iconImage != null || iconSvgPath != null) && iconPos != null && iconHAlignment != null && iconVAlignement != null) {
            double x = 0, y = 0, w = iconSvgPath != null ? iconSvgWidth : iconImage.getWidth(), h = iconSvgPath != null ? iconSvgHeight : iconImage.getHeight();
            switch (iconPos.getHpos()) {
                case LEFT:   x = b.getMinX() + hPadding; break;
                case RIGHT:  x = b.getMaxX() - hPadding; break;
                case CENTER: x = b.getCenterX();         break;
            }
            switch (iconPos.getVpos()) {
                case TOP:    y = b.getMinY();    break;
                case BASELINE:
                case CENTER: y = b.getCenterY(); break;
                case BOTTOM: y = b.getMaxY();    break;
            }
            switch (iconHAlignment) {
                case LEFT:               break;
                case CENTER: x -= w / 2; break;
                case RIGHT:  x -= w;     break;
            }
            switch (iconVAlignement) {
                case TOP:                break;
                case BASELINE:
                case CENTER: y -= h / 2; break;
                case BOTTOM: y -= h;     break;
            }
            gc.save();
            clipRect(b.getMinX() + hPadding, b.getMinY(), b.getWidth() - 2 * hPadding, b.getHeight(), gc);
            if (iconSvgPath != null) {
                Paint fill = iconSvgPath.getFill();
                gc.setFill(fill);
                gc.translate(x + iconTranslateX, y + iconTranslateY);
                gc.beginPath();
                gc.appendSVGPath(iconSvgPath.getContent());
                gc.closePath();
                gc.fill();
            } else
                gc.drawImage(iconImage, x + iconTranslateX, y + iconTranslateY);
            gc.restore();
        }
        return this;
    }

    public BarDrawer drawTexts(Bounds b, GraphicsContext gc) {
        boolean hasMiddleText = middleText != null, hasTopText = topText != null, hasBottomText = bottomText != null, hasTopOrBottomText = hasTopText || hasBottomText, hasText = hasMiddleText || hasTopOrBottomText;
        if (hasText && (!clipText || getTextAreaWidth(b) > 5)) { // Unnecessary to draw text when doesn't fit (this skip makes a big performance improvement on big zoom out over many events - because the text clip operation is time-consuming)
            if (hasMiddleText) {
                setFirstNonNullFont(middleTextFont, textFont, gc);
                fillText(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight(), hPadding, middleText, clipText, textFill, VPos.CENTER, textAlignment, gc);
            }
            if (hasTopOrBottomText) {
                double h = b.getHeight(), h2 = h / 2, vPadding = h / 16;
                if (hasTopText) {
                    setFirstNonNullFont(topTextFont, textFont, gc);
                    fillText(b.getMinX(), b.getMinY() + vPadding, b.getWidth(), h2, hPadding, topText, clipText, textFill, VPos.CENTER, textAlignment, gc);
                }
                if (hasBottomText) {
                    setFirstNonNullFont(bottomTextFont, textFont, gc);
                    fillText(b.getMinX(), b.getMinY() + h2, b.getWidth(), h2 - vPadding, hPadding, bottomText, clipText, textFill, VPos.CENTER, textAlignment, gc);
                }
            }
        }
        return this;
    }

    public double getTextAreaWidth(Bounds b) {
        return b.getWidth() - 2 * hPadding - 5;
    }

    private void setFirstNonNullFont(Font font1, Font font2, GraphicsContext gc) {
        gc.setFont(font1 != null ? font1 : font2);
    }

    // JavaFX bounds compatible API

    private static final FXBoundsWrapper FX_BOUNDS_WRAPPER = new FXBoundsWrapper();

    public BarDrawer drawBar(javafx.geometry.Bounds b, GraphicsContext gc) {
        FX_BOUNDS_WRAPPER.setFxBounds(b);
        return drawBar(FX_BOUNDS_WRAPPER, gc);
    }

    public double getTextAreaWidth(javafx.geometry.Bounds b) {
        FX_BOUNDS_WRAPPER.setFxBounds(b);
        return getTextAreaWidth(FX_BOUNDS_WRAPPER);
    }


    // Static utility methods

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
        if (textAlignment == null) { // auto
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
