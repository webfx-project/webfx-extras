package dev.webfx.extras.time.layout.impl;

import dev.webfx.extras.geometry.Bounds;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author Bruno Salmon
 */
public class TimeLayoutUtil {

    public static <O, OB extends ObjectBounds<O>, B extends Bounds> void processVisibleObjectBounds(List<OB> objectBounds, boolean ascY, javafx.geometry.Bounds visibleArea, double layoutOriginX, double layoutOriginY, BiConsumer<O, B> objectProcessor) {
        for (OB ob : objectBounds) {
            if (ascY) {
                if (ob.getY() - layoutOriginY > visibleArea.getMaxY())
                    break;
                if (ob.getMaxY() - layoutOriginY < visibleArea.getMinY())
                    continue;
            }
            processObjectIfVisible(ob.getObject(), ob, visibleArea, layoutOriginX, layoutOriginY, (BiConsumer<O, OB>) objectProcessor);
        }
    }

    public static <O, B extends Bounds> void processObjectIfVisible(O object, B objectBounds, javafx.geometry.Bounds visibleArea, double layoutOriginX, double layoutOriginY, BiConsumer<O, B> objectProcessor) {
        // Here is the object position in the layout coordinates:
        double layoutX = objectBounds.getMinX();
        double layoutY = objectBounds.getMinY();
        // Here is the object position in the canvas coordinates:
        double canvasX = layoutX - layoutOriginX;
        double canvasY = layoutY - layoutOriginY;
        // Skipping that object if it is not visible in the draw area
        if (visibleArea != null && !visibleArea.intersects(canvasX, canvasY, objectBounds.getWidth(), objectBounds.getHeight()))
            return; // This improves performance, as canvas operations can take time (even outside canvas)
        // Drawing the object
        objectProcessor.accept(object, objectBounds);
    }

}
