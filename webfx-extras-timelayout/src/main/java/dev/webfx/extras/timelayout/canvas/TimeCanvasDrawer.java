package dev.webfx.extras.timelayout.canvas;

import dev.webfx.extras.timelayout.LayoutPosition;
import dev.webfx.extras.timelayout.TimeLayout;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.List;
import java.util.function.Function;

/**
 * @author Bruno Salmon
 */
public class TimeCanvasDrawer<C, T> extends CanvasDrawerBase {
    private final TimeLayout<C, T> timeLayout;
    private final ChildDrawer<C> childDrawer;

    public TimeCanvasDrawer(TimeLayout<C, T> timeLayout, ChildDrawer<C> childDrawer) {
        this(new Canvas(), timeLayout, childDrawer);
    }

    public TimeCanvasDrawer(Canvas canvas, TimeLayout<C, T> timeLayout, ChildDrawer<C> childDrawer) {
        super(canvas);
        this.timeLayout = timeLayout;
        this.childDrawer = childDrawer;
        timeLayout.addOnAfterLayout(this::markDrawAreaAsDirty);
        timeLayout.selectedChildProperty().addListener(observable -> markDrawAreaAsDirty());
    }

    @Override
    protected void drawObjectsInArea() {
        drawVisibleChildren(getDrawAreaOrCanvasBounds(), getLayoutOriginX(), getLayoutOriginY(), timeLayout, childDrawer, gc);
    }

    public static <C, T> void drawVisibleChildren(Bounds drawAreaBounds, double layoutOriginX, double layoutOriginY, TimeLayout<C, T> timeLayout, ChildDrawer<C> childDrawer, GraphicsContext gc) {
        if (!timeLayout.isVisible())
            return;
        timeLayout.layoutIfDirty(); // ensuring the layout is done
        drawVisibleChildren(timeLayout.getChildren(), timeLayout::getChildPosition, drawAreaBounds, layoutOriginX, layoutOriginY, childDrawer, gc);
    }

    public static <C> void drawVisibleChildren(List<C> children, Function<Integer, LayoutPosition> childPositionGetter, Bounds drawAreaBounds, double layoutOriginX, double layoutOriginY, ChildDrawer<C> childDrawer, GraphicsContext gc) {
        for (int i = 0; i < children.size(); i++) {
            C child = children.get(i);
            LayoutPosition p = childPositionGetter.apply(i);
            drawChildIfVisible(child, p, drawAreaBounds, layoutOriginX, layoutOriginY, childDrawer, gc);
        }
    }

    public static <C> void drawChildIfVisible(C child, LayoutPosition p, Bounds drawAreaBounds, double layoutOriginX, double layoutOriginY, ChildDrawer<C> childDrawer, GraphicsContext gc) {
        // Here is the child position in the layout coordinates:
        double layoutX = p.getX();
        double layoutY = p.getY();
        // Here is the child position in the canvas coordinates:
        double canvasX = layoutX - layoutOriginX;
        double canvasY = layoutY - layoutOriginY;
        // Skipping that child if it is not visible in the draw area
        if (!drawAreaBounds.intersects(canvasX, canvasY, p.getWidth(), p.getHeight()))
            return; // This improves performance, as canvas operations can take time (even outside canvas)
        // Temporarily moving p to canvas coordinates for the drawing operations
        p.setX(canvasX);
        p.setY(canvasY);
        // Drawing the child
        gc.save();
        childDrawer.drawChild(child, p, gc);
        gc.restore();
        // Moving back p to layout coordinates
        p.setX(layoutX);
        p.setY(layoutY);
    }

}
