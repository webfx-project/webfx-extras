package dev.webfx.extras.media.metadata;

/**
 * @author Bruno Salmon
 */
public interface HasDurationMillis extends MediaMetadata {

    Long getDurationMillis(); // better to not use javafx Duration to not introduce javafx dependencies on domain models

}
