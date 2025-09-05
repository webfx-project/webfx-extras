package dev.webfx.extras.player.video.web;

import dev.webfx.extras.player.FeatureSupport;
import dev.webfx.extras.player.IntegrationMode;
import dev.webfx.extras.player.Status;
import dev.webfx.kit.util.properties.FXProperties;

/**
 * @author Bruno Salmon
 */
public abstract class SeamlessCapableWebVideoPlayer extends WebVideoPlayerBase {

    protected static final boolean IS_SEAMLESS = IS_BROWSER;

    public SeamlessCapableWebVideoPlayer() {
        super(isSeamless() ? IntegrationMode.SEAMLESS : IntegrationMode.EMBEDDED);
        if (IS_SEAMLESS) {
            FXProperties.runOnPropertyChange(scene -> {
                if (scene == null)
                    onVideoViewDetached();
            }, getMediaView().sceneProperty());
        }
    }

    protected void onVideoViewDetached() {
        if (isPlaying())
            setStatus(Status.STOPPED);
    }

    public static boolean isSeamless() {
        return IS_SEAMLESS;
    }

    @Override
    public void displayVideo() {
        if (IS_SEAMLESS) {
            updatePlayingStartingOption(false);
            seamless_displayVideo();
        } else
            super.displayVideo();
    }

    protected abstract void seamless_displayVideo();

    @Override
    public FeatureSupport getNavigationSupport() {
        if (IS_SEAMLESS)
            return FeatureSupport.FULL_SUPPORT;
        return super.getNavigationSupport();
    }

    @Override
    public void play() {
        if (IS_SEAMLESS) {
            updatePlayingStartingOption(false);
            seamless_play();
        } else {
            super.play();
        }
    }

    protected abstract void seamless_play();

    @Override
    public void pause() {
        if (IS_SEAMLESS) {
            seamless_pause();
        } else {
            super.pause();
        }
    }

    protected abstract void seamless_pause();

    @Override
    public void stop() {
        if (IS_SEAMLESS) {
            seamless_stop();
        } else {
            super.stop();
        }
    }

    protected abstract void seamless_stop();

    @Override
    public FeatureSupport getFullscreenSupport() {
        if (IS_SEAMLESS)
            return FeatureSupport.FULL_SUPPORT;
        return super.getFullscreenSupport();
    }

    @Override
    public void requestFullscreen() {
        if (IS_SEAMLESS && getStatus() == Status.PLAYING) {
            seamless_requestFullscreen();
            setFullscreen(true);
        } else
            super.requestFullscreen();
    }

    protected abstract void seamless_requestFullscreen();

    @Override
    public void cancelFullscreen() {
        if (IS_SEAMLESS) {
            seamless_cancelFullscreen();
            setFullscreen(false);
        } else
            super.cancelFullscreen();
    }

    protected abstract void seamless_cancelFullscreen();

}
