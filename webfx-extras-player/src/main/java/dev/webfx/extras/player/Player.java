package dev.webfx.extras.player;

import dev.webfx.platform.util.collection.Collections;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.util.Duration;

/**
 * @author Bruno Salmon
 */
public interface Player {

    ObservableList<String> getPlaylist();


    /**
     * Specifies the media file to be played at the specified index in the
     * initial playlist. The current media file will be stopped and the media
     * file that is located at the provided index will start playing.
     *
     * @param index The index from the playlist that will start playing
     */
    default void setCurrentIndex(int index) {
        currentIndexProperty().set(index);
    }

    /**
     * Integer property that indicates the current index on the playlist,
     * starting from 0.
     *
     * @return an {@link IntegerProperty} indicating the current index on the playlist.
     */
    IntegerProperty currentIndexProperty();

    default int getCurrentIndex() {
        return currentIndexProperty().get();
    }

    default String getCurrentTrack() {
        int currentIndex = getCurrentIndex();
        return Collections.get(getPlaylist(), currentIndex);
    }

    void play();

    void pause();

    void stop();

    void seek(Duration seekTime);

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

    boolean supportsEventHandlers();

    void setOnEndOfPlaying(Runnable onEndOfPlaying);

}
