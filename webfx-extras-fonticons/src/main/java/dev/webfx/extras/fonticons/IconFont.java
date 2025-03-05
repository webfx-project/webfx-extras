package dev.webfx.extras.fonticons;

/**
 * @author Bruno Salmon
 */
public interface IconFont {

    /**
     * @return the font family name declared in CSS rules
     */
    String getCssFamily();

    /**
     * @return the font family name internally declared in the font file. This is the one to be used in OpenJFX when
     * programmatically applying setFont()
     */
    String getInternalFamily();

    /**
     * @return the CSS class whose CSS selector applies the font family
     */
    default String getCssClass() {
        return "font-" + getCssFamily().toLowerCase();
    }

}
