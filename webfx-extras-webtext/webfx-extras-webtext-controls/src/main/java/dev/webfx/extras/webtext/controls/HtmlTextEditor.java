package dev.webfx.extras.webtext.controls;

import dev.webfx.extras.webtext.controls.registry.WebTextRegistry;

/**
 * @author Bruno Salmon
 */
public final class HtmlTextEditor extends HtmlText {

    static {
        WebTextRegistry.registerHtmlTextEditor();
    }
}
