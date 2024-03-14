package dev.webfx.extras.player.video.impl;

import dev.webfx.extras.panes.MonoPane;
import dev.webfx.extras.player.Status;
import dev.webfx.extras.player.video.IntegrationMode;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.Node;
import javafx.scene.web.WebView;
import javafx.util.Duration;

/**
 * @author Bruno Salmon
 */
public abstract class WebEmbedVideoPlayerBase extends VideoPlayerBase {

    private final MonoPane container = new MonoPane();
    protected WebView webView;

    public WebEmbedVideoPlayerBase() {
        this(IntegrationMode.EMBEDDED);
    }

    public WebEmbedVideoPlayerBase(IntegrationMode integrationMode) {
        super(integrationMode);
    }

    @Override
    public Node getVideoView() {
        return container;
    }

    @Override
    public void play() {
        String track = getCurrentTrack();
        if (track == null)
            stop();
        else {
            if (webView == null)
                webView = new WebView();
            // On mobiles, we need to ensure the web view container position is stabilized BEFORE attaching the OS web
            // view (otherwise the OS web view position may be wrong)
            if (container.getContent() == webView && container.getWidth() > 0)
                loadTrackUrl();
            else { // Means that the web view container is not stabilized
                // We set its content to a resizable rectangle, so it will be resized
                container.setContent(new ResizableRectangle());
                // We add a width listener to react when the container has been resized to a stable size
                container.widthProperty().addListener(new InvalidationListener() {
                    @Override
                    public void invalidated(Observable observable) {
                        // One-time listener => we remove it
                        container.widthProperty().removeListener(this);
                        // Now that the container has a stabilized size (which will be the size of the video player),
                        // we can set its content to the web view
                        container.setContent(webView);
                        // and load & start the video
                        loadTrackUrl();
                    }
                });
            }
        }
    }

    private void loadTrackUrl() {
        String track = getCurrentTrack();
        String url = trackUrl(track);
        webView.getEngine().load(url);
        setStatus(Status.PLAYING);
    }

    protected abstract String trackUrl(String track);

    @Override
    public void pause() {
        stop();
    }

    @Override
    public void stop() {
        if (webView != null)
            webView.getEngine().load(null);
        setStatus(Status.STOPPED);
        webView = null;
        container.setContent(null);
    }

    @Override
    public void seek(Duration seekTime) {
    }

    @Override
    public boolean supportsEventHandlers() {
        return false;
    }

}
