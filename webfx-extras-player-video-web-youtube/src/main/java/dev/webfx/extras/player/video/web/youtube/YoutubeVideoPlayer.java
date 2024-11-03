package dev.webfx.extras.player.video.web.youtube;

import dev.webfx.extras.player.Media;
import dev.webfx.extras.player.MediaMetadata;
import dev.webfx.extras.player.StartOptions;
import dev.webfx.extras.player.StartOptionsBuilder;
import dev.webfx.extras.player.video.web.SeamlessCapableWebVideoPlayer;
import dev.webfx.extras.webview.pane.LoadOptions;
import dev.webfx.extras.webview.pane.WebViewPane;
import javafx.util.Duration;

/**
 * @author Bruno Salmon
 */
public class YoutubeVideoPlayer extends SeamlessCapableWebVideoPlayer {

    static {
        if (IS_SEAMLESS) {
            WebViewPane.executeSeamlessScriptInBrowser(
                "window.webfx_extras_youtube_players = {};\n" +
                "window.webfx_extras_youtube_functions = [];\n" +
                "window.onYouTubeIframeAPIReady = function() {\n" +
                "    for (var i = 0; i < window.webfx_extras_youtube_functions.length; i++) {\n" +
                "        window.webfx_extras_youtube_functions[i]();" +
                "    }\n" +
                "};\n" +
                "const script = document.createElement('script');\n" +
                "script.src = 'https://www.youtube.com/iframe_api';\n" +
                "document.head.appendChild(script);"
            );
        }
    }

    @Override
    public Media acceptMedia(String mediaSource, MediaMetadata mediaMetadata) {
        return acceptMedia(mediaSource, mediaMetadata,
            "https://youtube.com/watch?v=",
            "https://youtube.com/embed/",
            "youtube:"
        );
    }

    @Override
    protected void queryParamToStartOption(String name, String value, StartOptionsBuilder sob) {
    }

        /*
Here are some of the most commonly used parameters to remove or hide overlays:
- controls=0: Hides the player controls (like play, pause, volume, etc.). However, this does not completely remove all overlays, as some branding elements may still appear.
- modestbranding=1: Minimizes YouTube branding by hiding the YouTube logo in the control bar. Note that this parameter only reduces the branding and does not completely remove it.
- rel=0: Disables showing related videos at the end of the video. This only works if you use the old parameter behavior; in 2018, YouTube updated this behavior so that rel=0 only shows related videos from the same channel.
- fs=0: Disables the fullscreen button on the player.
- showinfo=0: This parameter used to remove the title and uploader info at the top of the video. However, this parameter was deprecated by YouTube in 2018, and as such, it no longer works.
- iv_load_policy=3: Hides video annotations. The value can be 1 to show annotations and 3 to hide them.
- autohide=1: Automatically hides the video controls after a few seconds if the video is not interacted with.
*/

    @Override
    protected String generateMediaEmbedRawUrl() { // called in non-seamless mode only (iFrame)
        return "https://youtube.com/embed/" + getMedia().getId();
    }

    @Override
    protected void appendUrlParameters(StartOptions so, StringBuilder sb) {
        sb.append("rel=0&modestbranding=1&showinfo=0&iv_load_policy=3&autohide=1");
    }

    private void seamless_call(String script) {
        //Console.log("Calling: " + script);
        String mediaId = getMediaId();
        webViewPane.loadFromScript(
            "const playerId = '" + playerId + "';\n" +
            "var player = window.webfx_extras_youtube_players[playerId];\n" +
            "if (player) {\n" + script +
            "} else {\n" +
            "    var createPlayer = function() {\n" +
            "        const javaPlayer = window[playerId];\n" +
            //"        console.log('Creating YouTube player for javaPlayer = ' + javaPlayer );\n" +
            "        const container = document.getElementById(playerId);\n" +
            "        const child = document.createElement('div');\n" +
            "        container.appendChild(child);\n" +
            "        player = new YT.Player(child, {\n" +
            "            width: '100%',\n" +
            "            height: '100%',\n" +
            "            videoId: '" + mediaId + "',\n" +
            "            playerVars: {\n" +
            "                'rel': 0\n" +
            "            },\n" +
            "            events: {\n" +
            "                'onReady': function() {\n" +
            "                   javaPlayer.onReady();\n" +
            // Also reactivating onStateChange events in case the player has been detached and then reattached to the DOM
            "                   const message = JSON.stringify({event: 'command', func: 'addEventListener', args: ['onStateChange'], channel: 'widget'});\n" +
            "                   const iframe = document.getElementById(playerId).firstChild;\n" +
            "                   iframe.contentWindow.postMessage(message, 'https://www.youtube.com');\n" +
            "                },\n" +
            "                'onStateChange': function(event) {\n" +
            "                    console.log(event);\n" +
            "                    if      (event.data == YT.PlayerState.PLAYING) javaPlayer.onPlay();\n" +
            "                    else if (event.data == YT.PlayerState.PAUSED)  javaPlayer.onPause();\n" +
            "                    else if (event.data == YT.PlayerState.ENDED)   javaPlayer.onEnd();\n" +
            "                }\n" +
            "            }\n" +
            "        });\n" +
            "        " + script + ";\n" +
            "        window.webfx_extras_youtube_players[playerId] = player;\n" +
            "    };\n" +
            "    if (window.YT) createPlayer(); else window.webfx_extras_youtube_functions.push(createPlayer);" +
            "}", new LoadOptions()
                .setSeamlessInBrowser(true)
                .setSeamlessContainerId(playerId)
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
        if (IS_SEAMLESS) {
            seamless_call("player.cueVideoById('" + getMediaId() + "')");
        } else
            super.resetToInitialState();
    }

    @Override
    public void seek(Duration seekTime) {
        if (IS_SEAMLESS)
            seamless_call("player.seekTo(" + seekTime.toSeconds() + ", true)");
    }

    @Override
    protected void seamless_play() {
        seamless_call("player.playVideo()");
    }

    @Override
    protected void seamless_pause() {
        seamless_call("player.pauseVideo()");
    }

    @Override
    protected void seamless_stop() {
        seamless_call("player.stopVideo()");
    }

    @Override
    protected void seamless_requestFullscreen() {
        WebViewPane.executeSeamlessScriptInBrowser(
            "const playerId = '" + playerId + "';\n" +
            "const iframe = document.getElementById(playerId).firstChild;\n" +
            "iframe.requestFullscreen();"
        );
    }

    @Override
    protected void seamless_cancelFullscreen() {
        WebViewPane.executeSeamlessScriptInBrowser("document.exitFullscreen();");
    }

}
