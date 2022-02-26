package dev.webfx.extras.webtext.controls.registry;

import dev.webfx.extras.webtext.controls.HtmlText;
import dev.webfx.extras.webtext.controls.HtmlTextEditor;
import dev.webfx.extras.webtext.controls.peers.gwt.html.HtmlHtmlTextEditorPeer;
import dev.webfx.extras.webtext.controls.peers.gwt.html.HtmlHtmlTextPeer;
import dev.webfx.extras.webtext.controls.SvgText;
import dev.webfx.kit.mapper.peers.javafxgraphics.gwt.html.HtmlSvgTextPeer;

import static dev.webfx.kit.mapper.peers.javafxgraphics.NodePeerFactoryRegistry.registerNodePeerFactory;

public final class WebTextRegistry {

    public static void registerHtmlText() {
        registerNodePeerFactory(HtmlText.class, HtmlHtmlTextPeer::new);
    }

    public static void registerHtmlTextEditor() {
        registerNodePeerFactory(HtmlTextEditor.class, HtmlHtmlTextEditorPeer::new);
    }

    public static void registerSvgText() {
        registerNodePeerFactory(SvgText.class, HtmlSvgTextPeer::new);
    }

}
