package dev.webfx.extras.fonticons;

import javafx.scene.text.Font;

/**
 * @author Bruno Salmon
 */
public interface FontIcon {

    char getIconChar();

    default String getIconText() {
        return String.valueOf(getIconChar());
    }

    default String getFontFamily() {
        return getClass().getSimpleName();
    }

    default Font getFont() {
        return Font.font(getFontFamily());
    }

    default String getFontStyleClass() {
        return "font-" + getClass().getSimpleName().toLowerCase();
    }
}
