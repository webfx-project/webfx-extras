package dev.webfx.extras.time.layout.gantt.canvas;

import dev.webfx.extras.time.layout.LayoutBounds;
import dev.webfx.extras.canvas.layer.ChildDrawer;
import dev.webfx.extras.time.layout.gantt.GrandparentRow;
import dev.webfx.extras.time.layout.gantt.ParentRow;
import dev.webfx.extras.time.layout.canvas.TimeCanvasDrawer;
import dev.webfx.extras.time.layout.gantt.GanttLayout;
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
                LayoutBounds gp = grandparentRow.getRowPosition();
                gp.setWidth(virtualCanvasWidth);
                double gy = gp.getY();
                gp.setY(gy - virtualViewPortY);
                grandparentDrawer.drawChild(grandparentRow.getGrandparent(), gp, gc);
                gp.setY(gy);
                drawParentRows(grandparentRow.getParentRows(), gc);
            }
        }
    }

    private void drawParentRows(List<? extends ParentRow<?, ?>> parentRows, GraphicsContext gc) {
        TimeCanvasDrawer.drawVisibleChildren(
                (List<ParentRow<?, ?>>) parentRows,
                i -> getParentRowPosition(parentRows, i),
                drawingArea, 0, lastVirtualViewPortY,
                (parentRow, b, gc2) -> parentDrawer.drawChild(parentRow.getParent(), b, gc2)
                , gc);
    }

    private LayoutBounds getParentRowPosition(List<? extends ParentRow<?, ?>> parentRows, int i) {
        LayoutBounds pp = parentRows.get(i).getRowPosition();
        pp.setWidth(lastVirtualCanvasWidth);
        return pp;
    }

    private void redrawCanvas() {
        refreshCanvas(lastVirtualCanvasWidth, lastVirtualCanvasHeight, lastVirtualViewPortY, false);
    }

    double getLastVirtualViewPortY() {
        return lastVirtualViewPortY;
    }
}
