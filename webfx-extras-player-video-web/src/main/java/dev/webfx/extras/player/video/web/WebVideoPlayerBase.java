package dev.webfx.extras.player.video.web;

import dev.webfx.extras.player.Status;
import dev.webfx.extras.player.video.IntegrationMode;
import dev.webfx.extras.player.video.impl.VideoPlayerBase;
import dev.webfx.extras.webview.pane.LoadOptions;
import dev.webfx.extras.webview.pane.WebViewPane;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.platform.console.Console;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * @author Bruno Salmon
 */
public abstract class WebVideoPlayerBase extends VideoPlayerBase {

    protected static final boolean IS_BROWSER = WebViewPane.isBrowser();

    protected final WebViewPane webViewPane = new WebViewPane();

    public WebVideoPlayerBase() {
        this(IntegrationMode.EMBEDDED);
    }

    public WebVideoPlayerBase(IntegrationMode integrationMode) {
        super(integrationMode);
        if (hasIFrame()) {
            FXProperties.runOnPropertiesChange(() -> {
                if (webViewPane.getScene() != null) { //
                    Status status = getStatus();
                    switch (status) {
                        case READY:
                        case PLAYING:
                        case PAUSED:
                            Console.log("⚠️ Reloading video iFrame after reinsertion into DOM with status = " + status);
                            display(status);
                    }
                }
            }, webViewPane.sceneProperty());
        }
    }

    protected boolean hasIFrame() {
        return IS_BROWSER;
    }

    @Override
    public Node getVideoView() {
        return webViewPane;
    }

    @Override
    public void displayVideo() {
        display(Status.READY);
    }

    private void display(Status onLoadSuccessStatus) {
        String track = getCurrentTrack();
        if (track == null)
            stop();
        else {
            String url = trackUrl(track, onLoadSuccessStatus == Status.PLAYING);
            webViewPane.loadFromUrl(url, new LoadOptions().setOnLoadSuccess(() ->
                setStatus(onLoadSuccessStatus))
                , null);
        }
    }

    @Override
    public void play() {
        display(Status.PLAYING);
    }

    protected abstract String trackUrl(String track, boolean play);

    @Override
    public void pause() {
        display(Status.PAUSED); // reloading video - Not great, but what better can we do?
    }

    @Override
    public void stop() {
        //Console.log("WebVideoPlayerBase.stop() is called");
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
