package dev.webfx.extras.player.impl;

import dev.webfx.extras.player.Player;
import dev.webfx.extras.player.PlayerGroup;
import dev.webfx.extras.player.Players;
import dev.webfx.extras.player.multi.MultiPlayer;
import dev.webfx.platform.console.Console;
import dev.webfx.platform.util.collection.Collections;
import javafx.beans.property.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bruno Salmon
 */
public final class PlayerGroupImpl implements PlayerGroup {

    private static final boolean DEBUG = false;

    private final BooleanProperty keepsOnlySoundPlayingPlayerProperty = new SimpleBooleanProperty();
    private final ObjectProperty<Player> playerPlayerProperty = new SimpleObjectProperty<>();
    // List of players we are not sure if they are playing or not (because they don't support notification)
    private final List<Player> maybePlayingPlayers = new ArrayList<>();

    public PlayerGroupImpl(boolean keepsOnlySoundPlayingPlayer) {
        setKeepOnlyPlayingAudioPlayer(keepsOnlySoundPlayingPlayer);
    }

    @Override
    public ReadOnlyObjectProperty<Player> playingPlayerProperty() {
        return playerPlayerProperty;
    }

    private void setPlayingPlayer(Player player) {
        playerPlayerProperty.set(player);
    }

    private void setNoPlayingPlayer() {
        setPlayingPlayer(null);
    }

    @Override
    public BooleanProperty keepOnlyPlayingAudioPlayerProperty() {
        return keepsOnlySoundPlayingPlayerProperty;
    }

    @Override
    public void onPlayerStateChange(Player player) {
        if (player instanceof MultiPlayer)
            return;
        logDebug("onPlayerStateChange() called, status = " + player.getStatus() + ", player = " + player);
        boolean belongsToThisGroup = player.getPlayerGroup() == this;
        Player currentPlayingPlayer = getPlayingPlayer();
        if (!belongsToThisGroup) {
            maybePlayingPlayers.remove(player);
            if (player == currentPlayingPlayer) // looks like the playing player just left the group
                setNoPlayingPlayer();
        } else if (!isKeepOnlyPlayingAudioPlayer() || player.hasMediaAudio()) {
            boolean supportsNotification = player.getNavigationSupport().notification();
            if (supportsNotification) {
                boolean surelyPlaying = player.isPlaying();
                if (!surelyPlaying) {
                    if (currentPlayingPlayer == player)
                        setNoPlayingPlayer();
                } else {
                    // pausing previous playing player if present and different from this one
                    if (currentPlayingPlayer != null && currentPlayingPlayer != player) {
                        currentPlayingPlayer.pause();
                        maybePlayingPlayers.remove(currentPlayingPlayer); // just in case it was a maybe-playing player, so
                        // we don't call pause() twice, as we will pause all maybe-playing players below.
                    }
                    setPlayingPlayer(player);
                    maybePlayingPlayers.forEach(Player::pause); // should call back for confirming pause state, will be removed at that time
                }
            } else { // player doesn't support notification
                boolean maybePlaying = Players.isMaybePlaying(player);
                if (!maybePlaying) {
                    maybePlayingPlayers.remove(player);
                    if (currentPlayingPlayer == player) {
                        setNoPlayingPlayer();
                    }
                } else {
                    Collections.addIfNotContains(player, maybePlayingPlayers);
                    // If this is the only maybe-playing player, we take it as new current playing player
                    setPlayingPlayer(maybePlayingPlayers.size() == 1 ? player : null);
                    // Stopping possible previous surely-playing player
                    if (currentPlayingPlayer != null && !maybePlayingPlayers.contains(currentPlayingPlayer)) {
                        currentPlayingPlayer.pause();
                    }
                }
            }
        }
        logDebug("playingPlayer = " + getPlayingPlayer() + ", maybePlayingPlayers = " + maybePlayingPlayers);
    }

    private static void logDebug(String message) {
        if (DEBUG)
            Console.log("👉👉👉👉👉 " + message);
    }
}
