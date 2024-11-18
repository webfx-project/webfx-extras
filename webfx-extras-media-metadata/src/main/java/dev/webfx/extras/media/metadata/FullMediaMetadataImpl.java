package dev.webfx.extras.media.metadata;

/**
 * @author Bruno Salmon
 */
public class FullMediaMetadataImpl implements FullMediaMetadata {

    private final String title;
    private final Long durationMillis;
    private final Boolean hasAudio;

    public FullMediaMetadataImpl(String title, Long durationMillis, Boolean hasAudio) {
        this.title = title;
        this.durationMillis = durationMillis;
        this.hasAudio = hasAudio;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Long getDurationMillis() {
        return durationMillis;
    }

    @Override
    public Boolean hasAudio() {
        return hasAudio;
    }
}
