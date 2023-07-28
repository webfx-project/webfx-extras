package dev.webfx.extras.theme;

import javafx.scene.effect.Effect;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

/**
  * @author Bruno Salmon
 */
public class StyleCapture implements AutoCloseable {

    private Paint backgroundFill;
    private double backgroundRadius;
    private Background background;
    private Paint borderFill;
    private double borderRadius;
    private Border border;
    private Effect effect;
    private Font font;
    private Paint textFill;

    private static StyleCapture STYLE_CAPTURE;

    private StyleCapture() {
        if (STYLE_CAPTURE != null)
            throw new IllegalStateException();
        STYLE_CAPTURE = this;
    }

    public static StyleCapture getStyleCapture() {
        return STYLE_CAPTURE;
    }

    @Override
    public void close() {
        STYLE_CAPTURE = null;
    }

    // Cache variables to quickly return the same value on same input
    private static Theme LAST_THEME;
    private static Object LAST_LOGICAL_VALUE;
    private static boolean LAST_SELECTED;
    private static Object LAST_FACET_CATEGORY;
    private static StyleCapture LAST_STYLE_CAPTURE;

    static {
        // Clearing the cache on mode change
        ThemeRegistry.addModeChangeListener(() -> LAST_THEME = null); // clearing LAST_THEME is enough
    }

    public static StyleCapture captureStyle(Theme theme, Object logicalValue, Object facetCategory) {
        return captureStyle(theme, logicalValue, false, facetCategory);
    }

    public static StyleCapture captureStyle(Theme theme, Object logicalValue, boolean selected, Object facetCategory) {
        // Optimization: returning same last capture if same last inputs
        if (theme == LAST_THEME && logicalValue == LAST_LOGICAL_VALUE && selected == LAST_SELECTED && facetCategory == LAST_FACET_CATEGORY)
            return LAST_STYLE_CAPTURE;
        try (StyleCapture styleCapture = new StyleCapture()) {
            Facet.GENERIC_FACET.setLogicValue(logicalValue).setSelected(selected);
            theme.styleFacet(Facet.GENERIC_FACET, facetCategory);
            LAST_THEME = theme;
            LAST_LOGICAL_VALUE = logicalValue;
            LAST_SELECTED = selected;
            LAST_FACET_CATEGORY = facetCategory;
            return LAST_STYLE_CAPTURE = styleCapture;
        }
    }

    public Paint getBackgroundFill() {
        return backgroundFill;
    }

    public void setBackgroundFill(Paint backgroundFill) {
        this.backgroundFill = backgroundFill;
    }

    public double getBackgroundRadius() {
        return backgroundRadius;
    }

    public void setBackgroundRadius(double backgroundRadius) {
        this.backgroundRadius = backgroundRadius;
    }

    public Background getBackground() {
        return background;
    }

    public void setBackground(Background background) {
        this.background = background;
    }

    public Paint getBorderFill() {
        return borderFill;
    }

    public void setBorderFill(Paint borderFill) {
        this.borderFill = borderFill;
    }

    public double getBorderRadius() {
        return borderRadius;
    }

    public void setBorderRadius(double borderRadius) {
        this.borderRadius = borderRadius;
    }

    public Border getBorder() {
        return border;
    }

    public void setBorder(Border border) {
        this.border = border;
    }

    public Effect getEffect() {
        return effect;
    }

    public void setEffect(Effect effect) {
        this.effect = effect;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Paint getTextFill() {
        return textFill;
    }

    public void setTextFill(Paint textFill) {
        this.textFill = textFill;
    }

}
