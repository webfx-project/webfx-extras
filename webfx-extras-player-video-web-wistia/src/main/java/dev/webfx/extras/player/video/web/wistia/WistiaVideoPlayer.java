package dev.webfx.extras.player.video.web.wistia;

import dev.webfx.extras.player.Status;
import dev.webfx.extras.player.video.IntegrationMode;
import dev.webfx.extras.player.video.web.WebVideoPlayerBase;
import dev.webfx.extras.webview.pane.LoadOptions;
import dev.webfx.extras.webview.pane.WebViewPane;

/**
 * @author Bruno Salmon
 */
public class WistiaVideoPlayer extends WebVideoPlayerBase {

    private static final boolean IS_SEAMLESS = WebViewPane.isBrowser();

    private boolean fullscreen;

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
        //Console.log("Calling: " + script);
        String track = getCurrentTrack();
        webViewPane.loadFromScript(
           "const video = window.webfx_extras_wistia_videos['" + track + "'];" +
           " if (video) {" + script +
           "} else {" +
           "    _wq.push({ id: '" + track + "', playerColor: 'EE7130', onReady: function(video) {\n" +
           "    if (!window.webfx_extras_wistia_videos['" + track + "']) {\n" +
           "    " + script + "}\n" +
           "    window.webfx_extras_wistia_videos['" + track + "'] = video;\n" +
           "}});" +
           "}", new LoadOptions()
                        .setSeamlessInBrowser(true)
                        .setSeamlessStyleClass("wistia_embed", "wistia_async_" + track),
                null);
        webViewPane.setWindowMember("webfxWistiaVideoPlayer", this);
    }

    public WistiaVideoPlayer() {
        super(isSeamless() ? IntegrationMode.SEAMLESS : IntegrationMode.EMBEDDED);
        if (IS_SEAMLESS) {
            getVideoView().sceneProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null && isPlaying())
                    setStatus(Status.STOPPED);
            });
        }
    }

    public static boolean isSeamless() {
        return IS_SEAMLESS;
    }

    @Override
    public void play() {
        if (IS_SEAMLESS) {
            if (getStatus() == Status.PAUSED) {
                callVideoSeamlessly("video.play()");
            } else {
                String track = getCurrentTrack();
                // The replaceWith() call purpose is actually to set the color player.
                callVideoSeamlessly("video.replaceWith('" + track + "', { playerColor: 'EE7130'}); video.play(); video.bind('play', function() { window.webfxWistiaVideoPlayer.onPlay(); }); video.bind('pause', function() { window.webfxWistiaVideoPlayer.onPause(); }); video.bind('end', function() { window.webfxWistiaVideoPlayer.onEnd(); });");
            }
        } else {
            super.play();
        }
    }

    @Override
    public void pause() {
        if (IS_SEAMLESS) {
            callVideoSeamlessly("video.pause()");
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

    // Callback methods called by Wistia in seamless mode

    public void onPlay() {
        //Console.log("onPlay()");
        setStatus(Status.PLAYING);
    }

    public void onPause() {
        //Console.log("onPause()");
        if (getStatus() != Status.STOPPED)
            setStatus(Status.PAUSED);
    }

    public void onEnd() {
        //Console.log("onEnd()");
        setStatus(Status.STOPPED);
    }

    @Override
    public boolean supportsFullscreen() {
        if (IS_SEAMLESS)
            return true;
        return super.supportsFullscreen();
    }

    @Override
    public void requestFullscreen() {
        if (IS_SEAMLESS && getStatus() == Status.PLAYING) {
            callVideoSeamlessly("video.requestFullscreen()");
            fullscreen = true;
        }
    }

    @Override
    public boolean isFullscreen() {
        if (fullscreen)
            return true;
        return super.isFullscreen();
    }

    @Override
    public void cancelFullscreen() {
        if (IS_SEAMLESS)
            callVideoSeamlessly("video.cancelFullscreen()");
        else
            super.cancelFullscreen();
    }
}
