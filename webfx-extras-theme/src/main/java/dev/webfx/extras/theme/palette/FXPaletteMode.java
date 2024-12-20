package dev.webfx.extras.theme.palette;

import dev.webfx.extras.theme.ThemeRegistry;
import dev.webfx.kit.util.properties.FXProperties;
import javafx.beans.property.ObjectProperty;

/**
 * @author Bruno Salmon
 */
public class FXPaletteMode {

    private final static ObjectProperty<PaletteMode> paletteModeProperty = FXProperties.newObjectProperty(PaletteMode.ESSENTIAL_PALETTE, ThemeRegistry::fireModeChanged);

    public ObjectProperty<PaletteMode> paletteModeProperty() {
        return paletteModeProperty;
    }

    public static PaletteMode getPaletteMode() {
        return paletteModeProperty.get();
    }

    public static void setPaletteMode(PaletteMode paletteMode) {
        paletteModeProperty.set(paletteMode);
    }

    public static boolean isEssentialPalette() {
        return getPaletteMode() == PaletteMode.ESSENTIAL_PALETTE;
    }

    public static boolean isVariedPalette() {
        return getPaletteMode() == PaletteMode.VARIED_PALETTE;
    }

    public static void setVariedPalette(boolean enable) {
        setPaletteMode(enable ? PaletteMode.VARIED_PALETTE : PaletteMode.ESSENTIAL_PALETTE);
    }
}
