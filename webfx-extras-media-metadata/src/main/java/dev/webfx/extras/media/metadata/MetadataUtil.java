package dev.webfx.extras.media.metadata;

/**
 * @author Bruno Salmon
 */
public class MetadataUtil {

    public static String getTitle(MediaMetadata metadata) {
        return metadata instanceof HasTitle ? ((HasTitle) metadata).getTitle() : null;
    }

    public static Long getDurationMillis(MediaMetadata metadata) {
        return metadata instanceof HasDurationMillis ? ((HasDurationMillis) metadata).getDurationMillis() : null;
    }

    public static Boolean hasAudio(MediaMetadata metadata) {
        return metadata instanceof HasHasAudio ? ((HasHasAudio) metadata).hasAudio() : null;
    }

}
