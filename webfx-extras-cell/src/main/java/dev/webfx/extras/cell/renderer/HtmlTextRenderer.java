package dev.webfx.extras.cell.renderer;

import dev.webfx.extras.webtext.controls.HtmlText;
import dev.webfx.platform.util.Strings;

/**
 * @author Bruno Salmon
 */
public final class HtmlTextRenderer implements ValueRenderer {

    public final static HtmlTextRenderer SINGLETON = new HtmlTextRenderer();

    private HtmlTextRenderer() {}

    @Override
    public HtmlText renderValue(Object value, ValueRenderingContext context) {
        return new HtmlText(Strings.toString(value));
    }
}
