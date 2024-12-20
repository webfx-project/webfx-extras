package dev.webfx.extras.player.multi;

import dev.webfx.extras.player.*;
import dev.webfx.extras.player.impl.PlayerBase;
import dev.webfx.extras.media.metadata.MediaMetadata;
import dev.webfx.platform.util.Arrays;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bruno Salmon
 */
public final class MultiPlayer extends PlayerBase {

    private final List<Player> registeredPlayers = new ArrayList<>();
    private final StackPane multiMediaView = new StackPane();
    private Player selectedPlayer;

    public MultiPlayer() {
    }

    public MultiPlayer(Player... players) {
        Arrays.forEach(players, this::registerPlayer);
    }

    public void registerPlayer(Player player) {
        registeredPlayers.add(player);
        ((PlayerBase) player).setInMultiPlayer(true);
    }

    @Override
    public Media acceptMedia(String mediaSource, MediaMetadata mediaMetadata) {
        for (Player player : registeredPlayers) {
            Media media = player.acceptMedia(mediaSource, mediaMetadata);
            if (media != null) {
                return media;
            }
        }
        return null;
    }

    @Override
    protected void onMediaChange() {
        Media media = getMedia();
        Player oldSelectedPlayer = selectedPlayer;
        selectedPlayer = media == null ? null : media.getPlayer();
        if (selectedPlayer != oldSelectedPlayer) {
            Node selectedMediaView = selectedPlayer == null ? null : selectedPlayer.getMediaView();
            if (selectedMediaView == null)
                multiMediaView.getChildren().clear();
            else
                multiMediaView.getChildren().setAll(selectedMediaView);
            if (selectedPlayer != null) {
                ((PlayerBase) selectedPlayer).bindPlayerToMultiPlayer(this);
                selectedPlayer.setOnEndOfPlaying(onEndOfPlaying);
                selectedPlayer.setMedia(media);
                bindMultiPlayerToPlayer(selectedPlayer);
            } else {
                unbindMultiPlayerFromPlayer();
            }
            if (oldSelectedPlayer != null) {
                ((PlayerBase) selectedPlayer).unbindPlayerFromMultiPlayer();
                oldSelectedPlayer.setOnEndOfPlaying(null);
            }
        }
        //super.onMediaChange();
    }

    @Override
    public Node getMediaView() {
        return multiMediaView;
    }

    @Override
    public FeatureSupport getNavigationSupport() {
        return selectedPlayer == null ? FeatureSupport.NO_SUPPORT : selectedPlayer.getNavigationSupport();
    }

    @Override
    public void play() {
        if (selectedPlayer != null)
            selectedPlayer.play();
    }

    @Override
    public void pause() {
        if (selectedPlayer != null)
            selectedPlayer.pause();
    }

    @Override
    public void stop() {
        if (selectedPlayer != null)
            selectedPlayer.stop();
    }

    @Override
    public void seek(Duration seekTime) {
        if (selectedPlayer != null)
            selectedPlayer.seek(seekTime);
    }

    @Override
    public void resetToInitialState() {
        if (selectedPlayer != null)
            selectedPlayer.resetToInitialState();
    }

    @Override
    public boolean isMediaVideo() {
        return selectedPlayer != null && selectedPlayer.isMediaVideo();
    }

    @Override
    public void displayVideo() {
        if (selectedPlayer != null)
            selectedPlayer.displayVideo();
    }

    @Override
    public IntegrationMode getIntegrationMode() {
        return selectedPlayer == null ? IntegrationMode.SEAMLESS : selectedPlayer.getIntegrationMode();
    }

    @Override
    public FeatureSupport getFullscreenSupport() {
        return selectedPlayer == null ? FeatureSupport.NO_SUPPORT : selectedPlayer.getFullscreenSupport();
    }

    @Override
    public void requestFullscreen() {
        if (selectedPlayer != null)
            selectedPlayer.requestFullscreen();
    }

    @Override
    public void cancelFullscreen() {
        if (selectedPlayer != null)
            selectedPlayer.cancelFullscreen();
    }

    @Override
    public FeatureSupport getMuteSupport() {
        return selectedPlayer == null ? FeatureSupport.NO_SUPPORT : selectedPlayer.getMuteSupport();
    }

    @Override
    public void mute() {
        if (selectedPlayer != null)
            selectedPlayer.mute();
    }

    @Override
    public void unmute() {
        if (selectedPlayer != null)
            selectedPlayer.unmute();
    }

    @Override
    public void setOnEndOfPlaying(Runnable onEndOfPlaying) {
        super.setOnEndOfPlaying(onEndOfPlaying);
        if (selectedPlayer != null)
            selectedPlayer.setOnEndOfPlaying(onEndOfPlaying);
    }

}
