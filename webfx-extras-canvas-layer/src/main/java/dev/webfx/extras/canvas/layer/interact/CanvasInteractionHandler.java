package dev.webfx.extras.canvas.layer.interact;

import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 * @author Bruno Salmon
 */
public interface CanvasInteractionHandler {

    // Each method return a boolean telling if the event propagation continues (true) or stops (false)

    default boolean handleMousePressed(MouseEvent e, Canvas canvas) { return true; }

    default boolean handleMouseDragged(MouseEvent e, Canvas canvas) { return true; }

    default boolean handleMouseClicked(MouseEvent e, Canvas canvas) { return true; }

    default boolean handleMouseMoved(MouseEvent e, Canvas canvas) { return true; }

    default boolean handleScroll(ScrollEvent e, Canvas canvas) { return true; }

}
