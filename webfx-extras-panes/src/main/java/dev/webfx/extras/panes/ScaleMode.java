package dev.webfx.extras.panes;

/**
 * @author Bruno Salmon
 */
public enum ScaleMode {
    FIT_WIDTH, // scales the node, so its width fits the area width (can eventually crop the node in height)
    FIT_HEIGHT, // scales the node, so its height fits the area height (can eventually crop the node in width)
    BEST_FIT, // scales the node, so it fits either the area with or height (without cropping the node)
    BEST_ZOOM // scales the node, so it fits the area with and height (can eventually crop the node width or height)
}
