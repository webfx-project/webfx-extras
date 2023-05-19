package dev.webfx.extras.time.layout.impl;

import dev.webfx.extras.geometry.Bounds;
import dev.webfx.extras.geometry.MutableBounds;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author Bruno Salmon
 */
public class TimeLayoutUtil {

    public static <O, OB extends ObjectBounds<O>, B extends Bounds> void processVisibleObjectBounds(List<OB> objectBounds, boolean ascY, boolean shift, javafx.geometry.Bounds visibleArea, double layoutOriginX, double layoutOriginY, BiConsumer<O, B> objectProcessor) {
        for (OB ob : objectBounds) {
            if (ascY) {
                if (ob.getY() - layoutOriginY > visibleArea.getMaxY())
                    break;
                if (ob.getMaxY() - layoutOriginY < visibleArea.getMinY())
                    continue;
            }
            processObjectIfVisible(ob.getObject(), ob, shift, visibleArea, layoutOriginX, layoutOriginY, (BiConsumer<O, OB>) objectProcessor);
        }
    }

    public static <O, B extends MutableBounds> void processObjectIfVisible(O object, B ob, boolean shift, javafx.geometry.Bounds visibleArea, double layoutOriginX, double layoutOriginY, BiConsumer<O, B> objectProcessor) {
        // Here is the object position in the layout coordinates:
        double layoutX = ob.getX();
        double layoutY = ob.getY();
        // Here is the object position in the canvas coordinates:
        double canvasX = layoutX - layoutOriginX;
        double canvasY = layoutY - layoutOriginY;
        // Skipping that object if it is not visible in the draw area
        if (visibleArea != null && !visibleArea.intersects(canvasX, canvasY, ob.getWidth(), ob.getHeight()))
            return; // This improves performance, as canvas operations can take time (even outside canvas)
        // Temporarily moving p to canvas coordinates for the drawing operations
        if (shift) {
            ob.setX(canvasX);
            ob.setY(canvasY);
        }
        // Drawing the object
        objectProcessor.accept(object, ob);
        // Moving back p to layout coordinates
        if (shift) {
            ob.setX(layoutX);
            ob.setY(layoutY);
        }
    }

}
