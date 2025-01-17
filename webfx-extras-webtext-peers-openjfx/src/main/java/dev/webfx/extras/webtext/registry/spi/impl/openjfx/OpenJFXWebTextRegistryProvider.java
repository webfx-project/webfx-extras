package dev.webfx.extras.webtext.registry.spi.impl.openjfx;

import dev.webfx.extras.webtext.HtmlTextEditor;
import dev.webfx.extras.webtext.HtmlText;
import dev.webfx.extras.webtext.peers.openjfx.OpenJFXHtmlTextEditorPeer;
import dev.webfx.extras.webtext.peers.openjfx.OpenJFXHtmlTextTextFlowPeer;
import dev.webfx.extras.webtext.registry.spi.WebTextRegistryProvider;
import dev.webfx.kit.mapper.peers.javafxcontrols.openjfx.skin.FxControlPeerSkin;

import static dev.webfx.kit.mapper.peers.javafxgraphics.NodePeerFactoryRegistry.registerNodePeerFactory;

public final class OpenJFXWebTextRegistryProvider implements WebTextRegistryProvider {

    public void registerHtmlText() {
        HtmlText.setDefaultSkinFactory(FxControlPeerSkin::new);
        registerNodePeerFactory(HtmlText.class, OpenJFXHtmlTextTextFlowPeer::new);
    }

    public void registerHtmlTextEditor() {
        registerNodePeerFactory(HtmlTextEditor.class, OpenJFXHtmlTextEditorPeer::new);
    }

    public void registerSvgText() {
    }

}
