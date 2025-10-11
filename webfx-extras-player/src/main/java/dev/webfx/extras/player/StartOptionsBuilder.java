package dev.webfx.extras.player;

import dev.webfx.extras.player.impl.StartOptionsImpl;
import dev.webfx.platform.util.Objects;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.time.LocalDateTime;

/**
 * @author Bruno Salmon
 */
public class StartOptionsBuilder {

    private Boolean autoplay;
    private Boolean muted;
    private Boolean loop;
    private Boolean fullscreen;
    private Duration startTime;
    private Duration endTime;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Double aspectRatio;
    private Color playerColor;
    private String tracks;

    public StartOptionsBuilder setAutoplay(Boolean autoplay) {
        this.autoplay = autoplay;
        return this;
    }

    public Boolean getAutoplay() {
        return autoplay;
    }

    public StartOptionsBuilder setMuted(Boolean muted) {
        this.muted = muted;
        return this;
    }

    public Boolean getMuted() {
        return muted;
    }

    public StartOptionsBuilder setLoop(Boolean loop) {
        this.loop = loop;
        return this;
    }

    public Boolean getLoop() {
        return loop;
    }

    public StartOptionsBuilder setFullscreen(Boolean fullscreen) {
        this.fullscreen = fullscreen;
        return this;
    }

    public Boolean getFullscreen() {
        return fullscreen;
    }

    public StartOptionsBuilder setStartTime(Duration startTime) {
        this.startTime = startTime;
        return this;
    }

    public Duration getStartTime() {
        return startTime;
    }

    public StartOptionsBuilder setEndTime(Duration endTime) {
        this.endTime = endTime;
        return this;
    }

    public Duration getEndTime() {
        return endTime;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public StartOptionsBuilder setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
        return this;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public StartOptionsBuilder setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
        return this;
    }

    public StartOptionsBuilder setAspectRatio(Double aspectRatio) {
        this.aspectRatio = aspectRatio;
        return this;
    }

    public StartOptionsBuilder setAspectRatioTo16by9() {
        return setAspectRatio(16d/9d);
    }

    public Double getAspectRatio() {
        return aspectRatio;
    }

    public StartOptionsBuilder setPlayerColor(Color playerColor) {
        this.playerColor = playerColor;
        return this;
    }

    public Color getPlayerColor() {
        return playerColor;
    }

    public StartOptionsBuilder setTracks(String tracks) {
        this.tracks = tracks;
        return this;
    }

    public String getTracks() {
        return tracks;
    }

    public StartOptionsBuilder applyStartOptions(StartOptions so) {
        if (so == null)
            return this;
        autoplay = Objects.coalesce(so.autoplay(), autoplay);
        muted = Objects.coalesce(so.muted(), muted);
        loop = Objects.coalesce(so.loop(), loop);
        fullscreen = Objects.coalesce(so.fullscreen(), fullscreen);
        startTime = Objects.coalesce(so.startTime(), startTime);
        endTime = Objects.coalesce(so.endTime(), endTime);
        startDateTime = Objects.coalesce(so.startDateTime(), startDateTime);
        endDateTime = Objects.coalesce(so.endDateTime(), endDateTime);
        aspectRatio = Objects.coalesce(so.aspectRatio(), aspectRatio);
        playerColor = Objects.coalesce(so.playerColor(), playerColor);
        tracks = Objects.coalesce(so.getTracks(), tracks);
        return this;
    }

    public StartOptions build() {
        return new StartOptionsImpl(autoplay, muted, loop, fullscreen, startTime, endTime, startDateTime, endDateTime, aspectRatio, playerColor, tracks);
    }
}
