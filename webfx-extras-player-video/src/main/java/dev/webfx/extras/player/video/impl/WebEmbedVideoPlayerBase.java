package dev.webfx.extras.player.video.impl;

import dev.webfx.extras.panes.MonoPane;
import dev.webfx.extras.player.Status;
import dev.webfx.extras.player.video.IntegrationMode;
import javafx.application.Platform;
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
            Platform.runLater(() -> { // Required on mobiles to ensure the web view container position is stabilized before attaching the OS web view (otherwise the OS web view position may be wrong)
                String url = trackUrl(track);
                container.setContent(webView);
                webView.getEngine().load(url);
                setStatus(Status.PLAYING);
            });
        }
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
