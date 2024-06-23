package dev.webfx.extras.player.video.web.wistia;

import dev.webfx.extras.webview.pane.ResizableRectangle;

/**
 * @author Bruno Salmon
 */
class WistiaDivNode extends ResizableRectangle {

    public WistiaDivNode() {
        getProperties().put("webfx-htmlTag", "div");
    }
}
