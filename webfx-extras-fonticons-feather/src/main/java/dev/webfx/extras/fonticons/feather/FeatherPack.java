package dev.webfx.extras.fonticons.feather;

import dev.webfx.extras.fonticons.FontIcon;
import dev.webfx.extras.fonticons.IconFont;
import dev.webfx.extras.fonticons.IconPack;

/**
 * @author Bruno Salmon
 */
public final class FeatherPack implements IconPack {

    private static final FeatherPack INSTANCE = new FeatherPack();

    public static FeatherPack getInstance() {
        return INSTANCE;
    }

    @Override
    public FontIcon[] getIcons() {
        return FeatherIcon.values();
    }

    @Override
    public IconFont[] getFonts() {
        return FeatherFont.values();
    }

}
