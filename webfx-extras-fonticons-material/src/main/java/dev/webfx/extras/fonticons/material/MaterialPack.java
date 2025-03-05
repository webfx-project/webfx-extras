package dev.webfx.extras.fonticons.material;

import dev.webfx.extras.fonticons.FontIcon;
import dev.webfx.extras.fonticons.IconFont;
import dev.webfx.extras.fonticons.IconPack;
import dev.webfx.platform.util.Arrays;

/**
 * @author Bruno Salmon
 */
public final class MaterialPack implements IconPack {

    private static final MaterialPack INSTANCE = new MaterialPack();

    public static MaterialPack getInstance() {
        return INSTANCE;
    }

    @Override
    public FontIcon[] getIcons() {
        return Arrays.concat(FontIcon[]::new, MaterialIcon1.values(), MaterialIcon2.values());
    }

    @Override
    public IconFont[] getFonts() {
        return MaterialFont.values();
    }
}
