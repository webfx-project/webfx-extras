package dev.webfx.extras.player.metadata;

import dev.webfx.extras.player.Media;
import dev.webfx.extras.player.MediaMetadata;
import javafx.util.Duration;

/**
 * @author Bruno Salmon
 */
public class MetadataUtil {

    public static String getTitle(Media media) {
        return getTitle(media.getMetadata());
    }

    public static String getTitle(MediaMetadata metadata) {
        return metadata instanceof HasTitle ? ((HasTitle) metadata).getTitle() : null;
    }

    public static Duration getDuration(Media media) {
        return getDuration(media.getMetadata());
    }

    public static Duration getDuration(MediaMetadata metadata) {
        return metadata instanceof HasDuration ? ((HasDuration) metadata).getDuration() : null;
    }

    public static Boolean hasAudio(Media media) {
        return hasAudio(media.getMetadata());
    }

    public static Boolean hasAudio(MediaMetadata metadata) {
        return metadata instanceof HasHasAudio ? ((HasHasAudio) metadata).hasAudio() : null;
    }

}
