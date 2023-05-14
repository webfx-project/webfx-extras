package dev.webfx.extras.time.layout.impl;

import dev.webfx.extras.geometry.Bounds;
import dev.webfx.extras.geometry.MutableBounds;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author Bruno Salmon
 */
public class TimeLayoutUtil {

    public static <C, LB extends LayoutBounds<C, ?>, B extends Bounds> void processVisibleChildrenLayoutBounds(List<LB> layoutBounds, boolean ascY, boolean shift, javafx.geometry.Bounds visibleArea, double layoutOriginX, double layoutOriginY, BiConsumer<C, B> childProcessor) {
        for (LB lb : layoutBounds) {
            if (ascY) {
                if (lb.getY() - layoutOriginY > visibleArea.getMaxY())
                    break;
                if (lb.getMaxY() - layoutOriginY < visibleArea.getMinY())
                    continue;
            }
            processChildIfVisible(lb.getChild(), lb, shift, visibleArea, layoutOriginX, layoutOriginY, (BiConsumer<C, LB>) childProcessor);
        }
    }

    /*
    public static <C, B extends MutableBounds> void processVisibleChildren(List<C> children, Function<Integer, B> childPositionGetter, javafx.geometry.Bounds visibleArea, double layoutOriginX, double layoutOriginY, BiConsumer<C, B> childProcessor) {
        for (int i = 0; i < children.size(); i++) {
            C child = children.get(i);
            B cp = childPositionGetter.apply(i);
            processChildIfVisible(child, cp, false, visibleArea, layoutOriginX, layoutOriginY, childProcessor);
        }
    }
*/

    public static <C, B extends MutableBounds> void processChildIfVisible(C child, B cp, boolean shift, javafx.geometry.Bounds visibleArea, double layoutOriginX, double layoutOriginY, BiConsumer<C, B> childProcessor) {
        // Here is the child position in the layout coordinates:
        double layoutX = cp.getX();
        double layoutY = cp.getY();
        // Here is the child position in the canvas coordinates:
        double canvasX = layoutX - layoutOriginX;
        double canvasY = layoutY - layoutOriginY;
        // Skipping that child if it is not visible in the draw area
        if (visibleArea != null && !visibleArea.intersects(canvasX, canvasY, cp.getWidth(), cp.getHeight()))
            return; // This improves performance, as canvas operations can take time (even outside canvas)
        // Temporarily moving p to canvas coordinates for the drawing operations
        if (shift) {
            cp.setX(canvasX);
            cp.setY(canvasY);
        }
        // Drawing the child
        childProcessor.accept(child, cp);
        // Moving back p to layout coordinates
        if (shift) {
            cp.setX(layoutX);
            cp.setY(layoutY);
        }
    }

}
