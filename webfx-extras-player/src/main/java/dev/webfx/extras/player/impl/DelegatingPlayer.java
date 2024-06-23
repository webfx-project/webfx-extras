package dev.webfx.extras.player.impl;

import dev.webfx.extras.player.Player;
import dev.webfx.extras.player.Status;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.util.Duration;

/**
 * @author Bruno Salmon
 */
public class DelegatingPlayer implements Player {
    
    protected Player delegate;

    public DelegatingPlayer(Player delegate) {
        this.delegate = delegate;
    }

    @Override
    public ObservableList<String> getPlaylist() {
        return delegate.getPlaylist();
    }

    @Override
    public void setCurrentIndex(int index) {
        delegate.setCurrentIndex(index);
    }

    @Override
    public IntegerProperty currentIndexProperty() {
        return delegate.currentIndexProperty();
    }

    @Override
    public int getCurrentIndex() {
        return delegate.getCurrentIndex();
    }

    @Override
    public String getCurrentTrack() {
        return delegate.getCurrentTrack();
    }

    @Override
    public void play() {
        delegate.play();
    }

    @Override
    public void pause() {
        delegate.pause();
    }

    @Override
    public void stop() {
        delegate.stop();
    }

    @Override
    public void seek(Duration seekTime) {
        delegate.seek(seekTime);
    }

    @Override
    public Duration getCurrentTime() {
        return delegate.getCurrentTime();
    }

    @Override
    public ReadOnlyObjectProperty<Duration> currentTimeProperty() {
        return delegate.currentTimeProperty();
    }

    @Override
    public ReadOnlyObjectProperty<Status> statusProperty() {
        return delegate.statusProperty();
    }

    @Override
    public Status getStatus() {
        return delegate.getStatus();
    }

    @Override
    public boolean isPlaying() {
        return delegate.isPlaying();
    }

    @Override
    public boolean supportsEventHandlers() {
        return delegate.supportsEventHandlers();
    }

    @Override
    public void setOnEndOfPlaying(Runnable onEndOfPlaying) {
        delegate.setOnEndOfPlaying(onEndOfPlaying);
    }
}
