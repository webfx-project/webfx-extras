package dev.webfx.extras.player.video.web;

import dev.webfx.extras.player.*;

/**
 * @author Bruno Salmon
 */
public final class GenericWebVideoPlayer extends WebVideoPlayerBase {

    public GenericWebVideoPlayer() {
    }

    public GenericWebVideoPlayer(IntegrationMode integrationMode) {
        super(integrationMode);
    }

    @Override
    public Media acceptMedia(String mediaSource, MediaMetadata mediaMetadata) {
        if (!mediaSource.startsWith("http"))
            return null;
        return acceptMedia(mediaSource, mediaMetadata, "");
    }

    @Override
    protected void queryParamToStartOption(String name, String value, StartOptionsBuilder sob) {
    }

    @Override
    protected String generateMediaEmbedRawUrl() {
        return getMedia().getSource();
    }

    @Override
    protected void appendUrlParameters(StartOptions so, StringBuilder sb) {
    }
}
