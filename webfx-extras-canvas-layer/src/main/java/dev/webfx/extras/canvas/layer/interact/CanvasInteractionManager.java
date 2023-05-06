package dev.webfx.extras.canvas.layer.interact;

import javafx.scene.canvas.Canvas;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bruno Salmon
 */
public class CanvasInteractionManager {

    private final Canvas canvas;
    private final List<CanvasInteractionHandler> handlers = new ArrayList<>();

    public CanvasInteractionManager(Canvas canvas) {
        this.canvas = canvas;
    }

    public void addHandler(CanvasInteractionHandler handler) {
        addHandler(handler, false);
    }

    public void addHandler(CanvasInteractionHandler handler, boolean priority) {
        if (priority)
            handlers.add(0, handler);
        else
            handlers.add(handler);
    }

    public void removeHandler(CanvasInteractionHandler handler) {
        handlers.remove(handler);
    }

    public void makeCanvasInteractive() {
        setInteractive(true);
    }

    public void setInteractive(boolean interactive) {
        boolean off = !interactive;
        canvas.setOnMousePressed(off ? null : e -> {
            handlers.stream().filter(h -> !h.handleMousePressed(e, canvas)).findFirst();
        });
        canvas.setOnMouseDragged(off ? null : e -> {
            handlers.stream().filter(h -> !h.handleMouseDragged(e, canvas)).findFirst();
        });
        canvas.setOnMouseClicked(off ? null : e -> {
            handlers.stream().filter(h -> !h.handleMouseClicked(e, canvas)).findFirst();
        });
        canvas.setOnMouseMoved(off ? null : e -> {
            handlers.stream().filter(h -> !h.handleMouseMoved(e, canvas)).findFirst();
        });
        canvas.setOnScroll(off ? null : e -> {
            handlers.stream().filter(h -> !h.handleScroll(e, canvas)).findFirst();
        });
    }

}
