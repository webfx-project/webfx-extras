package dev.webfx.extras.player.metadata;

import javafx.util.Duration;

/**
 * @author Bruno Salmon
 */
public final class MediaMetadataBuilder {

    private String title;
    private Duration duration;
    private Boolean hasAudio;

    public String getTitle() {
        return title;
    }

    public MediaMetadataBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public Duration getDuration() {
        return duration;
    }

    public MediaMetadataBuilder setDuration(Duration duration) {
        this.duration = duration;
        return this;
    }

    public Boolean getHasAudio() {
        return hasAudio;
    }

    public MediaMetadataBuilder setHasAudio(Boolean hasAudio) {
        this.hasAudio = hasAudio;
        return this;
    }

    public FullMediaMetadata build() {
        return new FullMediaMetadataImpl(title, duration, hasAudio);
    }
}
