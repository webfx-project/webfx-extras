package dev.webfx.extras.player.video;

import dev.webfx.extras.player.Player;
import javafx.scene.Node;


/**
 * @author Bruno Salmon
 */
public interface VideoPlayer extends Player {

    IntegrationMode getIntegrationMode();

    Node getVideoView();

    void displayVideo();

    default boolean supportsFullscreen() {
        return false;
    }

    default void requestFullscreen() { }

    default boolean isFullscreen() {
        return false;
    }

    default void cancelFullscreen() { }

}
