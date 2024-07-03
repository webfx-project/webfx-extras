package dev.webfx.extras.webview.pane;

import javafx.scene.layout.Region;

/**
 * @author Bruno Salmon
 */
final class SeamlessDiv extends Region {

    public SeamlessDiv() {
        getProperties().put("webfx-htmlTag", "div");
    }

    public SeamlessDiv(String id) {
        this();
        setId(id);
    }

}