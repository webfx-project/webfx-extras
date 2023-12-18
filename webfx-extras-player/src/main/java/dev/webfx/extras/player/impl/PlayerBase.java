package dev.webfx.extras.player.impl;

import dev.webfx.extras.player.Player;
import dev.webfx.extras.player.Status;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Duration;

/**
 * @author Bruno Salmon
 */
public abstract class PlayerBase implements Player {

    protected final ObjectProperty<Duration> currentTimeProperty = new SimpleObjectProperty<>(Duration.ZERO);
    private final ObservableList<String> playlist = FXCollections.observableArrayList();

    private final IntegerProperty currentIndexProperty = new SimpleIntegerProperty() {
        @Override
        protected void invalidated() {
            onCurrentIndexChanged();
        }
    };
    private final ObjectProperty<Status> statusProperty = new SimpleObjectProperty<>(Status.UNKNOWN);

    protected Runnable onEndOfPlaying;

    @Override
    public ObservableList<String> getPlaylist() {
        return playlist;
    }

    @Override
    public IntegerProperty currentIndexProperty() {
        return currentIndexProperty;
    }

    protected void onCurrentIndexChanged() {
        if (isPlaying())
            play();
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

    public Duration getCurrentTime() {
        return currentTimeProperty().getValue();
    }

    public Status getStatus() {
        return statusProperty().getValue();
    }

    public boolean isPlaying() {
        return getStatus() == Status.PLAYING;
    }

    public abstract boolean supportsEventHandlers();

    public void setOnEndOfPlaying(Runnable onEndOfPlaying) {
        this.onEndOfPlaying = onEndOfPlaying;
    }

    protected void callOnEndOfPlaying() {
        if (onEndOfPlaying != null)
            onEndOfPlaying.run();
    }

}
