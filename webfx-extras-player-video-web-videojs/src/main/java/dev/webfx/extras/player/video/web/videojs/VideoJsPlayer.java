package dev.webfx.extras.player.video.web.videojs;

import dev.webfx.extras.media.metadata.MediaMetadata;
import dev.webfx.extras.player.Media;
import dev.webfx.extras.player.StartOptions;
import dev.webfx.extras.player.StartOptionsBuilder;
import dev.webfx.extras.player.Status;
import dev.webfx.extras.player.video.web.SeamlessCapableWebVideoPlayer;
import dev.webfx.extras.webview.pane.LoadOptions;
import dev.webfx.extras.webview.pane.WebViewPane;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.platform.resource.Resource;
import dev.webfx.platform.util.Booleans;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author David Hello
 */
public final class VideoJsPlayer extends SeamlessCapableWebVideoPlayer {

    static {
        if (IS_SEAMLESS) {
            String videoJsScriptUrl = Resource.toUrl("videojs.js", VideoJsPlayer.class);
            String videoJsCssUrl = Resource.toUrl("videojs.css", VideoJsPlayer.class);
            WebViewPane.executeSeamlessScriptInBrowser(
                "const vjsCssLink = document.createElement('link');\n" +
                    "vjsCssLink.href = 'https://vjs.zencdn.net/8.12.0/video-js.css';\n" +
                    "vjsCssLink.rel = 'stylesheet';\n" +
                    "document.head.appendChild(vjsCssLink);\n" +

                    "const customCssLink = document.createElement('link');\n" +
                    "customCssLink.href = '" + videoJsCssUrl + "';\n" +
                    "customCssLink.rel = 'stylesheet';\n" +
                    "document.head.appendChild(customCssLink);\n" +

                    "window.webfx_extras_videojs_players = {};" +
                    "window.webfx_extras_videojs_functions = [];\n" +
                    "window.bindVideoJsPlayer = function(player, javaPlayer) {\n" +
                    "    player.on('play',  function() { javaPlayer.onPlay();  });\n" +
                    "    player.on('pause', function() { javaPlayer.onPause(); });\n" +
                    "    player.on('ended', function() { javaPlayer.onEnd();   });\n" +
                    "};" +
                    "const vjsScript = document.createElement('script');\n" +
                    "vjsScript.src = 'https://vjs.zencdn.net/8.12.0/video.min.js';\n" +
                    "vjsScript.onload = function() {\n" +
                    "    const qualitySelectorScript = document.createElement('script');\n" +
                    "    qualitySelectorScript.src = 'https://cdn.jsdelivr.net/npm/videojs-quality-selector-hls@1.1.1/dist/videojs-quality-selector-hls.min.js';\n" +
                    "    qualitySelectorScript.onload = function() {\n" +
                    "        const managerScript = document.createElement('script');\n" +
                    "        managerScript.src = '" + videoJsScriptUrl + "';\n" +
                    "        managerScript.onload = function() {\n" +
                    "            for (var i = 0; i < window.webfx_extras_videojs_functions.length; i++) {\n" +
                    "                window.webfx_extras_videojs_functions[i]();\n" +
                    "            }\n" +
                    "            window.webfx_extras_videojs_functions = [];\n" +
                    "        };\n" +
                    "        document.head.appendChild(managerScript);\n" +
                    "    };\n" +
                    "    document.head.appendChild(qualitySelectorScript);\n" +
                    "};\n" +
                    "document.head.appendChild(vjsScript);\n"
            );
        }
    }

    private String playerType = "";
    private static final String BUNNY_PLAYER="bunny";
    private String clientId = "";
    private String videoId = "";
    private String tracksParam = "";

    public VideoJsPlayer() {
        if (!IS_SEAMLESS) {
            FXProperties.runOnPropertyChange(status -> {
                //Use setRedirectConsole only for debug. It can cause some infinite loop in some cases.
                //webViewPane.setRedirectConsole(true);
                if (status == Status.READY) {
                    WebViewPane webViewPane1 = webViewPane;
                    javafx.scene.web.WebEngine webEngine = webViewPane1.getWebEngine();
                    webViewPane.loadFromScript("safeOnLoad(() => {loadVideo('" + playerType + "', '" + videoId + "', '" + clientId + "','" + tracksParam + "','" +  playerId + "');});", new LoadOptions(), false);
                }
            }, statusProperty());
        }
    }

    @Override
    public Media acceptMedia(String mediaSource, MediaMetadata mediaMetadata) {

        //For now, we just manage Bunny hosted video
        //Their URL is in the form: https://vz-eb594b94-203.b-cdn.net/febd54a8-3491-4e20-bec5-96b31c31636a/playlist.m3u8?tracks=English,Espanol ...
        //The URL should be in the format bunny:videoId=ebd54a8-3491-4e20-bec5-96b31c31636a&zoneId=vz-eb594b94-203&tracks=English,Espanol
        String prefix = "bunny:";

        if (mediaSource.startsWith(prefix)) {
            String query = mediaSource.substring(prefix.length());

            Map<String, String> paramMap = new HashMap<>();
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf('=');
                if (idx > 0 && idx < pair.length() - 1) {
                    String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                    paramMap.put(key, value);
                }
            }

            videoId = paramMap.get("videoId");
            clientId = paramMap.get("zoneId");
            tracksParam = paramMap.get("tracks");
            playerType = BUNNY_PLAYER;
        }

        return acceptMedia(mediaSource, mediaMetadata,
            prefix);
    }

    @Override
    protected void queryParamToStartOption(String name, String value, StartOptionsBuilder sob) {
        switch (name) {
            case "autoplay": sob.setAutoplay(Booleans.isTrue(value)); break;
            case "muted": sob.setMuted(Booleans.isTrue(value)); break;
            case "playerColor":
                if (value != null) {
                    try {
                        sob.setPlayerColor(Color.web(value));
                    } catch (Exception ignored) {}
                }
                break;
        }
    }

    @Override
    protected String generateMediaEmbedRawUrl() {
        //return Resource.toUrl("videojs.html?player_type="+playerType+"&client_id="+clientId+"&hls_id="+videoId, getClass());
        return Resource.toUrl("videojs.html", getClass());
    }


    @Override
    protected void appendUrlParameters(StartOptions so, StringBuilder sb) {
        if (Booleans.isTrue(so.autoplay())) {
            sb.append("&autoplay=true");
        }

//        if (playerType != null) {
//            sb.append("&player_type=").append(playerType);
//        }
//
//       if (clientId != null) {
//           sb.append("&client=").append(clientId);
//        }
//
//        if (videoId != null) {
//           sb.append("&hls_id=").append(videoId);
//        }
////
//        String tracks = so.getCustomProperty("tracks");
//        if (tracks != null) {
//            sb.append("&tracks=").append(tracks);
//        }

    }

    private void seamless_call(String script) {
        StartOptions so = playingStartingOption;
        boolean autoplay = so != null && Booleans.isTrue(so.autoplay());
        boolean muted = so != null && Booleans.isTrue(so.muted());

        webViewPane.loadFromScript(
            "const playerId = '" + playerId + "';\n" +
                "var player = window.webfx_extras_videojs_players[playerId];\n" +
                "if (player && !player.isDisposed()) {\n" +
                "    " + script + ";\n" +
                "} else {\n" +
                "    var createPlayer = function() {\n" +
                "        const javaPlayer = window[playerId];\n" +
                "        const config = {\n" +
                "            containerId: playerId,\n" +
                "            playerType: '" + playerType + "',\n" +
                "            hlsId: '" + videoId + "',\n" +
                "            clientId: '" + clientId + "',\n" +
                "            tracksParam: '" + tracksParam + "',\n" +
                "            autoplay: " + autoplay + ",\n" +
                "            muted: " + muted + "\n" +
                "        };\n" +
                "        const player = window.VideoPlayerManager.loadVideo(config);\n" +
                "        window.webfx_extras_videojs_players[playerId] = player;\n" +
                "        window.bindVideoJsPlayer(player, javaPlayer);\n" +
                "        player.ready(function() {\n" +
                "            javaPlayer.onReady();\n" +
                "            " + script + ";\n" +
                "        });\n" +
                "    };\n" +
                "    if (window.VideoPlayerManager) createPlayer(); else window.webfx_extras_videojs_functions.push(createPlayer);\n" +
                "}",

            new LoadOptions()
                .setSeamlessInBrowser(true)
                .setSeamlessContainerId(playerId)
                .setOnWebWindowReady(() -> webViewPane.setWindowMember(playerId, this)),
            null
        );

    }

    @Override
    protected void seamless_displayVideo() {
        seamless_call("");
    }

    @Override
    public void resetToInitialState() {
        if (IS_SEAMLESS) {
            seamless_call("player.reset();");
        } else {
            super.resetToInitialState();
        }
    }

    @Override
    protected void seamless_play() {
        seamless_call("player.play()");
    }

    @Override
    protected void seamless_pause() {
        seamless_call("player.pause()");
    }

    @Override
    protected void seamless_stop() {
        if (IS_SEAMLESS) {
            seamless_call("player.pause(); player.currentTime(0);");
            setStatus(Status.STOPPED);
        }
    }

    @Override
    public void seek(Duration seekTime) {
        if (IS_SEAMLESS)
            seamless_call("player.currentTime(" + seekTime.toSeconds() + ")");
    }


    @Override
    protected void seamless_requestFullscreen() {
        seamless_call("player.requestFullscreen()");
    }

    @Override
    protected void seamless_cancelFullscreen() {
        seamless_call("player.exitFullscreen()");
    }
}