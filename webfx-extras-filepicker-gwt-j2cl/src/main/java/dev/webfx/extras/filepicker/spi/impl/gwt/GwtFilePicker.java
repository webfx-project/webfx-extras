package dev.webfx.extras.filepicker.spi.impl.gwt;


import dev.webfx.extras.filepicker.spi.impl.BaseFilePicker;
import dev.webfx.extras.filepicker.spi.impl.FilePickerClickableRegion;

import static dev.webfx.kit.mapper.peers.javafxgraphics.NodePeerFactoryRegistry.registerNodePeerFactory;

/**
 * @author Bruno Salmon
 */
public class GwtFilePicker extends BaseFilePicker {

    static {
        registerNodePeerFactory(FilePickerClickableRegion.class, HtmlFilePickerClickableRegionPeer::new);
    }

}
