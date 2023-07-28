package dev.webfx.extras.theme;

import dev.webfx.extras.util.background.BackgroundFactory;
import dev.webfx.extras.util.border.BorderFactory;
import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.effect.Effect;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * @author Bruno Salmon
 */
public final class ThemeUtil {

    public static void applyBackground(Region region, Paint fill, double radius) {
        StyleCapture styleCapture = StyleCapture.getStyleCapture();
        if (styleCapture != null) {
            styleCapture.setBackgroundFill(fill);
            styleCapture.setBackgroundRadius(radius);
        }
        applyBackground(region, fill == null ? null : BackgroundFactory.newBackground(fill, radius));
    }

    public static void applyBackground(Region region, Background background) {
        region.setBackground(background);
        StyleCapture styleCapture = StyleCapture.getStyleCapture();
        if (styleCapture != null)
            styleCapture.setBackground(background);
    }

    public static void applyBorder(Region region, Paint fill, double radius) {
        StyleCapture styleCapture = StyleCapture.getStyleCapture();
        if (styleCapture != null) {
            styleCapture.setBorderFill(fill);
            styleCapture.setBorderRadius(radius);
        }
        applyBorder(region, fill == null ? null : BorderFactory.newBorder(fill, radius));
    }

    public static void applyBorder(Region region, Border border) {
        region.setBorder(border);
        StyleCapture styleCapture = StyleCapture.getStyleCapture();
        if (styleCapture != null)
            styleCapture.setBorder(border);
    }

    public static void applyEffect(Node node, Effect effect) {
        node.setEffect(effect);
        StyleCapture styleCapture = StyleCapture.getStyleCapture();
        if (styleCapture != null)
            styleCapture.setEffect(effect);
    }

    public static void applyFont(Property<Font> fontProperty, String fontFamily, FontDef requestedFont) {
        Font font = requestedFont == null ? null : Font.font(fontFamily, requestedFont.getWeight(), requestedFont.getPosture(), requestedFont.getSize());
        applyFont(fontProperty, font);
    }

    public static void applyFont(Property<Font> fontProperty, Font font) {
        fontProperty.setValue(font);
        StyleCapture styleCapture = StyleCapture.getStyleCapture();
        if (styleCapture != null)
            styleCapture.setFont(font);
    }

    public static void applyTextFill(Text text, Paint textFill) {
        applyTextFill(text.fillProperty(), textFill);
    }

    public static void applyTextFill(Property<Paint> textFillProperty, Paint textFill) {
        textFillProperty.setValue(textFill);
        StyleCapture styleCapture = StyleCapture.getStyleCapture();
        if (styleCapture != null)
            styleCapture.setTextFill(textFill);
    }

}
