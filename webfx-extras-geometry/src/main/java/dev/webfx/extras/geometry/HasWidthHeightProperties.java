package dev.webfx.extras.geometry;

/**
 * @author Bruno Salmon
 */
public interface HasWidthHeightProperties extends HasWidthProperty, HasHeightProperty {

    default void resize(double width, double height) {
        setHeight(height); // Shouldn't trigger a layout pass
        setWidth(width);   // May trigger a layout pass, which may adjust back the height
    }

}
