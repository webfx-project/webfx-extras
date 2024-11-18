package dev.webfx.extras.player.impl;

import dev.webfx.extras.player.Media;
import dev.webfx.extras.media.metadata.MediaMetadata;
import dev.webfx.extras.player.Player;
import dev.webfx.extras.player.StartOptions;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Duration;

/**
 * @author Bruno Salmon
 */
public class MediaBase implements Media {

    private final String token;
    private final String id;
    private final StartOptions startOptions;
    private final MediaMetadata metaData;
    private final Player player;

    protected final ObjectProperty<Duration> durationProperty = new SimpleObjectProperty<>();
    protected final ObjectProperty<Duration> currentTimeProperty = new SimpleObjectProperty<>();

    public MediaBase(String token, String id, StartOptions startOptions, MediaMetadata metaData, Player player) {
        this.token = token;
        this.id = id;
        this.startOptions = startOptions;
        this.metaData = metaData;
        this.player = player;
    }

    @Override
    public String getSource() {
        return token;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public StartOptions getSourceStartOptions() {
        return startOptions;
    }

    @Override
    public MediaMetadata getMetadata() {
        return metaData;
    }

    @Override
    public ReadOnlyObjectProperty<Duration> durationProperty() {
        return durationProperty;
    }

    @Override
    public ReadOnlyObjectProperty<Duration> currentTimeProperty() {
        return currentTimeProperty;
    }

    @Override
    public Player getPlayer() {
        return player;
    }
}
