package dev.webfx.extras.webtext.registry;

import dev.webfx.extras.webtext.HtmlText;
import dev.webfx.extras.webtext.HtmlTextEditor;
import dev.webfx.extras.webtext.SvgText;
import dev.webfx.extras.webtext.peers.gwt.html.HtmlHtmlTextEditorPeer;
import dev.webfx.extras.webtext.peers.gwt.html.HtmlHtmlTextPeer;
import dev.webfx.kit.mapper.peers.javafxgraphics.gwtj2cl.html.HtmlSvgTextPeer;

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
