package dev.webfx.extras.player.video.youtube;

import dev.webfx.extras.player.video.impl.WebEmbedVideoPlayerBase;

/**
 * @author Bruno Salmon
 */
public class YoutubeVideoPlayer extends WebEmbedVideoPlayerBase {

    @Override
    protected String trackUrl(String track) {
        return "https://www.youtube.com/embed/" + track + "?rel=0";
    }
}
