package dev.webfx.extras.webtext;

import dev.webfx.extras.webtext.registry.WebTextRegistry;

/**
 * @author Bruno Salmon
 */
public final class HtmlTextEditor extends HtmlText {

    static {
        WebTextRegistry.registerHtmlTextEditor();
    }
}
