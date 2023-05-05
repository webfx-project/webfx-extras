package dev.webfx.extras.timelayout.gantt.canvas;

import dev.webfx.extras.timelayout.LayoutPosition;
import dev.webfx.extras.timelayout.canvas.ChildDrawer;
import dev.webfx.extras.timelayout.canvas.TimeCanvasDrawer;
import dev.webfx.extras.timelayout.gantt.GanttLayout;
import dev.webfx.extras.timelayout.gantt.GrandparentRow;
import dev.webfx.extras.timelayout.gantt.ParentRow;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.List;

/**
 * @author Bruno Salmon
 */
public final class ParentsCanvasRefresher {

    private final Canvas canvas;
    private final GanttLayout<?, ?> ganttLayout;
    private final ChildDrawer<Object> parentDrawer;
    private final ChildDrawer<Object> grandparentDrawer;
    private double lastVirtualCanvasWidth, lastVirtualCanvasHeight, lastVirtualViewPortY;
    private Bounds drawingArea;

    public ParentsCanvasRefresher(Canvas canvas, GanttLayout<?, ?> ganttLayout, ChildDrawer<?> parentDrawer) {
        this(canvas, ganttLayout, parentDrawer, null);
    }

    public ParentsCanvasRefresher(Canvas canvas, GanttLayout<?, ?> ganttLayout, ChildDrawer<?> parentDrawer, ChildDrawer<?> grandparentDrawer) {
        this(canvas, ganttLayout, parentDrawer, grandparentDrawer, true);
    }

    public ParentsCanvasRefresher(Canvas canvas, GanttLayout<?, ?> ganttLayout, ChildDrawer<?> parentDrawer, boolean redrawAfterLayout) {
        this(canvas, ganttLayout, parentDrawer, null, redrawAfterLayout);
    }

    public ParentsCanvasRefresher(Canvas canvas, GanttLayout<?, ?> ganttLayout, ChildDrawer<?> parentDrawer, ChildDrawer<?> grandparentDrawer, boolean redrawAfterLayout) {
        this.canvas = canvas;
        this.ganttLayout = ganttLayout;
        this.parentDrawer = (ChildDrawer<Object>) parentDrawer;
        this.grandparentDrawer = (ChildDrawer<Object>) grandparentDrawer;
        lastVirtualCanvasWidth = canvas.getWidth();
        lastVirtualCanvasHeight = canvas.getHeight();
        lastVirtualViewPortY = 0;
        if (redrawAfterLayout)
            ganttLayout.addOnAfterLayout(this::redrawCanvas);
    }

    void refreshCanvas(double virtualCanvasWidth, double virtualCanvasHeight, double virtualViewPortY, boolean canvasSizeChanged) {
        lastVirtualCanvasWidth = virtualCanvasWidth;
        lastVirtualCanvasHeight = virtualCanvasHeight;
        lastVirtualViewPortY = virtualViewPortY;
        drawingArea = new BoundingBox(0, 0, Math.min(canvas.getWidth(), virtualCanvasWidth), canvas.getHeight());
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(drawingArea.getMinX(), drawingArea.getMinY(), drawingArea.getWidth(), drawingArea.getHeight());
        if (grandparentDrawer == null)
            drawParentRows(ganttLayout.getParentRows(), gc);
        else {
            for (GrandparentRow grandparentRow : ganttLayout.getGrandparentRows()) {
                LayoutPosition p = grandparentRow.getRowPosition();
                p.setWidth(virtualCanvasWidth);
                double gy = p.getY();
                p.setY(gy - virtualViewPortY);
                grandparentDrawer.drawChild(grandparentRow.getGrandparent(), p, gc);
                p.setY(gy);
                drawParentRows(grandparentRow.getParentRows(), gc);
            }
        }
    }

    private void drawParentRows(List<? extends ParentRow<?, ?>> parentRows, GraphicsContext gc) {
        TimeCanvasDrawer.drawVisibleChildren(
                (List<ParentRow<?, ?>>) parentRows,
                i -> getParentPosition(parentRows, i),
                drawingArea, 0, lastVirtualViewPortY,
                (parentRow, p, gc1) -> parentDrawer.drawChild(parentRow.getParent(), p, gc1), gc);
    }

    private LayoutPosition getParentPosition(List<? extends ParentRow<?, ?>> parentRows, int i) {
        LayoutPosition p = parentRows.get(i).getRowPosition();
        p.setWidth(lastVirtualCanvasWidth);
        return p;
    }

    private void redrawCanvas() {
        refreshCanvas(lastVirtualCanvasWidth, lastVirtualCanvasHeight, lastVirtualViewPortY, false);
    }

}
