package dev.webfx.extras.timelayout.gantt.canvas;

import dev.webfx.extras.timelayout.ChildPosition;
import dev.webfx.extras.timelayout.canvas.ChildDrawer;
import dev.webfx.extras.timelayout.gantt.GanttLayout;
import dev.webfx.extras.timelayout.gantt.ParentRow;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.time.temporal.Temporal;

/**
 * @author Bruno Salmon
 */
public final class ParentsCanvasRefresher {

    private final Canvas canvas;
    private final GanttLayout<?, ?> ganttLayout;
    private final ChildDrawer<Object, Temporal> parentDrawer;
    private final ChildDrawer<Object, Temporal> grandParentDrawer;
    private double lastVirtualCanvasWidth, lastVirtualCanvasHeight, lastVirtualViewPortY;

    public ParentsCanvasRefresher(Canvas canvas, GanttLayout<?, ?> ganttLayout, ChildDrawer<?, Temporal> parentDrawer) {
        this(canvas, ganttLayout, parentDrawer, null);
    }

    public ParentsCanvasRefresher(Canvas canvas, GanttLayout<?, ?> ganttLayout, ChildDrawer<?, Temporal> parentDrawer, ChildDrawer<?, Temporal> grandParentDrawer) {
        this(canvas, ganttLayout, parentDrawer, grandParentDrawer, true);
    }

    public ParentsCanvasRefresher(Canvas canvas, GanttLayout<?, ?> ganttLayout, ChildDrawer<?, Temporal> parentDrawer, boolean redrawAfterLayout) {
        this(canvas, ganttLayout, parentDrawer, null, redrawAfterLayout);
    }

    public ParentsCanvasRefresher(Canvas canvas, GanttLayout<?, ?> ganttLayout, ChildDrawer<?, Temporal> parentDrawer, ChildDrawer<?, Temporal> grandParentDrawer, boolean redrawAfterLayout) {
        this.canvas = canvas;
        this.ganttLayout = ganttLayout;
        this.parentDrawer = (ChildDrawer<Object, Temporal>) parentDrawer;
        this.grandParentDrawer = (ChildDrawer<Object, Temporal>) grandParentDrawer;
        lastVirtualCanvasWidth = canvas.getWidth();
        lastVirtualCanvasHeight = canvas.getHeight();
        lastVirtualViewPortY = 0;
        if (redrawAfterLayout)
            ganttLayout.addOnAfterLayout(this::redrawCanvas);
    }

    void refreshCanvas(double virtualCanvasWidth, double virtualCanvasHeight, double virtualViewPortY, boolean canvasSizeChanged) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, Math.min(canvas.getWidth(), virtualCanvasWidth), canvas.getHeight());
        ChildPosition p = new ChildPosition();
        p.setX(0);
        p.setY(-virtualViewPortY);
        p.setWidth(virtualCanvasWidth);
        Object lastGrandParent = null;
        for (ParentRow<?, ?> parentRow : ganttLayout.getParentRows()) {
            Object grandParent = parentRow.getGrandParent();
            if (grandParent != lastGrandParent) {
                if (grandParentDrawer != null) {
                    p.setY(p.getY() + p.getHeight());
                    p.setHeight(ganttLayout.grandParentHeight);
                    grandParentDrawer.drawChild(grandParent, p, gc);
                }
                lastGrandParent = grandParent;
            }
            p.setY(parentRow.getY() - virtualViewPortY);
            p.setHeight(parentRow.getHeight());
            parentDrawer.drawChild(parentRow.getParent(), p, gc);
        }
        lastVirtualCanvasWidth = virtualCanvasWidth;
        lastVirtualCanvasHeight = virtualCanvasHeight;
        lastVirtualViewPortY = virtualViewPortY;
    }

    private void redrawCanvas() {
        refreshCanvas(lastVirtualCanvasWidth, lastVirtualCanvasHeight, lastVirtualViewPortY, false);
    }

}
