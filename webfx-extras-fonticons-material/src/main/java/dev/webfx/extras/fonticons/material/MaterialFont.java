package dev.webfx.extras.fonticons.material;

import dev.webfx.extras.fonticons.IconFont;

/**
 * @author Bruno Salmon
 */
public enum MaterialFont implements IconFont {

    MATERIAL_SYMBOLS_OUTLINED/*,
    MATERIAL_SYMBOLS_ROUNDED, TODO
    MATERIAL_SYMBOLS_SHARP*/;

    @Override
    public String getCssFamily() {
        return "MaterialSymbolsOutlined";
    }

    @Override
    public String getInternalFamily() {
        return "Material Symbols Outlined Regular";
    }


    @Override
    public String getCssClass() {
        return "font-material-outlined";
    }

}
