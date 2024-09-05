package dev.webfx.extras.player.video.impl;

import dev.webfx.extras.player.impl.PlayerBase;
import dev.webfx.extras.player.video.IntegrationMode;
import dev.webfx.extras.player.video.VideoPlayer;

/**
 * @author Bruno Salmon
 */
public abstract class VideoPlayerBase extends PlayerBase implements VideoPlayer {

    private static int SEQ;

    protected final String playerId = "player_" + ++SEQ;

    private final IntegrationMode integrationMode;

    public VideoPlayerBase(IntegrationMode integrationMode) {
        this.integrationMode = integrationMode;
    }

    @Override
    public IntegrationMode getIntegrationMode() {
        return integrationMode;
    }
}
