package dev.webfx.extras.theme.text;

import dev.webfx.extras.theme.*;
import dev.webfx.extras.theme.luminance.FXLuminanceMode;
import dev.webfx.extras.theme.shape.ShapeFacetCategory;
import dev.webfx.extras.theme.shape.ShapeTheme;
import dev.webfx.extras.util.color.Colors;
import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * @author Bruno Salmon
 */
public class TextTheme implements Theme {

    private final static String FONT_FAMILY = "Montserrat";

    private final static Color LIGHT_DEFAULT_COLOR = Color.rgb(17, 95, 24);
    private final static Color DARK_DEFAULT_COLOR = Color.WHITE;

    private final static Color LIGHT_DEFAULT_COLOR_INVERTED = Color.WHITE;
    private final static Color DARK_DEFAULT_COLOR_INVERTED = Color.WHITE;

    private final static Color LIGHT_PRIMARY_COLOR = Color.rgb(0, 150, 214);
    private final static Color DARK_PRIMARY_COLOR = Colors.whitenColor(LIGHT_PRIMARY_COLOR, 0.5);

    private final static Color LIGHT_PRIMARY_COLOR_INVERTED = Color.WHITE;
    private final static Color DARK_PRIMARY_COLOR_INVERTED = Color.WHITE;

    private final static Color LIGHT_SECONDARY_COLOR = Color.rgb( 144, 147, 148 );
    private final static Color DARK_SECONDARY_COLOR = Color.GRAY;

    private final static Color LIGHT_SECONDARY_COLOR_INVERTED = Color.WHITE;
    private final static Color DARK_SECONDARY_COLOR_INVERTED = Color.WHITE;

    private final static Color LIGHT_COLOR_DISABLED = Color.gray(0.8);
    private final static Color DARK_COLOR_DISABLED = Color.GRAY;

    static {
        ThemeRegistry.registerTheme(new TextTheme());
    }

    public static Facet createDefaultTextFacet(Node textNode) {
        return createTextFacet(textNode, TextFacetCategory.DEFAULT_TEXT_FACET);
    }

    private static Facet createTextFacet(Node textNode, TextFacetCategory textFacetCategory) {
        Facet textFacet = new Facet(textFacetCategory, textNode).setTextNode(textNode);
        if (textNode instanceof Labeled)
            textFacet.setGraphicNode(((Labeled) textNode).getGraphic());
        return textFacet;
    }

    public static Facet createPrimaryTextFacet(Node textNode) {
        return createTextFacet(textNode, TextFacetCategory.PRIMARY_TEXT_FACET);
    }

    public static Facet createSecondaryTextFacet(Node textNode) {
        return createTextFacet(textNode, TextFacetCategory.SECONDARY_TEXT_FACET);
    }

    @Override
    public boolean supportsFacetCategory(Object facetCategory) {
        return facetCategory instanceof TextFacetCategory;
    }

    @Override
    public void styleFacet(Facet facet, Object facetCategory) {
        TextFacetCategory textFacetCategory = (TextFacetCategory) facetCategory;

        Node textNode = facet.getTextNode();
        boolean primary = textFacetCategory == TextFacetCategory.PRIMARY_TEXT_FACET;
        boolean secondary = textFacetCategory == TextFacetCategory.SECONDARY_TEXT_FACET;
        
        Color textColor = getTextColor(primary, secondary, facet.isInverted(), facet.isDisabled());

        applyFacetFont(facet, facet.getRequestedFont());

        Property<Paint> fillProperty = facet.getFillProperty();
        if (fillProperty == null && textNode instanceof Shape)
            fillProperty = ((Shape) textNode).fillProperty();
        if (fillProperty == null && textNode instanceof Labeled)
            fillProperty = ((Labeled) textNode).textFillProperty();
        if (fillProperty != null) {
            ThemeUtil.applyTextFill(fillProperty, textColor);
        }

        Node graphicNode = facet.getGraphicNode();
        if (graphicNode != null) {
            ShapeTheme.styleFacet(facet, primary ? ShapeFacetCategory.PRIMARY_SHAPE_FACET : secondary ? ShapeFacetCategory.SECONDARY_SHAPE_FACET : ShapeFacetCategory.DEFAULT_SHAPE_FACET);
        }
    }

    public static void applyFacetFont(Facet facet, FontDef requestedFont) {
        FontDef lastRequestedFont = facet.getFacetValue("lastRequestedFont");
        if (requestedFont != lastRequestedFont) {
            facet.setFacetValue("lastRequestedFont", requestedFont);
            Property<Font> fontProperty = facet.getFontProperty();
            Node textNode = facet.getTextNode();
            if (fontProperty == null && textNode instanceof Text)
                fontProperty = ((Text) textNode).fontProperty();
            if (fontProperty == null && textNode instanceof Labeled)
                fontProperty = ((Labeled) textNode).fontProperty();
            if (fontProperty != null) {
                ThemeUtil.applyFont(fontProperty, FONT_FAMILY, requestedFont);
            }
        }
    }

    public static Font getFont(FontDef requestedFont) {
        applyFacetFont(Facet.GENERIC_FACET, requestedFont);
        return Facet.GENERIC_TEXT_NODE.getFont();
    }

    public static Color getTextColor(TextFacetCategory textFacetCategory) {
        return getTextColor(textFacetCategory, false);
    }

    public static Color getTextColor(TextFacetCategory textFacetCategory, boolean inverted) {
        boolean primary = textFacetCategory == TextFacetCategory.PRIMARY_TEXT_FACET;
        boolean secondary = textFacetCategory == TextFacetCategory.SECONDARY_TEXT_FACET;
        return getTextColor(primary, secondary, inverted, FXLuminanceMode.isLightMode(), false);
    }

    public static Color getTextColor(boolean primary, boolean secondary, boolean inverted, boolean disabled) {
        return getTextColor(primary, secondary, inverted, FXLuminanceMode.isLightMode(), disabled);
    }

    private static Color getTextColor(boolean primary, boolean secondary, boolean inverted, boolean lightMode, boolean disabled) {
        if (disabled)
            return lightMode ? LIGHT_COLOR_DISABLED : DARK_COLOR_DISABLED;
        return lightMode ?
                // Light mode
                (primary ? (inverted ? LIGHT_PRIMARY_COLOR_INVERTED : LIGHT_PRIMARY_COLOR) : secondary ? (inverted ? LIGHT_SECONDARY_COLOR_INVERTED : LIGHT_SECONDARY_COLOR) : (inverted ? LIGHT_DEFAULT_COLOR_INVERTED : LIGHT_DEFAULT_COLOR))
                // Dark mode
                : primary ? (inverted ? DARK_PRIMARY_COLOR_INVERTED : DARK_PRIMARY_COLOR) : secondary ? (inverted ? DARK_SECONDARY_COLOR_INVERTED : DARK_SECONDARY_COLOR) : (inverted ? DARK_DEFAULT_COLOR_INVERTED : DARK_DEFAULT_COLOR);
    }

}
