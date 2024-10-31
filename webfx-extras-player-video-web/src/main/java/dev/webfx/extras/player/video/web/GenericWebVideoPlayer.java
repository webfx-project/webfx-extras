package dev.webfx.extras.player.video.web;

/**
 * @author Bruno Salmon
 */
public final class GenericWebVideoPlayer extends WebVideoPlayerBase {

    @Override
    protected String trackUrl(String track, boolean play) {
        return track;
    }
}
