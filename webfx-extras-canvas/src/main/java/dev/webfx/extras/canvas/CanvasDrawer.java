package dev.webfx.extras.canvas;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;

public interface CanvasDrawer extends HasCanvas,
        HasDrawCountProperty,
        HasLayoutOriginProperties,
        HasDrawAreaProperties {

    default Bounds getDrawAreaOrCanvasBounds() {
        Bounds drawAreaBounds = getDrawAreaBounds();
        return drawAreaBounds != null ? drawAreaBounds : new BoundingBox(0, 0, getCanvas().getWidth(), getCanvas().getHeight());
    }

}
