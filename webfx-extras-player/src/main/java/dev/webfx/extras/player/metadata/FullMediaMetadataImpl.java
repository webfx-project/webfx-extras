package dev.webfx.extras.player.metadata;

import javafx.util.Duration;

/**
 * @author Bruno Salmon
 */
public class FullMediaMetadataImpl implements FullMediaMetadata {

    private final String title;
    private final Duration duration;
    private final Boolean hasAudio;

    public FullMediaMetadataImpl(String title, Duration duration, Boolean hasAudio) {
        this.title = title;
        this.duration = duration;
        this.hasAudio = hasAudio;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    @Override
    public Boolean hasAudio() {
        return hasAudio;
    }
}
