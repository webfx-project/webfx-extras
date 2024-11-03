package dev.webfx.extras.player.audio.javafxmedia;

import dev.webfx.extras.player.MediaMetadata;
import dev.webfx.extras.player.Player;
import dev.webfx.extras.player.impl.MediaBase;
import javafx.beans.property.ObjectProperty;
import javafx.scene.media.Media;
import javafx.util.Duration;

/**
 * @author Bruno Salmon
 */
final class JavaFXMedia extends MediaBase {

    private final Media fxMedia;

    JavaFXMedia(String token, MediaMetadata metaData, Player player) {
        super(token, null, null, metaData, player);
        fxMedia = new Media(token);
    }

    Media getFxMedia() {
        return fxMedia;
    }

    ObjectProperty<Duration> currentTimePropertyImpl() {
        return currentTimeProperty;
    }
}
