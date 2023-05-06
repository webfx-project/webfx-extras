package dev.webfx.extras.canvas;

import javafx.beans.property.DoubleProperty;

/**
 * @author Bruno Salmon
 */
public interface HasLayoutOriginProperties {

    /**
     * The layout origin is the point in the layout coordinates that will match the origin of the draw area.
     * For example, if the layout origin is set to (10, 50), the first objects that will appear in the origin (ie
     * left-top corner) of the draw area will be those laid out at (10, 50) in the layout coordinates. Note that
     * the origin of the draw area is the point (0, 0) of the canvas by default, but it can be translated through the
     * canvasDrawAreaProperty.
     */

    DoubleProperty layoutOriginXProperty();

    default double getLayoutOriginX() {
        return layoutOriginXProperty().get();
    }

    default void setLayoutOriginX(double layoutOriginX) {
        layoutOriginXProperty().set(layoutOriginX);
    }

    DoubleProperty layoutOriginYProperty();

    default double getLayoutOriginY() {
        return layoutOriginYProperty().get();
    }

    default void setLayoutOriginY(double layoutOriginY) {
        layoutOriginYProperty().set(layoutOriginY);
    }

}
