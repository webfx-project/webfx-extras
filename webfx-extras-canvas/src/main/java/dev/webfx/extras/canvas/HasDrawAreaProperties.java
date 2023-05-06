package dev.webfx.extras.canvas;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Bounds;
import javafx.scene.paint.Paint;

public interface HasDrawAreaProperties {

    /**
     * The draw area defines which area of the canvas this instance has the responsibility to draw. If the area value is
     * null, it means the whole canvas. In that case, drawArea() will erase the whole canvas before drawing the objects,
     * and no clip is set on the canvas. But in some scenarios, the same canvas can be shared between different instances,
     * each instance drawing a different part (ex: left part / right part). In that case, the application code must set
     * the draw area property for each CanDraw instance, and then their drawArea() method will erase only that area and
     * clip it before drawing the objects.
     */

    ObjectProperty<Bounds> drawAreaBoundsProperty();

    default Bounds getDrawAreaBounds() {
        return drawAreaBoundsProperty().get();
    }

    default void setDrawAreaBounds(Bounds drawAreaBounds) {
        drawAreaBoundsProperty().set(drawAreaBounds);
    }

    ObjectProperty<Paint> drawAreaBackgroundFillProperty();

    default Paint getDrawAreaBackgroundFill() {
        return drawAreaBackgroundFillProperty().get();
    }

    default void setDrawAreaBackgroundFill(Paint drawAreaBackgroundFill) {
        drawAreaBackgroundFillProperty().set(drawAreaBackgroundFill);
    }

    void markDrawAreaAsDirty();

    void drawArea();

}
