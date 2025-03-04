package dev.webfx.extras.fonticons;

import javafx.scene.Node;
import javafx.scene.text.Text;

/**
 * @author Bruno Salmon
 */
public final class FontIcons {

    public static <N extends Node> N applyFontStyleClass(N node, FontIcon fontIcon) {
        node.getStyleClass().add(fontIcon.getFontStyleClass());
        return node;
    }

    public static Text newText(FontIcon fontIcon) {
        return applyFontStyleClass(new Text(fontIcon.getIconText()), fontIcon);
    }
}
