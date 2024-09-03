package dev.webfx.extras.player.video.web;

import dev.webfx.extras.player.Status;
import dev.webfx.extras.player.video.IntegrationMode;
import dev.webfx.extras.player.video.impl.VideoPlayerBase;
import dev.webfx.extras.webview.pane.LoadOptions;
import dev.webfx.extras.webview.pane.WebViewPane;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * @author Bruno Salmon
 */
public abstract class WebVideoPlayerBase extends VideoPlayerBase {

    protected final WebViewPane webViewPane = new WebViewPane();

    public WebVideoPlayerBase() {
        this(IntegrationMode.EMBEDDED);
    }

    public WebVideoPlayerBase(IntegrationMode integrationMode) {
        super(integrationMode);
    }

    @Override
    public Node getVideoView() {
        return webViewPane;
    }

    @Override
    public void displayVideo() {
        display(false);
    }

    private void display(boolean play) {
        String track = getCurrentTrack();
        if (track == null)
            stop();
        else {
            String url = trackUrl(track, play);
            webViewPane.loadFromUrl(url, new LoadOptions().setOnLoadSuccess(() -> setStatus(play ? Status.PLAYING : Status.READY)), null);
        }
    }

    @Override
    public void play() {
        display(true);
    }

    protected abstract String trackUrl(String track, boolean play);

    @Override
    public void pause() {
        stop(); // TODO: can we do better than stopping?
    }

    @Override
    public void stop() {
        webViewPane.unload();
        setStatus(Status.STOPPED);
    }

    @Override
    public void seek(Duration seekTime) {
    }

    @Override
    public boolean supportsEventHandlers() {
        return false;
    }

}
