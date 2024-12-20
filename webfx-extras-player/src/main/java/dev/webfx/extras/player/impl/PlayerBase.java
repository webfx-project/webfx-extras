package dev.webfx.extras.player.impl;

import dev.webfx.extras.player.*;
import dev.webfx.extras.player.multi.MultiPlayer;
import dev.webfx.kit.util.properties.FXProperties;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Bruno Salmon
 */
public abstract class PlayerBase implements Player {

    private final ObjectProperty<PlayerGroup> playerGroupProperty = FXProperties.newObjectProperty(Players.getGlobalPlayerGroup(), this::onGroupChange);
    private final ObjectProperty<StartOptions> startOptionsProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<Media> mediaProperty = FXProperties.newObjectProperty(this::onMediaChange);
    private final ObjectProperty<Duration> currentTimeProperty = new SimpleObjectProperty<>(Duration.ZERO);
    private final ObjectProperty<Status> statusProperty = FXProperties.newObjectProperty(Status.NO_MEDIA, this::onStatusChange);
    private final BooleanProperty mutedProperty = FXProperties.newBooleanProperty(this::onMutedChange);
    private final BooleanProperty fullscreenProperty = FXProperties.newBooleanProperty(this::onFullscreenChange);
    protected StartOptions playingStartingOption;
    protected boolean isInMultiPlayer;

    protected Runnable onEndOfPlaying;

    @Override
    public ObjectProperty<PlayerGroup> playerGroupProperty() {
        return playerGroupProperty;
    }

    @Override
    public ObjectProperty<StartOptions> startOptionsProperty() {
        return startOptionsProperty;
    }

    @Override
    public ObjectProperty<Media> mediaProperty() {
        return mediaProperty;
    }

    protected String getMediaId() {
        Media media = getMedia();
        return media == null ? null : media.getId();
    }

    public ReadOnlyObjectProperty<Status> statusProperty() {
        return statusProperty;
    }

    protected void setStatus(Status status) {
        statusProperty.set(status);
    }

    public ReadOnlyObjectProperty<Duration> currentTimeProperty() {
        return currentTimeProperty;
    }

    @Override
    public ReadOnlyBooleanProperty mutedProperty() {
        return mutedProperty;
    }

    @Override
    public ReadOnlyBooleanProperty fullscreenProperty() {
        return fullscreenProperty;
    }

    protected void setFullscreen(boolean fullscreen) {
        fullscreenProperty.set(fullscreen);
    }

    protected void onGroupChange() {
        // TODO: also notify the previous group (not accessible here at this point for now)
        notifyPlayerGroup();
    }

    protected void onMediaChange() {
        Media media = getMedia();
        if (media != null) {
            FXProperties.setIfNotBound(statusProperty, Status.LOADING);
            currentTimeProperty.bind(media.currentTimeProperty());
        } else {
            FXProperties.setEvenIfBound(statusProperty, Status.NO_MEDIA);
            FXProperties.setEvenIfBound(currentTimeProperty, Duration.ZERO);
        }
    }

    protected void onStatusChange() {
        notifyPlayerGroup();
    }

    protected void onMutedChange() {
        notifyPlayerGroup();
    }

    protected void onFullscreenChange() {
    }

    protected void notifyPlayerGroup() {
        // Notifying associated player group
        PlayerGroup playerGroup = getPlayerGroup();
        if (playerGroup != null)
            playerGroup.onPlayerStateChange(this);
        // Notifying global player group (if different)
        PlayerGroup globalPlayerGroup = Players.getGlobalPlayerGroup();
        if (globalPlayerGroup != playerGroup)
            globalPlayerGroup.onPlayerStateChange(this);
    }

    public void setOnEndOfPlaying(Runnable onEndOfPlaying) {
        this.onEndOfPlaying = onEndOfPlaying;
    }

    protected void callOnEndOfPlayingIfSet() {
        if (onEndOfPlaying != null)
            onEndOfPlaying.run();
    }

    public void setInMultiPlayer(boolean inMultiPlayer) {
        isInMultiPlayer = inMultiPlayer;
    }

    public void bindPlayerToMultiPlayer(MultiPlayer multiPlayer) {
        playerGroupProperty.bind(multiPlayer.playerGroupProperty());
        startOptionsProperty.bind(multiPlayer.startOptionsProperty());
    }

    public void unbindPlayerFromMultiPlayer() {
        playerGroupProperty.unbind();
        startOptionsProperty.unbind();
    }

    protected void bindMultiPlayerToPlayer(Player player) {
        currentTimeProperty.bind(player.currentTimeProperty());
        statusProperty.bind(player.statusProperty());
        bindMutedPropertyTo(player.mutedProperty());
        fullscreenProperty.bind(player.fullscreenProperty());
    }

    protected void unbindMultiPlayerFromPlayer() {
        currentTimeProperty.unbind();
        statusProperty.unbind();
        mutedProperty.unbind();
        fullscreenProperty.unbind();
    }

    protected void bindMutedPropertyTo(ObservableValue<? extends Boolean> otherMutedProperty) {
        mutedProperty.bind(otherMutedProperty);
    }

    protected void updatePlayingStartingOption(boolean forceAutoplay) {
        StartOptionsBuilder sob = new StartOptionsBuilder()
            .applyStartOptions(getMedia().getSourceStartOptions())
            .applyStartOptions(getStartOptions());
        if (forceAutoplay)
            sob.setAutoplay(true);
        Color globalPlayerColor = Players.getGlobalPlayerColor();
        if (globalPlayerColor != null)
            sob.setPlayerColor(globalPlayerColor);
        playingStartingOption = sob.build();
    }

    protected String matchPrefix(String source, String... possiblePrefixes) {
        for (String prefix : possiblePrefixes) {
            // Convert '*' to '.*' for regex, and '^' to anchor it to the beginning
            String regex = "^" + prefix.replace("*", ".*");

            // Compile the regex pattern
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(source);

            // Check if the source starts with the prefix (match must start at index 0)
            if (matcher.find() && matcher.start() == 0) {
                // If it matches, return the substring after the matched prefix
                return source.substring(matcher.end());
            }
        }
        return null; // No match found
    }

}
