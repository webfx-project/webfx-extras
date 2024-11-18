package dev.webfx.extras.player.metadata;

import dev.webfx.extras.player.MediaMetadata;
import javafx.util.Duration;

/**
 * @author Bruno Salmon
 */
public interface HasDuration extends MediaMetadata {

    Duration getDuration();

}
