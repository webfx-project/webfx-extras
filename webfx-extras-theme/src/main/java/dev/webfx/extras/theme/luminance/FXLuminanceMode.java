package dev.webfx.extras.theme.luminance;

import dev.webfx.extras.theme.ThemeRegistry;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @author Bruno Salmon
 */
public final class FXLuminanceMode {

    private final static ObjectProperty<LuminanceMode> luminanceModeProperty = new SimpleObjectProperty<>(LuminanceMode.LIGHT_MODE) {
        @Override
        protected void invalidated() {
            ThemeRegistry.fireModeChanged();
        }
    };

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
