package dev.webfx.extras.timelayout.gantt.canvas;

import dev.webfx.extras.timelayout.LayoutPosition;
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
    private final ChildDrawer<Object, Temporal> grandparentDrawer;
    private double lastVirtualCanvasWidth, lastVirtualCanvasHeight, lastVirtualViewPortY;

    public ParentsCanvasRefresher(Canvas canvas, GanttLayout<?, ?> ganttLayout, ChildDrawer<?, Temporal> parentDrawer) {
        this(canvas, ganttLayout, parentDrawer, null);
    }

    public ParentsCanvasRefresher(Canvas canvas, GanttLayout<?, ?> ganttLayout, ChildDrawer<?, Temporal> parentDrawer, ChildDrawer<?, Temporal> grandparentDrawer) {
        this(canvas, ganttLayout, parentDrawer, grandparentDrawer, true);
    }

    public ParentsCanvasRefresher(Canvas canvas, GanttLayout<?, ?> ganttLayout, ChildDrawer<?, Temporal> parentDrawer, boolean redrawAfterLayout) {
        this(canvas, ganttLayout, parentDrawer, null, redrawAfterLayout);
    }

    public ParentsCanvasRefresher(Canvas canvas, GanttLayout<?, ?> ganttLayout, ChildDrawer<?, Temporal> parentDrawer, ChildDrawer<?, Temporal> grandparentDrawer, boolean redrawAfterLayout) {
        this.canvas = canvas;
        this.ganttLayout = ganttLayout;
        this.parentDrawer = (ChildDrawer<Object, Temporal>) parentDrawer;
        this.grandparentDrawer = (ChildDrawer<Object, Temporal>) grandparentDrawer;
        lastVirtualCanvasWidth = canvas.getWidth();
        lastVirtualCanvasHeight = canvas.getHeight();
        lastVirtualViewPortY = 0;
        if (redrawAfterLayout)
            ganttLayout.addOnAfterLayout(this::redrawCanvas);
    }

    void refreshCanvas(double virtualCanvasWidth, double virtualCanvasHeight, double virtualViewPortY, boolean canvasSizeChanged) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, Math.min(canvas.getWidth(), virtualCanvasWidth), canvas.getHeight());
        LayoutPosition gpp = new LayoutPosition(); // Grandparent position
        gpp.setX(0);
        gpp.setWidth(virtualCanvasWidth);
        gpp.setHeight(ganttLayout.grandparentHeight);
        Object lastGrandparent = null;
        for (ParentRow<?, ?> parentRow : ganttLayout.getParentRows()) {
            LayoutPosition pp = parentRow.getRowPosition();
            double py = pp.getY();
            Object grandparent = parentRow.getGrandparent();
            if (grandparent != lastGrandparent) {
                if (grandparentDrawer != null) {
                    gpp.setY(py - gpp.getHeight() - virtualViewPortY);
                    grandparentDrawer.drawChild(grandparent, gpp, gc);
                }
                lastGrandparent = grandparent;
            }
            pp.setWidth(virtualCanvasWidth);
            pp.setY(py - virtualViewPortY);
            parentDrawer.drawChild(parentRow.getParent(), pp, gc);
            pp.setY(py);
        }
        lastVirtualCanvasWidth = virtualCanvasWidth;
        lastVirtualCanvasHeight = virtualCanvasHeight;
        lastVirtualViewPortY = virtualViewPortY;
    }

    private void redrawCanvas() {
        refreshCanvas(lastVirtualCanvasWidth, lastVirtualCanvasHeight, lastVirtualViewPortY, false);
    }

}
