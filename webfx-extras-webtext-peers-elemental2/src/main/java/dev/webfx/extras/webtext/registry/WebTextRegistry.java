package dev.webfx.extras.webtext.registry;

import dev.webfx.extras.webtext.HtmlText;
import dev.webfx.extras.webtext.HtmlTextEditor;
import dev.webfx.extras.webtext.SvgText;
import dev.webfx.extras.webtext.peers.elemental2.html.Elemental2HtmlTextEditorPeer;
import dev.webfx.extras.webtext.peers.elemental2.html.Elemental2HtmlTextPeer;
import dev.webfx.kit.mapper.peers.javafxgraphics.elemental2.html.HtmlSvgTextPeer;

import static dev.webfx.kit.mapper.peers.javafxgraphics.NodePeerFactoryRegistry.registerNodePeerFactory;

public final class WebTextRegistry {

    public static void registerHtmlText() {
        registerNodePeerFactory(HtmlText.class, Elemental2HtmlTextPeer::new);
    }

    public static void registerHtmlTextEditor() {
        registerNodePeerFactory(HtmlTextEditor.class, Elemental2HtmlTextEditorPeer::new);
    }

    public static void registerSvgText() {
        registerNodePeerFactory(SvgText.class, HtmlSvgTextPeer::new);
    }

}
