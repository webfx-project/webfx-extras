package dev.webfx.extras.media.metadata;

/**
 * @author Bruno Salmon
 */
public interface HasHasAudio extends MediaMetadata {

    default Boolean hasAudio() {
        return true;
    }

}
