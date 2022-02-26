package dev.webfx.extras.webtext.controls.registry.spi.impl.openjfx;

import dev.webfx.extras.webtext.controls.HtmlText;
import dev.webfx.extras.webtext.controls.HtmlTextEditor;
import dev.webfx.extras.webtext.controls.peers.openjfx.FxHtmlTextEditorPeer;
import dev.webfx.extras.webtext.controls.peers.openjfx.FxHtmlTextTextFlowPeer;
import dev.webfx.extras.webtext.controls.registry.spi.WebTextRegistryProvider;
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
