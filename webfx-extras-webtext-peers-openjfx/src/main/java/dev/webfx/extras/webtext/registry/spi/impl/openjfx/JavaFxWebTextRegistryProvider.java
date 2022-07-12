package dev.webfx.extras.webtext.registry.spi.impl.openjfx;

import dev.webfx.extras.webtext.HtmlTextEditor;
import dev.webfx.extras.webtext.HtmlText;
import dev.webfx.extras.webtext.peers.openjfx.FxHtmlTextEditorPeer;
import dev.webfx.extras.webtext.peers.openjfx.FxHtmlTextTextFlowPeer;
import dev.webfx.extras.webtext.registry.spi.WebTextRegistryProvider;
import dev.webfx.kit.mapper.peers.javafxcontrols.openjfx.skin.FxControlPeerSkin;

import static dev.webfx.kit.mapper.peers.javafxgraphics.NodePeerFactoryRegistry.registerNodePeerFactory;

public final class JavaFxWebTextRegistryProvider implements WebTextRegistryProvider {

    public void registerHtmlText() {
        HtmlText.setDefaultSkinFactory(FxControlPeerSkin::new);
        registerNodePeerFactory(HtmlText.class, FxHtmlTextTextFlowPeer::new);
    }

    public void registerHtmlTextEditor() {
        registerNodePeerFactory(HtmlTextEditor.class, FxHtmlTextEditorPeer::new);
    }

    public void registerSvgText() {
    }

}
