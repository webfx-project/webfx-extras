package dev.webfx.extras.timelayout.gantt;

import dev.webfx.extras.timelayout.ChildPosition;
import dev.webfx.extras.timelayout.canvas.ChildDrawer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.time.temporal.Temporal;

/**
 * @author Bruno Salmon
 */
public final class ParentCanvasPane<P, T extends Temporal> extends Pane {

    private double parentHeight = 20; // arbitrary initial value (better than 0 is application code forgot to set it)
    private final Canvas canvas;
    private final GanttLayout<?, T> ganttLayout;
    private final ChildDrawer<P, T> parentDrawer;

    public ParentCanvasPane(GanttLayout<?, T> ganttLayout, ChildDrawer<P, T> parentDrawer) {
        this(new Canvas(), ganttLayout, parentDrawer);
    }

    public ParentCanvasPane(Canvas canvas, GanttLayout<?, T> ganttLayout, ChildDrawer<P, T> parentDrawer) {
        super(canvas);
        this.canvas = canvas;
        this.ganttLayout = ganttLayout;
        this.parentDrawer = parentDrawer;
        ganttLayout.addOnAfterLayout(this::redrawCanvas);
        setMinWidth(100);
        setMaxWidth(150);
    }

    public double getParentHeight() {
        return parentHeight;
    }

    public void setParentHeight(double parentHeight) {
        this.parentHeight = parentHeight;
    }

    @Override
    protected void layoutChildren() {
        canvas.setWidth(getWidth());
        canvas.setHeight(getHeight());
        redrawCanvas();
    }

    private void redrawCanvas() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        ChildPosition<T> p = new ChildPosition<>();
        p.setX(0);
        p.setWidth(canvas.getWidth());
        p.setHeight(parentHeight);
        p.setY(0);
        for (Object parent : ganttLayout.getParents()) {
            parentDrawer.drawChild((P) parent, p, gc);
            p.setY(p.getY() + parentHeight);
        }
    }

}
