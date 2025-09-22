package dev.webfx.extras.player;

import dev.webfx.extras.player.multi.MultiPlayer;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.kit.util.properties.Unregisterable;
import dev.webfx.platform.uischeduler.UiScheduler;
import javafx.scene.Node;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Bruno Salmon
 */
public final class FullscreenButton {

    private static boolean FULLSCREEN_BUTTON_ENABLED = true;
    private static Supplier<Node> FULLSCREEN_BUTTON_SHOWER;
    private static Consumer<Node> FULLSCREEN_BUTTON_HIDER;
    private static Consumer<Node> FULLSCREEN_BUTTON_ANIMATOR;
    private static Node FULLSCREEN_BUTTON;
    private static Unregisterable PLAYING_VIDEO_SCENE_CHECKER;

    public static <N extends Node> void setupFullscreenButton(Supplier<N> fullscreenButtonShower, Consumer<N> fullscreenButtonHider, Consumer<N> fullscreenButtonAnimator) {
        FULLSCREEN_BUTTON_SHOWER = (Supplier<Node>) fullscreenButtonShower;
        FULLSCREEN_BUTTON_HIDER = (Consumer<Node>) fullscreenButtonHider;
        FULLSCREEN_BUTTON_ANIMATOR = (Consumer<Node>) fullscreenButtonAnimator;
    }

    public static void setFullscreenButtonEnabled(boolean fullscreenButtonEnabled) {
        FULLSCREEN_BUTTON_ENABLED = fullscreenButtonEnabled;
    }

    static {
        FXProperties.runNowAndOnPropertyChange((observable, oldPlayer, newPlayer) -> {
            hideFullscreenButton();
            // If the old player is a video player in fullscreen, we exit that fullscreen
            if (oldPlayer != null && oldPlayer.isFullscreen()) {
                if (newPlayer != null) // we exit immediately if it's replaced by another player
                    oldPlayer.cancelFullscreen();
                else if (oldPlayer.getStatus() == Status.STOPPED) // otherwise if we reached the end of the video,
                    UiScheduler.scheduleDelay(2000, oldPlayer::cancelFullscreen); // we exit after 2s
            }
            if (newPlayer != null) {
                if (newPlayer.getFullscreenSupport().api() && FULLSCREEN_BUTTON_ENABLED) {
                    showFullscreenButton();
                }
                Node mediaView = newPlayer.getMediaView();
                if (mediaView != null) {
                    PLAYING_VIDEO_SCENE_CHECKER = FXProperties.runOnPropertyChange(scene -> {
                        if (scene == null)
                            hideFullscreenButton();
                    }, mediaView.sceneProperty());
                }
            }
        }, Players.getGlobalPlayerGroup().playingPlayerProperty());
    }

    private static void showFullscreenButton() {
        if (FULLSCREEN_BUTTON_SHOWER != null) {
            FULLSCREEN_BUTTON = FULLSCREEN_BUTTON_SHOWER.get();
            FULLSCREEN_BUTTON.setOnMouseClicked(e -> {
                Player playingPlayer = Players.getGlobalPlayerGroup().getPlayingPlayer();
                if (playingPlayer != null) {
                    MultiPlayer parentMultiPlayer = playingPlayer.getParentMultiPlayer();
                    if (parentMultiPlayer != null && parentMultiPlayer.appRequestedOverlayChildren())
                        parentMultiPlayer.requestFullscreen();
                    else
                        playingPlayer.requestFullscreen();
                }
            });
            // Continue to animate it every 30s as long as the video is played
            Player playingPlayer = Players.getGlobalPlayerGroup().getPlayingPlayer();
            UiScheduler.schedulePeriodic(30000, scheduled -> {
                if (playingPlayer != Players.getGlobalPlayerGroup().getPlayingPlayer()) {
                    scheduled.cancel();
                } else if (FULLSCREEN_BUTTON_ANIMATOR != null && FULLSCREEN_BUTTON != null) {
                    FULLSCREEN_BUTTON_ANIMATOR.accept(FULLSCREEN_BUTTON);
                }
            });
        }
    }

    private static void hideFullscreenButton() {
        if (FULLSCREEN_BUTTON_HIDER != null && FULLSCREEN_BUTTON != null)
            FULLSCREEN_BUTTON_HIDER.accept(FULLSCREEN_BUTTON);
        if (PLAYING_VIDEO_SCENE_CHECKER != null)
            PLAYING_VIDEO_SCENE_CHECKER.unregister();
    }

}
