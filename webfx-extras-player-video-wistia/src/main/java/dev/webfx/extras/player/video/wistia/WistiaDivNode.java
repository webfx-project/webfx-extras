package dev.webfx.extras.player.video.wistia;

import dev.webfx.extras.player.video.impl.ResizableRectangle;

/**
 * @author Bruno Salmon
 */
class WistiaDivNode extends ResizableRectangle {

    public WistiaDivNode() {
        getProperties().put("webfx-htmlTag", "div");
    }
}
