package dev.webfx.extras.player.video.web;

import dev.webfx.extras.player.*;
import dev.webfx.extras.player.impl.MediaBase;
import dev.webfx.extras.player.video.impl.VideoPlayerBase;
import dev.webfx.extras.webview.pane.LoadOptions;
import dev.webfx.extras.webview.pane.WebViewPane;
import dev.webfx.kit.launcher.WebFxKitLauncher;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.platform.console.Console;
import dev.webfx.platform.util.Arrays;
import dev.webfx.platform.util.Strings;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.web.WebView;
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
        // Player status management when the webview is removed from or reinserted into the scene graph. Indeed, when
        // this happens, the platform (especially browsers with iFrame) may stop the player and we are not automatically
        // notified of this.
        boolean hasIFrame = hasIFrame();
        boolean notificationSupport = getNavigationSupport().notification();
        Status[] noSceneStatus = { null };
        if (hasIFrame || !notificationSupport) {
            FXProperties.runOnPropertiesChange(() -> {
                Status status = getStatus();
                if (webViewPane.getScene() == null) { // Removed from the scene graph or DOM!
                    noSceneStatus[0] = status; // memorising the status to eventually reapply it when it's reinserted
                    // Browsers automatically stops videos in iFrame, but we didn't get a notification for it.
                    // And for the other platforms, we also simulate the same behaviour, in order to notify the
                    // player group of the change (see maybe-playing management for players with no notification support)
                    switch (status) {
                        case READY:
                        case PLAYING:
                            setStatus(Status.PAUSED); // to notify the player group of the change
                    }
                } else { // Re-inserted into the scene graph / DOM
                    if (noSceneStatus[0] != null)
                        status = noSceneStatus[0];
                    switch (status) {
                        case READY:
                        case PLAYING:
                        case PAUSED:
                            if (hasIFrame) {
                                if (webViewPane.isLoading()) {
                                    Console.log("⚠️ Video iFrame already reloading after reinsertion into DOM with status = " + status);
                                } else {
                                    Console.log("⚠️ Reloading video iFrame after reinsertion into DOM with status = " + status);
                                    display(status); // will reload
                                }
                            } else { // no need to reload when not an iFrame
                                Console.log("⚠️ Restoring video status after reinsertion into scene graph with status = " + status);
                                setStatus(status);
                            }
                    }
                }
            }, webViewPane.sceneProperty());
        }
    }

    protected boolean hasIFrame() {
        return IS_BROWSER;
    }

    @Override
    public Node getMediaView() {
        return webViewPane;
    }

    @Override
    public void displayVideo() {
        display(Status.READY);
    }

    private void display(Status onLoadSuccessStatus) {
        if (getMedia() == null)
            stop();
        else {
            String url = generateMediaEmbedFullUrl(onLoadSuccessStatus == Status.PLAYING);
            webViewPane.loadFromUrl(url, new LoadOptions().setOnLoadSuccess(() ->
                setStatus(onLoadSuccessStatus))
                , null);
        }
    }

    @Override
    public FeatureSupport getNavigationSupport() {
        return FeatureSupport.USER_AND_API_BY_RESTART_SUPPORT;
    }

    @Override
    public void play() {
        display(Status.PLAYING);
    }

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
    public FeatureSupport getFullscreenSupport() {
        if (WebFxKitLauncher.isFullscreenEnabled())
            return FeatureSupport.FULL_SUPPORT;
        return FeatureSupport.USER_SUPPORT;
    }

    @Override
    public void requestFullscreen() {
        WebView webView = webViewPane.getWebView();
        WebFxKitLauncher.requestNodeFullscreen(webView);
    }

    @Override
    public void cancelFullscreen() {
        WebFxKitLauncher.exitFullscreen();
    }

    @Override
    public FeatureSupport getMuteSupport() {
        return FeatureSupport.USER_SUPPORT;
    }

    protected Media acceptMedia(String mediaSource, MediaMetadata mediaMetadata, String... possiblePrefixes) {
        if (Strings.startsWith(mediaSource, "http:"))
            mediaSource = "https:" + mediaSource.substring(5);
        if (Strings.startsWith(mediaSource, "https://www."))
            mediaSource = "https://" + mediaSource.substring(12);
        String videoIdAndParams = matchPrefix(mediaSource, possiblePrefixes);
        if (videoIdAndParams == null)
            return null;
        String[] params = videoIdAndParams.replace("?", "&").split("&");
        String videoId = params[0];
        StartOptions startOptions = null;
        if (params.length > 1) {
            StartOptionsBuilder sob = new StartOptionsBuilder();
            for (int i = 1; i < params.length; i++) {
                String[] nameValue = params[i].split("=");
                queryParamToStartOption(nameValue[0], Arrays.getValue(nameValue,1), sob);
            }
            startOptions = sob.build();
        }
        return new MediaBase(mediaSource, videoId, startOptions, mediaMetadata, this);
    }

    protected abstract void queryParamToStartOption(String name, String value, StartOptionsBuilder sob);

    protected String generateMediaEmbedFullUrl(boolean forceAutoplay) {
        String url = generateMediaEmbedRawUrl();
        updatePlayingStartingOption(forceAutoplay);
        StringBuilder sb = new StringBuilder();
        appendUrlParameters(playingStartingOption, sb);
        if (sb.length() > 0)
            url = url + "?" + Strings.removePrefix(sb.toString(), "&");
        Double aspectRatio = playingStartingOption.aspectRatio();
        if (aspectRatio != null) {
            webViewPane.prefHeightProperty().bind(FXProperties.computeDeferred(webViewPane.widthProperty(), w -> w.doubleValue() / aspectRatio));
            webViewPane.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        } else {
            webViewPane.prefHeightProperty().unbind();
        }

        return url;
    }

    protected abstract String generateMediaEmbedRawUrl();

    protected abstract void appendUrlParameters(StartOptions so, StringBuilder sb);

}
