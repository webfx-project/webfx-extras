package dev.webfx.extras.player;

import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.time.LocalDateTime;

/**
 * @author Bruno Salmon
 */
public interface StartOptions {

    Boolean autoplay();

    Boolean muted();

    Boolean loop();

    Boolean fullscreen();

    Duration startTime();

    Duration endTime();

    LocalDateTime startDateTime();

    LocalDateTime endDateTime();

    Double aspectRatio();

    Color playerColor();

    String getTracks();

}
