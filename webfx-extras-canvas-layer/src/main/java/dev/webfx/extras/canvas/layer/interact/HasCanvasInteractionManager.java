package dev.webfx.extras.canvas.layer.interact;

public interface HasCanvasInteractionManager {

    CanvasInteractionManager getCanvasInteractionManager();

    default HasCanvasInteractionManager enableCanvasInteraction() {
        setInteractive(true);
        return this;
    }

    default void setInteractive(boolean interactive) {
        getCanvasInteractionManager().setInteractive(interactive);
    }

}
