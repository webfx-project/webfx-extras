package dev.webfx.extras.player.impl;

import dev.webfx.extras.panes.MonoPane;
import dev.webfx.kit.launcher.WebFxKitLauncher;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 * @author Bruno Salmon
 */
public final class MediaViewWithOverlay {

    private Node mediaView;
    private MonoPane overlay;
    private boolean fullscreen;
    private final Pane container = new Pane() {
        @Override
        protected void layoutChildren() {
            double width = getWidth(), height = getHeight();
            if (mediaView != null)
                mediaView.resizeRelocate(0, 0, width, height);
            if (overlay != null)
                overlay.resizeRelocate(0, 0, width, height);
        }
    };

    public MediaViewWithOverlay() {
    }

    public MediaViewWithOverlay(Node mediaView) {
        setMediaView(mediaView);
    }

    public void setMediaView(Node mediaView) {
        if (this.mediaView != mediaView) {
            if (this.mediaView != null)
                container.getChildren().remove(this.mediaView);
            if (mediaView != null)
                container.getChildren().add(0, mediaView);
            this.mediaView = mediaView;
        }
    }

    public Node getContainer() {
        return container;
    }

    public MonoPane getOverlay() {
        if (overlay == null) {
            overlay = new MonoPane();
            overlay.setMouseTransparent(true);
            overlay.getStyleClass().add("overlay");
            container.getChildren().add(overlay);
        }
        return overlay;
    }

    public boolean hasOverlay() {
        return overlay != null;
    }

    public boolean requestFullscreen() {
        return fullscreen = WebFxKitLauncher.requestNodeFullscreen(container);
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public boolean exitFullscreen() {
        if (!WebFxKitLauncher.exitFullscreen())
            return false;
        fullscreen = false;
        return true;
    }
}
