package dev.webfx.extras.theme.luminance;

import dev.webfx.extras.theme.ThemeRegistry;
import dev.webfx.kit.util.properties.FXProperties;
import javafx.beans.property.ObjectProperty;

/**
 * @author Bruno Salmon
 */
public final class FXLuminanceMode {

    private final static ObjectProperty<LuminanceMode> luminanceModeProperty = FXProperties.newObjectProperty(LuminanceMode.LIGHT_MODE, ThemeRegistry::fireModeChanged);

    public static ObjectProperty<LuminanceMode> luminanceModeProperty() {
        return luminanceModeProperty;
    }


    public static LuminanceMode getLuminanceMode() {
        return luminanceModeProperty.get();
    }

    public static void setLuminanceMode(LuminanceMode luminanceMode) {
        luminanceModeProperty.set(luminanceMode);
    }

    public static boolean isLightMode() {
        return getLuminanceMode() == LuminanceMode.LIGHT_MODE;
    }

    public static boolean isDarkMode() {
        return getLuminanceMode() == LuminanceMode.DARK_MODE;
    }

    public static void setDarkMode(boolean darkMode) {
        setLuminanceMode(darkMode ? LuminanceMode.DARK_MODE : LuminanceMode.LIGHT_MODE);
    }
}
