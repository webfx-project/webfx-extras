package dev.webfx.extras.timelayout.canvas;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.paint.Paint;

public interface CanvasDrawer extends HasCanvas {

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

    /**
     * The draw area defines which area of the canvas is covered by this CanDraw instance. The default bounds value is
     * null, and it means the whole canvas. In that case, drawArea() will erase the whole canvas before drawing the
     * objects, and no clip is set on the canvas. But in some scenarios, a same canvas instance can be shared between
     * different CanDraw instances, each instance covering a different part (ex: left part / right part). In that case,
     * the application code must set the draw area property for each CanDraw instance, and then their drawArea() method
     * will erase only that area and clip it before drawing the objects.
     */

    ObjectProperty<Bounds> drawAreaBoundsProperty();

    default Bounds getDrawAreaBounds() {
        return drawAreaBoundsProperty().get();
    }

    default void setDrawAreaBounds(Bounds drawAreaBounds) {
        drawAreaBoundsProperty().set(drawAreaBounds);
    }

    default Bounds getDrawAreaOrCanvasBounds() {
        Bounds drawAreaBounds = getDrawAreaBounds();
        return drawAreaBounds != null ? drawAreaBounds : new BoundingBox(0, 0, getCanvas().getWidth(), getCanvas().getHeight());
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

    ObservableIntegerValue drawCountProperty();

    default int getDrawCount() {
        return Math.abs(drawCountProperty().get());
    }

    default boolean isDrawing() {
        return drawCountProperty().get() < 0;
    }

    default void addOnBeforeDraw(Runnable runnable) {
        drawCountProperty().addListener((observable, oldValue, newValue) -> {
            if (isDrawing())
                runnable.run();
        });
    }

    default void addOnAfterDraw(Runnable runnable) {
        drawCountProperty().addListener((observable, oldValue, newValue) -> {
            if (!isDrawing())
                runnable.run();
        });
    }

}
