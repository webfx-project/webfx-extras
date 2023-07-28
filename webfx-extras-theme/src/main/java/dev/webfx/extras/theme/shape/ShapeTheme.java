package dev.webfx.extras.theme.shape;

import dev.webfx.extras.theme.Facet;
import dev.webfx.extras.theme.Theme;
import dev.webfx.extras.theme.ThemeRegistry;
import dev.webfx.extras.theme.luminance.FXLuminanceMode;
import dev.webfx.extras.util.color.Colors;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;

/**
 * @author Bruno Salmon
 */
public class ShapeTheme implements Theme {

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

    static {
        ThemeRegistry.registerTheme(new ShapeTheme());
    }

    public static Facet createDefaultShapeFacet(Node shapeNode) {
        return createShapeFacet(shapeNode, ShapeFacetCategory.DEFAULT_SHAPE_FACET);
    }

    private static Facet createShapeFacet(Node shapeNode, ShapeFacetCategory shapeFacetCategory) {
        return new Facet(shapeFacetCategory, shapeNode).setGraphicNode(shapeNode);
    }

    public static Facet createPrimaryShapeFacet(Node shapeNode) {
        return createShapeFacet(shapeNode, ShapeFacetCategory.PRIMARY_SHAPE_FACET);
    }

    public static Facet createSecondaryShapeFacet(Node shapeNode) {
        return createShapeFacet(shapeNode, ShapeFacetCategory.SECONDARY_SHAPE_FACET);
    }

    @Override
    public boolean supportsFacetCategory(Object facetCategory) {
        return facetCategory instanceof ShapeFacetCategory;
    }

    @Override
    public void styleFacet(Facet facet, Object facetCategory) {
        styleFacet(facet, (ShapeFacetCategory) facetCategory);
    }

    public static void styleFacet(Facet facet, ShapeFacetCategory shapeFacetCategory) {
        Node graphicNode = facet.getGraphicNode();
        boolean primary = shapeFacetCategory == ShapeFacetCategory.PRIMARY_SHAPE_FACET;
        boolean secondary = shapeFacetCategory == ShapeFacetCategory.SECONDARY_SHAPE_FACET;

        Color textColor = getShapeColor(primary, secondary, facet.isInverted());

        Color invertedColor = getShapeColor(primary, secondary, !facet.isInverted());
        Color oppositeLightColor = getShapeColor(primary, secondary, facet.isInverted(), !FXLuminanceMode.isLightMode());
        setGraphicFill(graphicNode, textColor, invertedColor, oppositeLightColor);
    }

    private static void setGraphicFill(Node graphicNode, Color graphicColor, Color invertedColor, Color oppositeLightColor) {
        if (graphicNode instanceof Shape) {
            Shape graphic = (Shape) graphicNode;
            if (!(graphic instanceof SVGPath))
                graphic.setFill(graphicColor);
            else {
                SVGPath svgPath = (SVGPath) graphic;
                Paint graphicFill = svgPath.getFill();
                Paint graphicStroke = svgPath.getStroke();
                if (Color.BLACK.equals(graphicFill) || invertedColor.equals(graphicFill) || oppositeLightColor.equals(graphicFill) || graphicFill == null && graphicStroke == null)
                    graphic.setFill(graphicColor);
                if (Color.BLACK.equals(graphicStroke) || invertedColor.equals(graphicStroke) || oppositeLightColor.equals(graphicStroke))
                    graphic.setStroke(graphicColor);
            }
        } else if (graphicNode instanceof Parent) {
            ((Parent) graphicNode).getChildrenUnmodifiable().forEach(n -> setGraphicFill(n, graphicColor, invertedColor, oppositeLightColor));
        }
    }

    private static Color getShapeColor(boolean primary, boolean secondary, boolean inverted) {
        return getShapeColor(primary, secondary, inverted, FXLuminanceMode.isLightMode());
    }

    private static Color getShapeColor(boolean primary, boolean secondary, boolean inverted, boolean lightMode) {
        return lightMode ?
                // Light mode
                (primary ? (inverted ? LIGHT_PRIMARY_COLOR_INVERTED : LIGHT_PRIMARY_COLOR) : secondary ? (inverted ? LIGHT_SECONDARY_COLOR_INVERTED : LIGHT_SECONDARY_COLOR) : (inverted ? LIGHT_DEFAULT_COLOR_INVERTED : LIGHT_DEFAULT_COLOR))
                // Dark mode
                : primary ? (inverted ? DARK_PRIMARY_COLOR_INVERTED : DARK_PRIMARY_COLOR) : secondary ? (inverted ? DARK_SECONDARY_COLOR_INVERTED : DARK_SECONDARY_COLOR) : (inverted ? DARK_DEFAULT_COLOR_INVERTED : DARK_DEFAULT_COLOR);
    }

}
