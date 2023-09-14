package dev.webfx.extras.theme.luminance;

import dev.webfx.extras.theme.Facet;
import dev.webfx.extras.theme.Theme;
import dev.webfx.extras.theme.ThemeRegistry;
import dev.webfx.extras.theme.ThemeUtil;
import dev.webfx.extras.theme.layout.FXLayoutMode;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

/**
 * @author Bruno Salmon
 */
public class LuminanceTheme implements Theme {

    private final static double ROUND_RADIUS = 10;

    private final static Color LIGHT_APPLICATION_FRAME_BACKGROUND_COLOR = Color.LIGHTGRAY;
    private final static Color DARK_APPLICATION_FRAME_BACKGROUND_COLOR = Color.rgb(44, 44, 58);

    private final static Color LIGHT_PRIMARY_PANEL_BACKGROUND_COLOR = Color.ALICEBLUE;
    private final static Color DARK_PRIMARY_PANEL_BACKGROUND_COLOR = Color.rgb(29, 29, 38);

    private final static Color LIGHT_SECONDARY_PANEL_BACKGROUND_COLOR = Color.WHITE;
    private final static Color DARK_SECONDARY_PANEL_BACKGROUND_COLOR = DARK_APPLICATION_FRAME_BACKGROUND_COLOR;

    private final static Color LIGHT_SECONDARY_PANEL_BACKGROUND_COLOR_INVERTED = Color.rgb(0, 150, 214);
    private final static Color DARK_SECONDARY_PANEL_BACKGROUND_COLOR_INVERTED = LIGHT_SECONDARY_PANEL_BACKGROUND_COLOR_INVERTED.brighter();

    private final static Color LIGHT_BORDER_COLOR = Color.GRAY;
    private final static Color DARK_BORDER_COLOR = Color.DARKGRAY;

    private final static Effect LIGHT_SHADOW_DIAGONAL = new DropShadow(10, 5, 5, Color.LIGHTGRAY);
    private final static Effect DARK_SHADOW_DIAGONAL = null; //new DropShadow(10, 5, 5, Color.WHITE);
    private final static Effect LIGHT_SHADOW_DOWN = new DropShadow(10, 0, 5, Color.GRAY);
    private final static Effect DARK_SHADOW_DOWN = new DropShadow(10, 0, 5, Color.CYAN);
    private final static Effect LIGHT_SHADOW_UP = new DropShadow(10, 0, -5, Color.GRAY);
    private final static Effect DARK_SHADOW_UP = new DropShadow(10, 0, -5, Color.CYAN);

    static {
        ThemeRegistry.registerTheme(new LuminanceTheme());
    }

    public static Facet createApplicationFrameFacet(Region frame) {
        return new Facet(LuminanceFacetCategory.APPLICATION_FRAME_FACET, frame);
    }

    public static Facet createPrimaryPanelFacet(Region panel) {
        return new Facet(LuminanceFacetCategory.PRIMARY_PANEL_FACET, panel);
    }

    public static Facet createSecondaryPanelFacet(Region panel) {
        return new Facet(LuminanceFacetCategory.SECONDARY_PANEL_FACET, panel);
    }

    public static Facet createTopPanelFacet(Region panel) {
        return new Facet(LuminanceFacetCategory.TOP_PANEL_FACET, panel);
    }

    public static Facet createBottomPanelFacet(Region panel) {
        return new Facet(LuminanceFacetCategory.BOTTOM_PANEL_FACET, panel);
    }

    @Override
    public boolean supportsFacetCategory(Object facetCategory) {
        return facetCategory instanceof LuminanceFacetCategory;
    }

    @Override
    public void styleFacet(Facet facet, Object facetCategory) {
        LuminanceFacetCategory luminanceFacetCategory = (LuminanceFacetCategory) facetCategory;

        Color backgroundColor = null, borderColor = facet.isBordered() ? getBorderColor() : null;
        Effect effect = facet.isShadowed() ? getShadow(luminanceFacetCategory) : null;

        switch (luminanceFacetCategory) {

            case APPLICATION_FRAME_FACET:
                backgroundColor = getApplicationFrameBackgroundColor();
                break;

                case PRIMARY_PANEL_FACET:
                backgroundColor = getPrimaryBackgroundColor();
                break;

            case TOP_PANEL_FACET:
            case BOTTOM_PANEL_FACET:
            case SECONDARY_PANEL_FACET:
                backgroundColor = getSecondaryBackgroundColor(facet.isInverted());
                break;

        }

        Region background = facet.getBackgroundNode();
        if (background != null) {
            double radius = facet.isRounded() ? ROUND_RADIUS : 0;
            ThemeUtil.applyBackground(background, backgroundColor, radius);
            ThemeUtil.applyBorder(background, borderColor, radius);
        }
        Node containerNode = facet.getContainerNode();
        if (containerNode != null) {
            ThemeUtil.applyEffect(containerNode, effect);
        }
    }

    private static Effect getShadow(LuminanceFacetCategory luminanceFacetCategory) {
        switch (luminanceFacetCategory) {
            case TOP_PANEL_FACET: return FXLuminanceMode.isLightMode() ? LIGHT_SHADOW_DOWN : DARK_SHADOW_DOWN;
            case BOTTOM_PANEL_FACET: return FXLuminanceMode.isLightMode() ? LIGHT_SHADOW_UP : DARK_SHADOW_UP;
            default: return FXLuminanceMode.isLightMode() ? LIGHT_SHADOW_DIAGONAL : DARK_SHADOW_DIAGONAL;
        }
    }

    private static Color getApplicationFrameBackgroundColor() {
        return FXLayoutMode.isCompactMode() ? null : // No background in compact mode (transparent floating toolbar)
                FXLuminanceMode.isLightMode() ?
                LIGHT_APPLICATION_FRAME_BACKGROUND_COLOR
                : DARK_APPLICATION_FRAME_BACKGROUND_COLOR;
    }

    private static Color getPrimaryBackgroundColor() {
        return FXLuminanceMode.isLightMode() ?
                LIGHT_PRIMARY_PANEL_BACKGROUND_COLOR
                : DARK_PRIMARY_PANEL_BACKGROUND_COLOR;
    }

    public static Color getSecondaryBackgroundColor(boolean inverted) {
        return FXLuminanceMode.isLightMode() ?
                (inverted ? LIGHT_SECONDARY_PANEL_BACKGROUND_COLOR_INVERTED : LIGHT_SECONDARY_PANEL_BACKGROUND_COLOR)
                : (inverted ? DARK_SECONDARY_PANEL_BACKGROUND_COLOR_INVERTED : DARK_SECONDARY_PANEL_BACKGROUND_COLOR);
    }

    private static Color getBorderColor() {
        return FXLuminanceMode.isLightMode() ?
                LIGHT_BORDER_COLOR
                : DARK_BORDER_COLOR;
    }

}
