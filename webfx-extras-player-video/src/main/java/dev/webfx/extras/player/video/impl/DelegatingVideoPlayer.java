package dev.webfx.extras.player.video.impl;

import dev.webfx.extras.player.impl.DelegatingPlayer;
import dev.webfx.extras.player.video.IntegrationMode;
import dev.webfx.extras.player.video.VideoPlayer;
import javafx.scene.Node;

/**
 * @author Bruno Salmon
 */
public class DelegatingVideoPlayer extends DelegatingPlayer implements VideoPlayer {

    VideoPlayer delegate;

    public DelegatingVideoPlayer(VideoPlayer delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    @Override
    public IntegrationMode getIntegrationMode() {
        return delegate.getIntegrationMode();
    }

    @Override
    public Node getVideoView() {
        return delegate.getVideoView();
    }

    @Override
    public void displayVideo() {
        delegate.displayVideo();
    }

    @Override
    public boolean supportsFullscreen() {
        return delegate.supportsFullscreen();
    }

    @Override
    public void requestFullscreen() {
        delegate.requestFullscreen();
    }

    @Override
    public boolean isFullscreen() {
        return delegate.isFullscreen();
    }

    @Override
    public void cancelFullscreen() {
        delegate.cancelFullscreen();
    }
}
