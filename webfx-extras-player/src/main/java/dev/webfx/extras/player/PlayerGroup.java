package dev.webfx.extras.player;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

/**
 * @author Bruno Salmon
 */
public interface PlayerGroup {

    ReadOnlyObjectProperty<Player> playingPlayerProperty();

    default Player getPlayingPlayer() {
        return playingPlayerProperty().get();
    }

    BooleanProperty keepOnlyPlayingAudioPlayerProperty();

    default boolean isKeepOnlyPlayingAudioPlayer() {
        return keepOnlyPlayingAudioPlayerProperty().get();
    }

    default void setKeepOnlyPlayingAudioPlayer(boolean audioOnly) {
        keepOnlyPlayingAudioPlayerProperty().set(audioOnly);
    }

    /**
     * Player implementations should call this method ideally each time the player state changes, or at least when these
     * changes are happening:
     * - on starting/resuming playing or stopping/pausing
     * - on being muted or unmuted
     *
     * @param player the player whose state just changed
     */
    void onPlayerStateChange(Player player);

}
