package dev.webfx.extras.player;

import dev.webfx.extras.media.metadata.MediaMetadata;
import dev.webfx.extras.media.metadata.MetadataUtil;
import dev.webfx.kit.util.properties.FXProperties;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * @author Bruno Salmon
 */
public interface Player {

    default Media acceptMedia(String mediaSource) {
        return acceptMedia(mediaSource, null);
    }

    Media acceptMedia(String mediaSource, MediaMetadata mediaMetadata);

    ObjectProperty<PlayerGroup> playerGroupProperty();

    default PlayerGroup getPlayerGroup() {
        return playerGroupProperty().get();
    }

    default void setPlayerGroup(PlayerGroup playerGroup) {
        playerGroupProperty().set(playerGroup);
    }

    ObjectProperty<StartOptions> startOptionsProperty();

    default StartOptions getStartOptions() {
        return startOptionsProperty().get();
    }

    default void setStartOptions(StartOptions startOptions) {
        startOptionsProperty().set(startOptions);
    }

    ObjectProperty<Media> mediaProperty();

    default Media getMedia() {
        return mediaProperty().get();
    }

    default void setMedia(Media media) {
        setMedia(media, getStartOptions());
    }

    default void setMedia(Media media, StartOptions startOptions) {
        if (startOptions == null && media != null)
            startOptions = media.getSourceStartOptions();
        FXProperties.setIfNotBound(startOptionsProperty(), startOptions);
        //setStartOptions(startOptions);
        mediaProperty().set(media);
    }

    Node getMediaView();

    Pane getMediaViewOverlay();

    default boolean hasMediaAudio() {
        Media media = getMedia();
        if (media == null)
            return false;
        Boolean hasAudio = MetadataUtil.hasAudio(media.getMetadata());
        if (hasAudio != null)
            return hasAudio;
        return true; // if no info is provided, we assume the media has audio by default
    }

    default boolean isMediaVideo() {
        return false;
    }

    default void displayVideo() { }

    default IntegrationMode getIntegrationMode() {
        return IntegrationMode.SEAMLESS;
    }

    FeatureSupport getNavigationSupport();

    void play();

    void pause();

    void stop();

    void seek(Duration seekTime);

    default void resetToInitialState() {
        seek(Duration.ZERO);
    }

    default Duration getCurrentTime() {
        return currentTimeProperty().getValue();
    }

    ReadOnlyObjectProperty<Duration> currentTimeProperty();

    ReadOnlyObjectProperty<Status> statusProperty();

    default Status getStatus() {
        return statusProperty().getValue();
    }

    default boolean isPlaying() {
        return getStatus() == Status.PLAYING;
    }

    default boolean isPlayingAudio() {
        return isPlaying() && !isMuted() && hasMediaAudio();
    }

    FeatureSupport getMuteSupport();

    default void mute() { }

    default void unmute() { }

    ReadOnlyBooleanProperty mutedProperty();

    default boolean isMuted() {
        return mutedProperty().get();
    }

    FeatureSupport getFullscreenSupport();

    ReadOnlyBooleanProperty fullscreenProperty();

    default boolean isFullscreen() {
        return fullscreenProperty().get();
    }

    default void requestFullscreen() { }

    default void cancelFullscreen() { }

    void setOnEndOfPlaying(Runnable onEndOfPlaying);

}
