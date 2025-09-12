package dev.webfx.extras.player.audio.javafxmedia;

import dev.webfx.extras.media.metadata.MediaMetadata;
import dev.webfx.extras.panes.MonoPane;
import dev.webfx.extras.player.FeatureSupport;
import dev.webfx.extras.player.Status;
import dev.webfx.extras.player.impl.PlayerBase;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.kit.util.properties.Unregisterable;
import javafx.scene.Node;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * @author Bruno Salmon
 */
public class JavaFXMediaAudioPlayer extends PlayerBase {

    private MediaPlayer mediaPlayer;
    private Unregisterable mediaPlayerBinding; // will allow to unbind a recycled view from its previous associated media player.
    private AudioMediaView audioMediaView;

    @Override
    public dev.webfx.extras.player.Media acceptMedia(String mediaSource, MediaMetadata mediaMetadata) {
        return new JavaFXMedia(mediaSource, mediaMetadata, this); // TODO catch possible exceptions + anticipate unsupported audio formats
    }

    @Override
    public Node getMediaView() {
        if (audioMediaView == null)
            audioMediaView = new AudioMediaView(this);
        return audioMediaView.getContainer();
    }

    @Override
    public MonoPane getMediaViewOverlay() {
        return null; // Not used so far. TODO: Implement if needed.
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

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
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
        MediaPlayer mPlayer = mediaPlayer;
        if (mediaPlayer != null) {
            mediaPlayer = null;
            if (mediaPlayerBinding != null) {
                mediaPlayerBinding.unregister();
            }
        }
        super.onMediaChange(); // will bind player currentTimeProperty to this media currentTimeProperty
        if (mPlayer != null)
            mPlayer.dispose();
    }

    private void checkCreateMediaPlayer() {
        if (mediaPlayer == null) {
            dev.webfx.extras.player.Media media = getMedia();
            if (media instanceof JavaFXMedia) {
                JavaFXMedia javaFxMedia = (JavaFXMedia) media;
                Media actualJavaFxMedia = javaFxMedia.getFxMedia();
                mediaPlayer = new MediaPlayer(actualJavaFxMedia);
                mediaPlayer.setOnEndOfMedia(onEndOfPlaying);
                mediaPlayerBinding = FXProperties.runNowAndOnPropertyChange(status ->
                        setStatus(convertMediaStatus(status))
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
