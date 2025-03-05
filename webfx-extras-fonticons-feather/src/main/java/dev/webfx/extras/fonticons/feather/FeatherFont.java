package dev.webfx.extras.fonticons.feather;

import dev.webfx.extras.fonticons.IconFont;

/**
 * @author Bruno Salmon
 */
public enum FeatherFont implements IconFont {

    FEATHER;

    @Override
    public String getCssFamily() {
        return "Feather";
    }

    @Override
    public String getInternalFamily() {
        return "FeatherIcons";
    }


    @Override
    public String getCssClass() {
        return "font-feather";
    }
}
