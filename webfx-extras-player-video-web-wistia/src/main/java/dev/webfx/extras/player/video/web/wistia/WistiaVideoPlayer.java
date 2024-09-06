package dev.webfx.extras.player.video.web.wistia;

import dev.webfx.extras.player.Status;
import dev.webfx.extras.player.video.web.SeamlessCapableWebVideoPlayer;
import dev.webfx.extras.webview.pane.LoadOptions;
import dev.webfx.extras.webview.pane.WebViewPane;
import javafx.util.Duration;

/**
 * @author Bruno Salmon
 */
public class WistiaVideoPlayer extends SeamlessCapableWebVideoPlayer {

    static {
        if (IS_SEAMLESS) {
            WebViewPane.executeSeamlessScriptInBrowser(
                "window._wq = window._wq || [];" +
                "window.webfx_extras_wistia_videos = {};" +
                "const script = document.createElement('script');\n" +
                "script.src = '//fast.wistia.com/assets/external/E-v1.js';\n" +
                "document.head.appendChild(script);\n" +
                "window.bindWistiaVideo = function(video, playerId) {\n" +
                "    const javaPlayer = window[playerId];\n" +
                "    video.bind('play',  function() { javaPlayer.onPlay();  });\n" +
                "    video.bind('pause', function() { javaPlayer.onPause(); });\n" +
                "    video.bind('end',   function() { javaPlayer.onEnd();   });\n" +
                "};"
            );
        }
    }

    @Override
    protected String trackUrl(String track, boolean play) { // called in non-seamless mode only (iFrame)
        return "https://fast.wistia.net/embed/iframe/" + track + "?" + (play ? "autoplay=true&" : "") + "playerColor=EE7130";
    }

    private void seamless_call(String script) {
        //Console.log("Calling: " + script);
        String track = getCurrentTrack();
        webViewPane.loadFromScript(
            "const playerId = '" + playerId + "';\n" +
            "const track = '" + track + "';\n" +
            "const video = window.webfx_extras_wistia_videos[playerId];\n" +
            "if (video) {\n" + script +
            "} else {" +
            "    _wq.push({ id: track, playerColor: 'EE7130', onReady: function(video) {\n" +
            "    if (!window.webfx_extras_wistia_videos[playerId]) {\n" +
            "        window.bindWistiaVideo(video, playerId);\n" +
            "        " + script + "}\n" +
            "    window.webfx_extras_wistia_videos[playerId] = video;\n" +
            "}});" +
            "}", new LoadOptions()
                .setSeamlessInBrowser(true)
                .setSeamlessStyleClass("wistia_embed", "wistia_async_" + track)
                .setOnWebWindowReady(() -> {
                    webViewPane.setWindowMember(playerId, this);
                })
            ,
            null);
    }

    @Override
    protected void seamless_displayVideo() {
        seamless_call("");
    }

    @Override
    public void resetToInitialState() {
        if (IS_SEAMLESS)
            seamless_call("video.replaceWith('" + getCurrentTrack() + "', { playerColor: 'EE7130'}); window.bindWistiaVideo(video, playerId);");
        else
            super.resetToInitialState();
    }

    @Override
    protected void seamless_play() {
        if (getStatus() == Status.PAUSED) {
            seamless_call("video.play()");
        } else {
            String track = getCurrentTrack();
            // The replaceWith() call purpose is actually to set the color player.
            seamless_call("video.replaceWith('" + track + "', { playerColor: 'EE7130'}); video.play();");
        }
    }

    @Override
    protected void seamless_pause() {
        seamless_call("video.pause()");
    }

    @Override
    protected void seamless_stop() {
        seamless_call("video.pause()");
        setStatus(Status.STOPPED);
    }

    @Override
    public void seek(Duration seekTime) {
        if (IS_SEAMLESS)
            seamless_call("video.time(" + seekTime.toSeconds() + ")");
    }

    @Override
    protected void seamless_requestFullscreen() {
        seamless_call("video.requestFullscreen()");
    }

    @Override
    protected void seamless_cancelFullscreen() {
        seamless_call("video.cancelFullscreen()");
    }

}
