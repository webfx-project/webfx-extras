package dev.webfx.extras.player.video.web.videojs;

import dev.webfx.extras.media.metadata.MediaMetadata;
import dev.webfx.extras.player.Media;
import dev.webfx.extras.player.StartOptions;
import dev.webfx.extras.player.StartOptionsBuilder;
import dev.webfx.extras.player.video.web.SeamlessCapableWebVideoPlayer;

/**
 * @author David Hello
 */
public final class VideoJsPlayer extends SeamlessCapableWebVideoPlayer {

    @Override
    protected void queryParamToStartOption(String name, String value, StartOptionsBuilder sob) {

    }

    @Override
    protected String generateMediaEmbedRawUrl() {
        return "";
    }

    @Override
    protected void appendUrlParameters(StartOptions so, StringBuilder sb) {

    }

    @Override
    public Media acceptMedia(String mediaSource, MediaMetadata mediaMetadata) {
        return null;
    }

    @Override
    protected void seamless_displayVideo() {

    }

    @Override
    protected void seamless_play() {

    }

    @Override
    protected void seamless_pause() {

    }

    @Override
    protected void seamless_stop() {

    }

    @Override
    protected void seamless_requestFullscreen() {

    }

    @Override
    protected void seamless_cancelFullscreen() {

    }
}
