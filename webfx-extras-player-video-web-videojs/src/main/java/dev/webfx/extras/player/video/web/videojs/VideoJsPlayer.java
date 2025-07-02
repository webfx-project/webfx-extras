package dev.webfx.extras.player.video.web.videojs;

import dev.webfx.extras.media.metadata.MediaMetadata;
import dev.webfx.extras.player.Media;
import dev.webfx.extras.player.StartOptions;
import dev.webfx.extras.player.StartOptionsBuilder;
import dev.webfx.extras.player.Status;
import dev.webfx.extras.player.video.web.WebVideoPlayerBase;
import dev.webfx.extras.webview.pane.LoadOptions;
import dev.webfx.extras.webview.pane.WebViewPane;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.platform.resource.Resource;
import dev.webfx.platform.util.Booleans;
import javafx.scene.paint.Color;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author David Hello
 */
public final class VideoJsPlayer extends WebVideoPlayerBase {

    private String playerType = "";
    private static final String BUNNY_PLAYER="bunny";
    private String clientId = "";
    private String videoId = "";
    private String tracksParam = "";

    public VideoJsPlayer() {
        FXProperties.runOnPropertyChange(status -> {
            //Use setRedirectConsole only for debug. It can cause some infinite loop in some cases.
            //webViewPane.setRedirectConsole(true);
            if (status == Status.READY) {
                WebViewPane webViewPane1 = webViewPane;
                javafx.scene.web.WebEngine webEngine = webViewPane1.getWebEngine();
                webViewPane.loadFromScript("safeOnLoad(() => {loadVideo('"+playerType+"', '"+videoId+"', '"+clientId+"','"+ tracksParam+"');});",new LoadOptions(), false);
            }
        }, statusProperty());
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
//           sb.append("&client_id=").append(clientId);
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
}