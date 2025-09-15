package dev.webfx.extras.player.impl;

import dev.webfx.kit.launcher.WebFxKitLauncher;
import dev.webfx.kit.util.properties.ObservableLists;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * @author Bruno Salmon
 */
public final class MediaViewWithOverlay {

    private Node mediaView;
    private boolean fullscreen;
    private ObservableList<Node> overlayChildren; // lazy instantiation (for appRequestedOverlayChildren() test)
    private final StackPane container = new StackPane() {
        @Override
        protected void layoutChildren() {
            // The media view should always fill the whole container
            if (mediaView != null)
                mediaView.resizeRelocate(0, 0, getWidth(), getHeight());
            super.layoutChildren(); // for possible overlay children (won't touch mediaView because unmanaged)
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
            if (mediaView != null) {
                mediaView.setManaged(false); // this is to be ignored in the overlay children layout
                container.getChildren().add(0, mediaView);
            }
            this.mediaView = mediaView;
        }
    }

    public Region getContainer() {
        return container;
    }

    public ObservableList<Node> getOverlayChildren() {
        if (overlayChildren == null) {
            overlayChildren = ObservableLists.newObservableListWithListener(change -> {
                // Reflecting changes made on overlayChildren to the container
                while (change.next()) {
                    container.getChildren().removeAll(change.getRemoved());
                    container.getChildren().addAll(change.getAddedSubList());
                }
            });
        }
        return overlayChildren;
    }

    public boolean appRequestedOverlayChildren() {
        return overlayChildren != null;
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
