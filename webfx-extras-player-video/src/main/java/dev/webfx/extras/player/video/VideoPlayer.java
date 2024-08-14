package dev.webfx.extras.player.video;

import dev.webfx.extras.player.Player;
import javafx.scene.Node;


/**
 * @author Bruno Salmon
 */
public interface VideoPlayer extends Player {

    IntegrationMode getIntegrationMode();

    Node getVideoView();

    default boolean supportsFullscreen() {
        return false;
    }

    default void requestFullscreen() { }

}
