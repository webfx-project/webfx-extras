package dev.webfx.extras.player.video.wistia;

import dev.webfx.extras.player.Status;
import dev.webfx.extras.player.video.IntegrationMode;
import dev.webfx.extras.player.video.impl.WebEmbedVideoPlayerBase;
import dev.webfx.extras.webview.pane.WebViewPane;
import dev.webfx.platform.console.Console;
import javafx.scene.Node;

/**
 * @author Bruno Salmon
 */
public class WistiaVideoPlayer extends WebEmbedVideoPlayerBase {

    private static final boolean IS_SEAMLESS = WebViewPane.isBrowser();

    static {
        if (IS_SEAMLESS) {
            WebViewPane.executeSeamlessScriptInBrowser(
                    "window._wq = window._wq || [];" +
                    "window.webfx_extras_wistia_videos = {};" +
                    "const script = document.createElement('script');\n" +
                    "script.src = '//fast.wistia.com/assets/external/E-v1.js';\n" +
                    "document.head.appendChild(script);"
            );
        }
    }

    private void callVideoSeamlessly(String script) {
        Console.log("Calling: " + script);
        String track = getCurrentTrack();
        WebViewPane.executeSeamlessScriptInBrowser(
           "const video = window.webfx_extras_wistia_videos['" + track + "'];" +
           " if (video) {" + script +
           "} else {" +
           "    _wq.push({ id: '" + track + "', playerColor: 'EE7130', onReady: function(video) {\n" +
           "    if (!window.webfx_extras_wistia_videos['" + track + "']) {\n" +
           "    " + script + "}\n" +
           "    window.webfx_extras_wistia_videos['" + track + "'] = video;\n" +
           "}});" +
           "}"
        );
    }

    private final Node seamlessContainer = IS_SEAMLESS ? new WistiaDivNode() : null;

    public WistiaVideoPlayer() {
        super(isSeamless() ? IntegrationMode.SEAMLESS : IntegrationMode.EMBEDDED);
    }

    public static boolean isSeamless() {
        return IS_SEAMLESS;
    }

    @Override
    public Node getVideoView() {
        return IS_SEAMLESS ? seamlessContainer : super.getVideoView();
    }

    @Override
    public void play() {
        if (IS_SEAMLESS) {
            if (getStatus() == Status.PAUSED) {
                callVideoSeamlessly("video.play()");
            } else {
                String track = getCurrentTrack();
                seamlessContainer.getStyleClass().setAll("wistia_embed", "wistia_async_" + track);
                // The replaceWith() call purpose is actually to set the color player.
                callVideoSeamlessly("video.replaceWith('" + track + "', { playerColor: 'EE7130'}); video.play();");
            }
            setStatus(Status.PLAYING);
            // TODO: the player is automatically paused when switching to another activity (removed from DOM?), but
            // TODO: is reestablished when going back, but the status is still playing here (pause not detected)
            // TODO: so the user has to click pause and then play to play gain (while he expects only 1 click) => TO FIX
        } else {
            super.play();
        }
    }

    @Override
    public void pause() {
        if (IS_SEAMLESS) {
            callVideoSeamlessly("video.pause()");
            setStatus(Status.PAUSED);
        } else {
            super.pause();
        }
    }

    @Override
    public void stop() {
        if (IS_SEAMLESS) {
            callVideoSeamlessly("video.pause()");
            setStatus(Status.STOPPED);
        } else {
            super.stop();
        }
    }

    @Override
    protected String trackUrl(String track) { // used in non-seamless mode (iFrame)
        return "https://fast.wistia.net/embed/iframe/" + track + "?autoplay=true&playerColor=EE7130";
    }

}
