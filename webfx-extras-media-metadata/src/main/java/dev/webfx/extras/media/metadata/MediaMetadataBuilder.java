package dev.webfx.extras.media.metadata;

/**
 * @author Bruno Salmon
 */
public final class MediaMetadataBuilder {

    private String title;
    private Long durationMillis;
    private Boolean hasAudio;

    public String getTitle() {
        return title;
    }

    public MediaMetadataBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public Long getDurationMillis() {
        return durationMillis;
    }

    public MediaMetadataBuilder setDurationMillis(Long durationMillis) {
        this.durationMillis = durationMillis;
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
        return new FullMediaMetadataImpl(title, durationMillis, hasAudio);
    }
}
