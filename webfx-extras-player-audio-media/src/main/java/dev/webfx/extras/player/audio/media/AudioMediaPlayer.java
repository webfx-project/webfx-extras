package dev.webfx.extras.player.audio.media;

import dev.webfx.extras.player.Status;
import dev.webfx.extras.player.impl.PlayerBase;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.kit.util.properties.Unregisterable;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.Objects;

/**
 * @author Bruno Salmon
 */
public class AudioMediaPlayer extends PlayerBase {

    private MediaPlayer mediaPlayer;
    private Unregisterable mediaPlayerBinding; // will allow to unbind a recycled view from its previous associated media player.

    @Override
    public void play() {
        createMediaPlayer();
        if (mediaPlayer != null)
            mediaPlayer.play();
    }

    @Override
    public void pause() {
        if (mediaPlayer != null)
            mediaPlayer.pause();
    }

    @Override
    public void stop() {
        if (mediaPlayer != null)
            mediaPlayer.stop();
    }

    @Override
    public void seek(Duration seekTime) {
        if (mediaPlayer != null)
            mediaPlayer.seek(seekTime);
    }

    @Override
    public boolean supportsEventHandlers() {
        return true;
    }

    private void createMediaPlayer() {
        String audioUrl = getCurrentTrack();
        if (mediaPlayer == null || !Objects.equals(audioUrl, mediaPlayer.getMedia().getSource())) {
            mediaPlayer = new MediaPlayer(new Media(audioUrl));
            mediaPlayer.setOnEndOfMedia(onEndOfPlaying);
            if (mediaPlayerBinding != null)
                mediaPlayerBinding.unregister();
            mediaPlayerBinding = FXProperties.runNowAndOnPropertiesChange(() ->
                setStatus(convertMediaStatus(mediaPlayer.getStatus()))
            , mediaPlayer.statusProperty());
            currentTimeProperty.bind(mediaPlayer.currentTimeProperty());
        }
    }

    private Status convertMediaStatus(MediaPlayer.Status status) {
        if (status == null)
            return null;
        switch (status) {
            case READY:    return Status.READY;
            case PAUSED:   return Status.PAUSED;
            case PLAYING:  return Status.PLAYING;
            case STOPPED:  return Status.STOPPED;
            case STALLED:  return Status.STALLED;
            case HALTED:   return Status.HALTED;
            case DISPOSED: return Status.DISPOSED;
            case UNKNOWN:
            default:       return Status.UNKNOWN;
        }
    }

    @Override
    public void setOnEndOfPlaying(Runnable onEndOfPlaying) {
        super.setOnEndOfPlaying(onEndOfPlaying);
        if (mediaPlayer != null)
            mediaPlayer.setOnEndOfMedia(onEndOfPlaying);
    }
}
