package dev.webfx.extras.player.impl;

import dev.webfx.extras.player.StartOptions;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.time.LocalDateTime;

/**
 * @author Bruno Salmon
 */
public final class StartOptionsImpl implements StartOptions {

    private final Boolean autoplay;
    private final Boolean muted;
    private final Boolean fullscreen;
    private final Duration startTime;
    private final Duration endTime;
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;
    private final Double aspectRatio;
    private final Color playerColor;

    public StartOptionsImpl(Boolean autoplay, Boolean muted, Boolean fullscreen, Duration startTime, Duration endTime, LocalDateTime startDateTime, LocalDateTime endDateTime, Double aspectRatio, Color playerColor) {
        this.autoplay = autoplay;
        this.muted = muted;
        this.fullscreen = fullscreen;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.aspectRatio = aspectRatio;
        this.playerColor = playerColor;
    }

    @Override
    public Boolean autoplay() {
        return autoplay;
    }

    @Override
    public Boolean muted() {
        return muted;
    }

    @Override
    public Boolean fullscreen() {
        return fullscreen;
    }

    @Override
    public Duration startTime() {
        return startTime;
    }

    @Override
    public Duration endTime() {
        return endTime;
    }

    @Override
    public LocalDateTime startDateTime() {
        return startDateTime;
    }

    @Override
    public LocalDateTime endDateTime() {
        return endDateTime;
    }

    @Override
    public Double aspectRatio() {
        return aspectRatio;
    }

    public Color playerColor() {
        return playerColor;
    }
}
