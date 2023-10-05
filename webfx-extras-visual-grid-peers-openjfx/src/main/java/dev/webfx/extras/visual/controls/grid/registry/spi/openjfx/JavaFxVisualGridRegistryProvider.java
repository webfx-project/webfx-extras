package dev.webfx.extras.visual.controls.grid.registry.spi.openjfx;

import dev.webfx.extras.cell.collator.grid.GridCollator;
import dev.webfx.extras.visual.controls.grid.VisualGrid;
import dev.webfx.extras.visual.controls.grid.peers.openjfx.FxVisualGridPeer;
import dev.webfx.extras.visual.controls.grid.registry.spi.VisualGridRegistryProvider;

import static dev.webfx.kit.mapper.peers.javafxgraphics.NodePeerFactoryRegistry.registerNodePeerFactory;

public class JavaFxVisualGridRegistryProvider implements VisualGridRegistryProvider {

    static {
        registerNodePeerFactory(GridCollator.class, GridCollator.GridCollatorPeer::new);
    }

    public void registerVisualGrid() {
        registerNodePeerFactory(VisualGrid.class, FxVisualGridPeer::new);
    }

}
