package dev.webfx.extras.player.video;

import dev.webfx.extras.player.Player;


/**
 * @author Bruno Salmon
 */
public interface VideoPlayer extends Player {

    @Override
    default boolean isMediaVideo() {
        return true;
    }
}
