package dev.webfx.extras.player.video.web.castr;

import dev.webfx.extras.media.metadata.MediaMetadata;
import dev.webfx.extras.player.IntegrationMode;
import dev.webfx.extras.player.Media;
import dev.webfx.extras.player.StartOptions;
import dev.webfx.extras.player.StartOptionsBuilder;
import dev.webfx.extras.player.video.web.WebVideoPlayerBase;
import dev.webfx.platform.util.Booleans;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author Bruno Salmon
 */
public final class CastrVideoPlayer extends WebVideoPlayerBase {

    public CastrVideoPlayer() {
        super(IntegrationMode.EMBEDDED);
    }

    @Override
    public Media acceptMedia(String mediaSource, MediaMetadata mediaMetadata) {
        return acceptMedia(mediaSource, mediaMetadata,
            "https://player.castr.com/",
            "castr:"
        );
    }

    /* From https://developers.castr.com/docs/embed-player-api
autoplay: on | off - Controls whether the player should automatically start playing upon load.
muted: on | off - Enables or disables muted playback.
controls: on | off - Determines the visibility of player controls.
loop: on | off - Enables looping for video-on-demand content.
pp: true | false - The player will start only when focused if this flag is set to true.
pip: on | off - Determines the visibility of PiP option.
cast: on | off - Control Google Cast detection
airplay: on | off - Set whether or not in-player AirPlay support should be activated on eligible devices.
fullscreen: on | off - Determines the visibility of fullscreen option.
click: on | off - Disables click-to-pause functionality and keyboard controls when set to 'off'.
h265: true | false - Enables H265 playback capability in the player.
speed: true | false - Enables playback speed control capability in the player.
tracks: [labels] - Customizes the labels of audio tracks by listing them in a comma-separated format. This parameter allows you to assign specific names to each audio track in the order they appear. For example, to label two audio tracks as English and French, you would use tracks=english,french.
defaultTrack: [x] - Specifies which audio track should play by default when the stream starts. The track number corresponds to the order of tracks provided in the tracks parameter, with 0 referring to the first track, 1 to the second, and so on.
buttons: true | false - When set to true, this option enables clickable buttons beneath the player interface. These buttons will be labeled based on the tracks parameter, allowing users to switch between audio tracks with ease.
seekTo: [x] - Using this, the player will seek to the time indicated by the parameter 'x', measured in seconds. This is only applicable for Video-on-Demand contents.

+ undocumented parameters?
ex: ?range=1722671889-3724&abr=false&namedHls=true
     */

    @Override
    protected void queryParamToStartOption(String name, String value, StartOptionsBuilder sob) {
        switch (name) {
            case "autoplay": sob.setAutoplay(isTrue(value)); break;
            case "muted": sob.setMuted(isTrue(value)); break;
            case "fullscreen": sob.setFullscreen(isTrue(value)); break;
            case "range" :
                String[] range = value.split("-");
                sob.setStartDateTime(LocalDateTime.ofEpochSecond(Long.parseLong(range[0]), 0, ZoneOffset.UTC));
                sob.setEndDateTime(sob.getStartDateTime().plusSeconds(Long.parseLong(range[1])));
                break;
            case "tracks":
                sob.setTracks(value);
                break;
        }
    }

    @Override
    protected String generateMediaEmbedRawUrl() {
        Media media = getMedia();
        return "https://player.castr.com/" + media.getId();
    }

    @Override
    protected void appendUrlParameters(StartOptions so, StringBuilder sb) {
        if (Booleans.isTrue(so.autoplay()))
            sb.append("&autoplay=on");
        if (Booleans.isFalse(so.fullscreen()))
            sb.append("&fullscreen=off");
        if (so.startDateTime() != null && so.endDateTime() != null) {
            sb.append("&range=").append(so.startDateTime().toEpochSecond(ZoneOffset.UTC)).append('-').append(java.time.Duration.between(so.startDateTime(), so.endDateTime()).getSeconds())
                .append("&abr=false&namedHls=true");
        }
        if (so.getTracks() != null)
            sb.append("&tracks=").append(so.getTracks());
    }

    private static boolean isTrue(String paramValue) {
        return "on".equalsIgnoreCase(paramValue) || "true".equalsIgnoreCase(paramValue);
    }
}