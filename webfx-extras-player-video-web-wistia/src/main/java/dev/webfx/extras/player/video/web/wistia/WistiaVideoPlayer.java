package dev.webfx.extras.player.video.web.wistia;

import dev.webfx.extras.media.metadata.MediaMetadata;
import dev.webfx.extras.player.*;
import dev.webfx.extras.player.video.web.SeamlessCapableWebVideoPlayer;
import dev.webfx.extras.webview.pane.LoadOptions;
import dev.webfx.extras.webview.pane.WebViewPane;
import dev.webfx.platform.util.Booleans;
import javafx.scene.paint.Color;
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
                "window.bindWistiaVideo = function(video, javaPlayer) {\n" +
                "    video.bind('play',  function() { javaPlayer.onPlay();  });\n" +
                "    video.bind('pause', function() { javaPlayer.onPause(); });\n" +
                "    video.bind('end',   function() { javaPlayer.onEnd();   });\n" +
                "};"
            );
        }
    }

    @Override
    public Media acceptMedia(String mediaSource, MediaMetadata mediaMetadata) {
        return acceptMedia(mediaSource, mediaMetadata,
            "https://fast.wistia.net/embed/iframe/",
            "wistia:",
            "https://*.wistia.com/medias/"
        );
    }

    /*
    See https://docs.wistia.com/docs/embed-options-and-plugins#options for the list of parameters
     */

    @Override
    protected void queryParamToStartOption(String name, String value, StartOptionsBuilder sob) {
        switch (name) {
            case "autoplay": sob.setAutoplay(Booleans.isFalse(value)); break;
            case "playerColor": sob.setPlayerColor(Color.web(value)); break;
        }
    }

    @Override
    protected String generateMediaEmbedRawUrl() { // called in non-seamless mode only (iFrame)
        return "https://fast.wistia.net/embed/iframe/" + getMedia().getId();
    }

    @Override
    protected void appendUrlParameters(StartOptions so, StringBuilder sb) {
        if (Booleans.isTrue(so.autoplay()))
            sb.append("&autoplay=true");
        if (so.playerColor() != null)
            sb.append("&playerColor=").append(toWebColor(so.playerColor()));
    }

    private void seamless_call(String script) {
        if (script.contains("$playerColor$")) {
            String playerColor = toWebColor(playingStartingOption.playerColor());
            script = script.replace("$playerColor$", playerColor == null ? "" : "playerColor: '" + playerColor + "'");
        }
        //Console.log("Calling: " + script);
        String mediaId = getMediaId();
        webViewPane.loadFromScript(
            "const playerId = '" + playerId + "';\n" +
            "const mediaId = '" + mediaId + "';\n" +
            "const video = window.webfx_extras_wistia_videos[playerId];\n" +
            "if (video) {\n" + script +
            "} else {\n" +
            "    _wq.push({ id: mediaId, onReady: function(video) {\n" +
            "        if (!window.webfx_extras_wistia_videos[playerId]) {\n" +
            "            window.webfx_extras_wistia_videos[playerId] = video;\n" +
            "            " + script +
            "        }\n" +
            "        const javaPlayer = window[playerId];\n" +
            "        window.bindWistiaVideo(video, javaPlayer);\n" +
            "        javaPlayer.onReady();\n" +
            "    }});\n" +
            "}", new LoadOptions()
                .setSeamlessInBrowser(true)
                .setSeamlessStyleClass("wistia_embed", "wistia_async_" + mediaId)
                .setOnWebWindowReady(() -> webViewPane.setWindowMember(playerId, this))
            ,
            null);
    }

    @Override
    protected void seamless_displayVideo() {
        seamless_call("");
    }

    @Override
    public void resetToInitialState() {
        if (IS_SEAMLESS) {
            seamless_call("video.replaceWith('" + getMediaId() + "', { $playerColor$ }); window.bindWistiaVideo(video, playerId);");
        } else
            super.resetToInitialState();
    }

    @Override
    protected void seamless_play() {
        if (getStatus() == Status.PAUSED) {
            seamless_call("video.play()");
        } else {
            String mediaId = getMediaId();
            // The replaceWith() call purpose is actually to set the color player.
            seamless_call("video.replaceWith('" + mediaId + "', { $playerColor$ }); video.play();");
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

    private static String toWebColor(Color c) {
        return c == null ? null : toHex2(c.getRed()) + toHex2(c.getGreen()) + toHex2(c.getBlue());
    }

    private static String toHex2(double colorComponent) {
        String s = Integer.toHexString((int) (colorComponent * 255)).toUpperCase();
        return s.length() == 1 ? "0" + s : s;
    }

}
