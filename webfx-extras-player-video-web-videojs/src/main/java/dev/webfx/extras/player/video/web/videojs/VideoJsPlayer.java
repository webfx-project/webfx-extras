package dev.webfx.extras.player.video.web.videojs;

import dev.webfx.extras.media.metadata.MediaMetadata;
import dev.webfx.extras.player.Media;
import dev.webfx.extras.player.StartOptions;
import dev.webfx.extras.player.StartOptionsBuilder;
import dev.webfx.extras.player.Status;
import dev.webfx.extras.player.video.web.WebVideoPlayerBase;
import dev.webfx.extras.webview.pane.LoadOptions;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.platform.resource.Resource;
import dev.webfx.platform.util.Booleans;
import javafx.scene.paint.Color;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author David Hello
 */
public final class VideoJsPlayer extends WebVideoPlayerBase {

    private String playerType = "";
    private static final String BUNNY_PLAYER="bunny";
    private String clientId = "";
    private String videoId = "";

    public VideoJsPlayer() {
        FXProperties.runOnPropertyChange(status -> {
            if (status == Status.READY) {
                String script = Resource.getText(Resource.toUrl("videojs.js", getClass()));
                script = script.replace("$player$", playerType).replace("$hlsId$", videoId).replace("$clientId$", clientId).replace("$tracksParam$", "French");
                webViewPane.loadFromScript(script, new LoadOptions(), false);
            }
        }, statusProperty());
    }

    @Override
    public Media acceptMedia(String mediaSource, MediaMetadata mediaMetadata) {
        String videoIdAndParams = matchPrefix(mediaSource,"https://*.b-cdn.net/");


        //First case: the video comes from bunny
        Pattern pattern = Pattern.compile("https://([\\w-]+)\\.b-cdn\\.net/([\\w-]+)/");
        Matcher matcher = pattern.matcher(mediaSource);

        if (matcher.find()) {
            clientId = matcher.group(1);
            videoId = matcher.group(2);
            playerType = BUNNY_PLAYER;
        }


        return acceptMedia(mediaSource, mediaMetadata,
            "bunny:",
            "https://*.b-cdn.net/",
            "castr:",
            "https://stream.castr.net/"
        );
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

 //       String playerType = so.getCustomProperty("playerType");
        if (playerType != null) {
//            sb.append("&player_type=").append(playerType);
//        }
//
//        String clientId = so.getCustomProperty("clientId");
//        if (clientId != null) {
//            sb.append("&client_id=").append(clientId);
//        }
//
//        String hlsId = so.getCustomProperty("hlsId");
//        if (hlsId != null) {
//            sb.append("&hls_id=").append(hlsId);
//        }
//
//        String tracks = so.getCustomProperty("tracks");
//        if (tracks != null) {
//            sb.append("&tracks=").append(tracks);
//        }

    }
        }




    private String buildVideoUrl(String mediaSource) {
        if (mediaSource.startsWith("bunny:")) {
            String[] parts = mediaSource.substring(6).split("/");
            if (parts.length >= 2) {
                return "https://" + parts[0] + ".b-cdn.net/" + parts[1] + "/playlist.m3u8";
            }
        } else if (mediaSource.startsWith("castr:")) {
            String[] parts = mediaSource.substring(6).split("/");
            if (parts.length >= 2) {
                return "https://stream.castr.net/" + parts[0] + "/live_" + parts[1] + "/index.m3u8";
            }
        } else if (mediaSource.startsWith("hls:")) {
            return mediaSource.substring(4);
        } else if (mediaSource.endsWith(".m3u8") || mediaSource.contains(".b-cdn.net/") || mediaSource.contains("stream.castr.net/")) {
            return mediaSource;
        }
        return mediaSource;
    }


    private static String toWebColor(Color c) {
        return c == null ? null : toHex2(c.getRed()) + toHex2(c.getGreen()) + toHex2(c.getBlue());
    }

    private static String toHex2(double colorComponent) {
        String s = Integer.toHexString((int) (colorComponent * 255)).toUpperCase();
        return s.length() == 1 ? "0" + s : s;
    }
}