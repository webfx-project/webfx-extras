package dev.webfx.extras.player.video.web.youtube;

import dev.webfx.extras.player.video.web.WebVideoPlayerBase;

/**
 * @author Bruno Salmon
 */
public class YoutubeVideoPlayer extends WebVideoPlayerBase {
/*
Here are some of the most commonly used parameters to remove or hide overlays:
- controls=0: Hides the player controls (like play, pause, volume, etc.). However, this does not completely remove all overlays, as some branding elements may still appear.
- modestbranding=1: Minimizes YouTube branding by hiding the YouTube logo in the control bar. Note that this parameter only reduces the branding and does not completely remove it.
- rel=0: Disables showing related videos at the end of the video. This only works if you use the old parameter behavior; in 2018, YouTube updated this behavior so that rel=0 only shows related videos from the same channel.
- fs=0: Disables the fullscreen button on the player.
- showinfo=0: This parameter used to remove the title and uploader info at the top of the video. However, this parameter was deprecated by YouTube in 2018, and as such, it no longer works.
- iv_load_policy=3: Hides video annotations. The value can be 1 to show annotations and 3 to hide them.
- autohide=1: Automatically hides the video controls after a few seconds if the video is not interacted with.
*/

    @Override
    protected String trackUrl(String track, boolean play) {
        return "https://www.youtube.com/embed/" + track + "?rel=0&modestbranding=1&showinfo=0&iv_load_policy=3&autohide=1";
    }
}
