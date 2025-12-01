package dev.webfx.extras.canvas;

import javafx.beans.property.DoubleProperty;

/**
 * @author Bruno Salmon
 */
public interface HasOriginCoordinates {

    /**
     * The origin layout is the point in the layout coordinates that will match the origin of the draw area.
     * For example, if the layout origin is set to (10, 50), the first objects that will appear in the origin (i.e.,
     * left-top corner) of the draw area will be those laid out at (10, 50) in the layout coordinates. Note that
     * the origin of the draw area is the point (0, 0) of the canvas by default, but it can be translated through the
     * canvasDrawAreaProperty.
     */

    DoubleProperty originLayoutXProperty();

    default double getOriginLayoutX() {
        return originLayoutXProperty().get();
    }

    default void setOriginLayoutX(double layoutOriginX) {
        originLayoutXProperty().set(layoutOriginX);
    }

    DoubleProperty originLayoutYProperty();

    default double getOriginLayoutY() {
        return originLayoutYProperty().get();
    }

    default void setOriginLayoutY(double layoutOriginY) {
        originLayoutYProperty().set(layoutOriginY);
    }

    DoubleProperty originTranslateXProperty();

    default double getOriginTranslateX() {
        return originTranslateXProperty().get();
    }

    default void setOriginTranslateX(double TranslateOriginX) {
        originTranslateXProperty().set(TranslateOriginX);
    }

    DoubleProperty originTranslateYProperty();

    default double getOriginTranslateY() {
        return originTranslateYProperty().get();
    }

    default void setOriginTranslateY(double TranslateOriginY) {
        originTranslateYProperty().set(TranslateOriginY);
    }

    default double getOriginX() {
        return getOriginLayoutX() + getOriginTranslateX();
    }

    default double getOriginY() {
        return getOriginLayoutY() + getOriginTranslateY();
    }

}
