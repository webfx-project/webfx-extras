package dev.webfx.extras.canvas.layer.interact;

public interface HasCanvasInteractionManager {

    CanvasInteractionManager getCanvasInteractionManager();

    default void enableCanvasInteraction() {
        setInteractive(true);
    }

    default void setInteractive(boolean interactive) {
        getCanvasInteractionManager().setInteractive(interactive);
    }

}
