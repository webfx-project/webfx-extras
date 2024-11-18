package dev.webfx.extras.player;

import dev.webfx.extras.media.metadata.MediaMetadata;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.util.Duration;

/**
 * @author Bruno Salmon
 */
public interface Media {

    String getSource();

    String getId();

    StartOptions getSourceStartOptions();

    MediaMetadata getMetadata();

    Player getPlayer();

    ReadOnlyObjectProperty<Duration> durationProperty();

    default Duration getDuration() {
        return durationProperty().getValue();
    }

    ReadOnlyObjectProperty<Duration> currentTimeProperty();

    default Duration getCurrentTime() {
        return currentTimeProperty().getValue();
    }

}
