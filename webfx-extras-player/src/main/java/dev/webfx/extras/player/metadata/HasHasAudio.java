package dev.webfx.extras.player.metadata;

import dev.webfx.extras.player.MediaMetadata;

/**
 * @author Bruno Salmon
 */
public interface HasHasAudio extends MediaMetadata {

    default Boolean hasAudio() {
        return true;
    }

}
