package dev.webfx.extras.player.video.web.youtube;

import dev.webfx.extras.player.video.web.WebVideoPlayerBase;

/**
 * @author Bruno Salmon
 */
public class YoutubeVideoPlayer extends WebVideoPlayerBase {

    @Override
    protected String trackUrl(String track) {
        return "https://www.youtube.com/embed/" + track + "?rel=0";
    }
}
