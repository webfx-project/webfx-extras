package dev.webfx.extras.theme.layout;

import dev.webfx.extras.theme.ThemeRegistry;
import dev.webfx.kit.util.properties.FXProperties;
import javafx.beans.property.ObjectProperty;

/**
 * @author Bruno Salmon
 */
public final class FXLayoutMode {

    private final static ObjectProperty<LayoutMode> layoutModeProperty = FXProperties.newObjectProperty(LayoutMode.STANDARD_LAYOUT, ThemeRegistry::fireModeChanged);

    public static ObjectProperty<LayoutMode> layoutModeProperty() {
        return layoutModeProperty;
    }

    public static LayoutMode getLayoutMode() {
        return layoutModeProperty.get();
    }

    public static void setLayoutMode(LayoutMode layoutMode) {
        layoutModeProperty.set(layoutMode);
    }

    public static boolean isCompactMode() {
        return getLayoutMode() == LayoutMode.COMPACT_LAYOUT;
    }

    public static void setCompactMode(boolean compactMode) {
        setLayoutMode(compactMode ? LayoutMode.COMPACT_LAYOUT : LayoutMode.STANDARD_LAYOUT);
    }

}
