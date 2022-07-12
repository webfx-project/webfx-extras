package dev.webfx.extras.webtext;

import dev.webfx.extras.webtext.registry.WebTextRegistry;
import javafx.scene.text.Text;

public class SvgText extends Text {

    public SvgText() {
    }

    public SvgText(String text) {
        super(text);
    }

    public SvgText(double x, double y, String text) {
        super(x, y, text);
    }

    static {
        WebTextRegistry.registerSvgText();
    }
}
