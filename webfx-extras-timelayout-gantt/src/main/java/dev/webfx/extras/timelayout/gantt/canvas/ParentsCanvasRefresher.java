package dev.webfx.extras.timelayout.gantt.canvas;

import dev.webfx.extras.timelayout.ChildPosition;
import dev.webfx.extras.timelayout.canvas.ChildDrawer;
import dev.webfx.extras.timelayout.gantt.GanttLayout;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.time.temporal.Temporal;

/**
 * @author Bruno Salmon
 */
public final class ParentsCanvasRefresher<P> {

    private double parentHeight = 40; // arbitrary initial value (better than 0 is application code forgot to set it)
    private final Canvas canvas;
    private final GanttLayout<?, ?> ganttLayout;
    private final ChildDrawer<P, Temporal> parentDrawer;
    private double lastVirtualCanvasWidth, lastVirtualCanvasHeight, lastVirtualViewPortY;

    public ParentsCanvasRefresher(Canvas canvas, GanttLayout<?, ?> ganttLayout, ChildDrawer<P, Temporal> parentDrawer) {
        this(canvas, ganttLayout, parentDrawer, true);
    }

    public ParentsCanvasRefresher(Canvas canvas, GanttLayout<?, ?> ganttLayout, ChildDrawer<P, Temporal> parentDrawer, boolean redrawAfterLayout) {
        this.canvas = canvas;
        this.ganttLayout = ganttLayout;
        this.parentDrawer = parentDrawer;
        lastVirtualCanvasWidth = canvas.getWidth();
        lastVirtualCanvasHeight = canvas.getHeight();
        lastVirtualViewPortY = 0;
        if (redrawAfterLayout)
            ganttLayout.addOnAfterLayout(this::redrawCanvas);
    }

    public double getParentHeight() {
        return parentHeight;
    }

    public void setParentHeight(double parentHeight) {
        this.parentHeight = parentHeight;
    }

    void refreshCanvas(double virtualCanvasWidth, double virtualCanvasHeight, double virtualViewPortY, boolean canvasSizeChanged) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, Math.min(canvas.getWidth(), virtualCanvasWidth), canvas.getHeight());
        ChildPosition<Temporal> p = new ChildPosition<>();
        p.setX(0);
        p.setWidth(virtualCanvasWidth);
        p.setHeight(parentHeight);
        p.setY(-virtualViewPortY);
        for (Object parent : ganttLayout.getParents()) {
            parentDrawer.drawChild((P) parent, p, gc);
            p.setY(p.getY() + parentHeight);
        }
        lastVirtualCanvasWidth = virtualCanvasWidth;
        lastVirtualCanvasHeight = virtualCanvasHeight;
        lastVirtualViewPortY = virtualViewPortY;
    }

    private void redrawCanvas() {
        refreshCanvas(lastVirtualCanvasWidth, lastVirtualCanvasHeight, lastVirtualViewPortY, false);
    }

}
