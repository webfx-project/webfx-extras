package dev.webfx.extras.fonticons;

import dev.webfx.platform.useragent.UserAgent;
import javafx.scene.Node;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * @author Bruno Salmon
 */
public final class FontIcons {

    public static <N extends Node> N applyFontCssClass(N node, IconFont iconFont) {
        node.getStyleClass().add(iconFont.getCssClass());
        return node;
    }

    public static Text newText(FontIcon fontIcon) {
        return new Text(fontIcon.getText());
    }

    public static Text newText(FontIcon fontIcon, IconFont iconFont) {
        return applyFontCssClass(newText(fontIcon), iconFont);
    }

    public static Font getFont(IconFont iconFont) {
        return getFont(iconFont, -1);
    }

    public static Font getFont(IconFont iconFont, double size) {
        return Font.font(UserAgent.isBrowser() ? iconFont.getCssFamily() : iconFont.getInternalFamily(), size);
    }

}