package dev.webfx.extras.player.audio.javafxmedia;

import dev.webfx.extras.player.FeatureSupport;
import dev.webfx.extras.player.MediaMetadata;
import dev.webfx.extras.player.Status;
import dev.webfx.extras.player.impl.PlayerBase;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.kit.util.properties.Unregisterable;
import javafx.scene.Node;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bruno Salmon
 */
public class JavaFXMediaAudioPlayer extends PlayerBase {

    // will hold the player currently playing (only one player can be playing at one time)
    // Keeping all media players in memory (even if paused) to hold their states (ex: current time). There shouldn't be
    // that many because we create the media players only when the user actually presses the podcast play button.
    private static final Map<String /* source */, MediaPlayer> INSTANTIATED_PLAYERS = new /*Weak*/HashMap<>();

    private MediaPlayer mediaPlayer;
    private Unregisterable mediaPlayerBinding; // will allow to unbind a recycled view from its previous associated media player.

    @Override
    public dev.webfx.extras.player.Media acceptMedia(String mediaSource, MediaMetadata mediaMetadata) {
        return new JavaFXMedia(mediaSource, mediaMetadata, this); // TODO catch possible exceptions + anticipate unsupported audio formats
    }

    @Override
    public Node getMediaView() {
        return null;
    }

    @Override
    public FeatureSupport getFullscreenSupport() {
        return FeatureSupport.NO_SUPPORT;
    }

    @Override
    public FeatureSupport getNavigationSupport() {
        return FeatureSupport.FULL_SUPPORT; // Should we say something different since user controls are not available (no view)?
    }

    @Override
    public void play() {
        checkCreateMediaPlayer();
        if (mediaPlayer != null)
            mediaPlayer.play();
    }

    @Override
    public void pause() {
        checkCreateMediaPlayer();
        if (mediaPlayer != null)
            mediaPlayer.pause();
    }

    @Override
    public void stop() {
        checkCreateMediaPlayer();
        if (mediaPlayer != null)
            mediaPlayer.stop();
    }

    @Override
    public void seek(Duration seekTime) {
        checkCreateMediaPlayer();
        if (mediaPlayer != null)
            mediaPlayer.seek(seekTime);
    }

    @Override
    public FeatureSupport getMuteSupport() {
        return FeatureSupport.FULL_SUPPORT;
    }

    @Override
    public void mute() {
        checkCreateMediaPlayer();
        if (mediaPlayer != null)
            mediaPlayer.setMute(true);
    }

    @Override
    public void unmute() {
        checkCreateMediaPlayer();
        if (mediaPlayer != null)
            mediaPlayer.setMute(false);
    }

    @Override
    protected void onMediaChange() {
        if (mediaPlayer != null) {
            mediaPlayer.dispose();
            mediaPlayer = null;
            if (mediaPlayerBinding != null)
                mediaPlayerBinding.unregister();
        }
        super.onMediaChange(); // will bind player currentTimeProperty to this media currentTimeProperty
    }

    private void checkCreateMediaPlayer() {
        if (mediaPlayer == null) {
            dev.webfx.extras.player.Media media = getMedia();
            if (media instanceof JavaFXMedia) {
                JavaFXMedia javaFxMedia = (JavaFXMedia) media;
                Media actualJavaFxMedia = javaFxMedia.getFxMedia();
                String source = actualJavaFxMedia.getSource();
                mediaPlayer = INSTANTIATED_PLAYERS.get(source);
                if (mediaPlayer == null) {
                    INSTANTIATED_PLAYERS.put(source, mediaPlayer = new MediaPlayer(actualJavaFxMedia));
                }
                mediaPlayer.setOnEndOfMedia(onEndOfPlaying);
                mediaPlayerBinding = FXProperties.runNowAndOnPropertiesChange(() ->
                        setStatus(convertMediaStatus(mediaPlayer.getStatus()))
                    , mediaPlayer.statusProperty());
                javaFxMedia.currentTimePropertyImpl().bind(mediaPlayer.currentTimeProperty());
                bindMutedPropertyTo(mediaPlayer.muteProperty());
            }
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
            case DISPOSED:
            case HALTED:   return Status.HALTED;
            case UNKNOWN:
            default:       return Status.LOADING;
        }
    }

    @Override
    public void setOnEndOfPlaying(Runnable onEndOfPlaying) {
        super.setOnEndOfPlaying(onEndOfPlaying);
        if (mediaPlayer != null)
            mediaPlayer.setOnEndOfMedia(onEndOfPlaying);
    }
}
