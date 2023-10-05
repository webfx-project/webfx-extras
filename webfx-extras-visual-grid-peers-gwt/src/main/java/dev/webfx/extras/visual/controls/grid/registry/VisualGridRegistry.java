package dev.webfx.extras.visual.controls.grid.registry;

import dev.webfx.extras.visual.controls.grid.VisualGrid;
import dev.webfx.extras.visual.controls.grid.peers.gwt.html.HtmlVisualGridPeer;

import static dev.webfx.kit.mapper.peers.javafxgraphics.NodePeerFactoryRegistry.registerNodePeerFactory;

public final class VisualGridRegistry {

    public static void registerVisualGrid() {
        registerNodePeerFactory(VisualGrid.class, HtmlVisualGridPeer::new);
    }

}
