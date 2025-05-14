package dev.webfx.extras.player.multi.all;

import dev.webfx.extras.player.Player;
import dev.webfx.extras.player.audio.javafxmedia.JavaFXMediaAudioPlayer;
import dev.webfx.extras.player.multi.MultiPlayer;
import dev.webfx.extras.player.video.web.GenericWebVideoPlayer;
import dev.webfx.extras.player.video.web.castr.CastrVideoPlayer;
import dev.webfx.extras.player.video.web.wistia.WistiaVideoPlayer;
import dev.webfx.extras.player.video.web.youtube.YoutubeVideoPlayer;
import dev.webfx.platform.util.Arrays;

/**
 * @author Bruno Salmon
 */
public final class AllPlayers {

    public static MultiPlayer createAllAudioPlayer() {
        return new MultiPlayer(createAllAudioPlayers());
    }

    public static MultiPlayer createAllVideoPlayer() {
        return new MultiPlayer(createAllVideoPlayers());
    }

    public static MultiPlayer createAllAudioVideoPlayer() {
        return new MultiPlayer(createAllAudioVideoPlayers());
    }

    public static Player[] createAllAudioPlayers() {
        return new Player[] {
            createJavaFXMediaAudioPlayer(),
        };
    }

    public static Player[] createAllVideoPlayers() {
        return new Player[] {
            createYoutubeVideoPlayer(),
            createWistiaVideoPlayer(),
            createCastrVideoPlayer(),
            createGenericWebVideoPlayer()
        };
    }

    public static Player[] createAllAudioVideoPlayers() {
        return Arrays.concat(Player[]::new, createAllVideoPlayers(), createAllAudioPlayers());
    }

    public static YoutubeVideoPlayer createYoutubeVideoPlayer() {
        return new YoutubeVideoPlayer();
    }

    public static WistiaVideoPlayer createWistiaVideoPlayer() {
        return new WistiaVideoPlayer();
    }

    public static CastrVideoPlayer createCastrVideoPlayer() {
        return new CastrVideoPlayer();
    }

    public static GenericWebVideoPlayer createGenericWebVideoPlayer() {
        return new GenericWebVideoPlayer();
    }

    public static JavaFXMediaAudioPlayer createJavaFXMediaAudioPlayer() {
        return new JavaFXMediaAudioPlayer(); // Note that this one is currently accepting any source, so to be put last
    }

}
