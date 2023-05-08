package dev.webfx.extras.time.layout;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author Bruno Salmon
 */
public class TimeLayoutUtil {

    public static <C> void processVisibleChildren(List<C> children, Function<Integer, LayoutBounds> childPositionGetter, BiConsumer<C, LayoutBounds> childProcessor) {
        processVisibleChildren(children, childPositionGetter, null, 0, 0, childProcessor);
    }

    public static <C> void processVisibleChildren(List<C> children, Function<Integer, LayoutBounds> childPositionGetter, javafx.geometry.Bounds visibleArea, double layoutOriginX, double layoutOriginY, BiConsumer<C, LayoutBounds> childProcessor) {
        for (int i = 0; i < children.size(); i++) {
            C child = children.get(i);
            LayoutBounds cp = childPositionGetter.apply(i);
            processChildIfVisible(child, cp, visibleArea, layoutOriginX, layoutOriginY, childProcessor);
        }
    }

    public static <C> void processChildIfVisible(C child, LayoutBounds cp, javafx.geometry.Bounds visibleArea, double layoutOriginX, double layoutOriginY, BiConsumer<C, LayoutBounds> childProcessor) {
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
        cp.setX(canvasX);
        cp.setY(canvasY);
        // Drawing the child
        childProcessor.accept(child, cp);
        // Moving back p to layout coordinates
        cp.setX(layoutX);
        cp.setY(layoutY);
    }

}
