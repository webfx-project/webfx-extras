package dev.webfx.extras.player.video.web;

import dev.webfx.extras.player.Status;
import dev.webfx.extras.player.video.IntegrationMode;

/**
 * @author Bruno Salmon
 */
public abstract class SeamlessCapableWebVideoPlayer extends WebVideoPlayerBase {

    protected static final boolean IS_SEAMLESS = IS_BROWSER;

    private boolean fullscreen;

    public SeamlessCapableWebVideoPlayer() {
        super(isSeamless() ? IntegrationMode.SEAMLESS : IntegrationMode.EMBEDDED);
        if (IS_SEAMLESS) {
            getVideoView().sceneProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null)
                    onVideoViewDetached();
            });
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
            seamless_displayVideo();
        } else
            super.displayVideo();
    }

    protected abstract void seamless_displayVideo();

    @Override
    public void play() {
        if (IS_SEAMLESS) {
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
    public boolean supportsFullscreen() {
        if (IS_SEAMLESS)
            return true;
        return super.supportsFullscreen();
    }

    @Override
    public void requestFullscreen() {
        if (IS_SEAMLESS && getStatus() == Status.PLAYING) {
            seamless_requestFullscreen();
            fullscreen = true;
        }
    }

    protected abstract void seamless_requestFullscreen();

    @Override
    public boolean isFullscreen() {
        if (fullscreen)
            return true;
        return super.isFullscreen();
    }

    @Override
    public void cancelFullscreen() {
        if (IS_SEAMLESS)
            seamless_cancelFullscreen();
        else
            super.cancelFullscreen();
    }

    protected abstract void seamless_cancelFullscreen();

    // Callback methods called by web player in seamless mode

    public void onReady() {
        //Console.log("onReady()");
        setStatus(Status.READY);
    }

    public void onPlay() {
        //Console.log("onPlay()");
        setStatus(Status.PLAYING);
    }

    public void onPause() {
        //Console.log("onPause()");
        if (getStatus() != Status.STOPPED)
            setStatus(Status.PAUSED);
    }

    public void onEnd() {
        //Console.log("onEnd()");
        setStatus(Status.STOPPED);
    }

}
