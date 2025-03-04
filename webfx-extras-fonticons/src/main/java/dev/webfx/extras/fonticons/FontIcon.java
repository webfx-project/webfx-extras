package dev.webfx.extras.fonticons;

import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * @author Bruno Salmon
 */
public interface FontIcon {

    char getChar();

    default String getFontFamily() {
        return getClass().getSimpleName();
    }

    default String getStyleClass() {
        return "font-" + getFontFamily().toLowerCase();
    }

    default String getString() {
        return String.valueOf(getChar());
    }

    default Text newText() {
        Text text = new Text(getString());
        text.getStyleClass().add(getStyleClass());
        // text.setFont(getFont()); // Normally set by CSS
        return text;
    }

    default Font getFont() {
        return Font.font(getFontFamily());
    }
}
