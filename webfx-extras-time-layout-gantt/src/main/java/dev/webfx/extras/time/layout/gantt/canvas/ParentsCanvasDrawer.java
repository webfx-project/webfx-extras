package dev.webfx.extras.time.layout.gantt.canvas;

import dev.webfx.extras.canvas.CanvasDrawer;
import dev.webfx.extras.canvas.layer.ChildDrawer;
import dev.webfx.extras.canvas.layer.interact.CanvasInteractionManager;
import dev.webfx.extras.canvas.layer.interact.HasCanvasInteractionManager;
import dev.webfx.extras.geometry.MutableBounds;
import dev.webfx.extras.time.layout.impl.TimeLayoutUtil;
import dev.webfx.extras.time.layout.gantt.impl.GanttLayoutImpl;
import dev.webfx.extras.time.layout.gantt.impl.GrandparentRow;
import dev.webfx.extras.time.layout.gantt.impl.ParentRow;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.List;

/**
 * @author Bruno Salmon
 */
public final class ParentsCanvasDrawer {

    private final Canvas canvas;
    private final GanttLayoutImpl<?, ? extends Temporal> ganttLayout;
    private ChildDrawer<Object> parentDrawer;
    private ChildDrawer<Object> grandparentDrawer;
    private Paint horizontalStroke;
    private Paint verticalStroke;
    private double lastVirtualCanvasWidth, lastVirtualViewPortY;
    private Bounds drawingArea;

    public ParentsCanvasDrawer(GanttLayoutImpl<?, ? extends Temporal> ganttLayout, CanvasDrawer childrenDrawer) {
        this(ganttLayout, childrenDrawer, null);
    }

    public <P> ParentsCanvasDrawer(GanttLayoutImpl<?, ? extends Temporal> ganttLayout, CanvasDrawer childrenDrawer, ChildDrawer<P> parentDrawer) {
        this(ganttLayout, childrenDrawer, parentDrawer, null);
    }

    public <P, G> ParentsCanvasDrawer(GanttLayoutImpl<?, ? extends Temporal> ganttLayout, CanvasDrawer childrenDrawer, ChildDrawer<P> parentDrawer, ChildDrawer<G> grandparentDrawer) {
        this(ganttLayout, childrenDrawer.getCanvas(), childrenDrawer, parentDrawer, grandparentDrawer);
    }

    public ParentsCanvasDrawer(GanttLayoutImpl<?, ? extends Temporal> ganttLayout, Canvas canvas) {
        this(ganttLayout, canvas, null);
    }

    public <P> ParentsCanvasDrawer(GanttLayoutImpl<?, ? extends Temporal> ganttLayout, Canvas canvas, ChildDrawer<P> parentDrawer) {
        this(ganttLayout, canvas, parentDrawer, null);
    }

    public <P, G> ParentsCanvasDrawer(GanttLayoutImpl<?, ? extends Temporal> ganttLayout, Canvas canvas, ChildDrawer<P> parentDrawer, ChildDrawer<G> grandparentDrawer) {
        this(ganttLayout, canvas, null, parentDrawer, grandparentDrawer);
    }

    private <P, G> ParentsCanvasDrawer(GanttLayoutImpl<?, ? extends Temporal> ganttLayout, Canvas canvas, CanvasDrawer childrenDrawer, ChildDrawer<P> parentDrawer, ChildDrawer<G> grandparentDrawer) {
        this.ganttLayout = ganttLayout;
        this.parentDrawer = (ChildDrawer<Object>) parentDrawer;
        this.grandparentDrawer = (ChildDrawer<Object>) grandparentDrawer;
        this.canvas = canvas;
        lastVirtualCanvasWidth = canvas.getWidth();
        lastVirtualViewPortY = 0;
        if (childrenDrawer != null) {
            childrenDrawer.addOnAfterDraw(() ->
                refreshCanvas(ganttLayout.getParentWidth(), childrenDrawer.getCanvas().getHeight(), childrenDrawer.getLayoutOriginY(), false)
            );
            if (childrenDrawer instanceof HasCanvasInteractionManager) {
                CanvasInteractionManager canvasInteractionManager = ((HasCanvasInteractionManager) childrenDrawer).getCanvasInteractionManager();
                canvasInteractionManager.addHandler(new ParentsCanvasInteractionHandler(ganttLayout, childrenDrawer, this), true);
            }
        }
    }

    public <P> ParentsCanvasDrawer setParentDrawer(ChildDrawer<P> parentDrawer) {
        this.parentDrawer = (ChildDrawer<Object>) parentDrawer;
        return this;
    }

    public <G> ParentsCanvasDrawer setGrandparentDrawer(ChildDrawer<G> grandparentDrawer) {
        this.grandparentDrawer = (ChildDrawer<Object>) grandparentDrawer;
        return this;
    }

    public ParentsCanvasDrawer setParentWidth(double parentWidth) {
        ganttLayout.setParentWidth(parentWidth);
        return this;
    }

    public ParentsCanvasDrawer setHorizontalStroke(Paint horizontalStroke) {
        this.horizontalStroke = horizontalStroke;
        return this;
    }

    public ParentsCanvasDrawer setVerticalStroke(Paint verticalStroke) {
        this.verticalStroke = verticalStroke;
        return this;
    }

    double getLastVirtualViewPortY() {
        return lastVirtualViewPortY;
    }

    void refreshCanvas(double virtualCanvasWidth, double virtualCanvasHeight, double virtualViewPortY, boolean canvasSizeChanged) {
        lastVirtualCanvasWidth = virtualCanvasWidth;
        lastVirtualViewPortY = virtualViewPortY;
        drawingArea = new BoundingBox(0, 0, Math.min(canvas.getWidth(), virtualCanvasWidth), canvas.getHeight());
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(drawingArea.getMinX(), drawingArea.getMinY(), drawingArea.getWidth(), drawingArea.getHeight());
        List<GrandparentRow> grandparentRows = ganttLayout.getGrandparentRows();
        if (!grandparentRows.isEmpty())
            drawGrandparentAndParentRows(grandparentRows, gc);
        else
            drawParentRows(ganttLayout.getParentRows(), gc);
    }

    private void drawGrandparentAndParentRows(List<GrandparentRow> grandparentRows, GraphicsContext gc) {
        TimeLayoutUtil.processVisibleChildrenLayoutBounds(
                grandparentRows,
                true, false, drawingArea, 0, lastVirtualViewPortY,
                (grandparentRow, b) -> drawGrandparentRow(grandparentRow, gc)
        );
    }

    private void drawGrandparentRow(GrandparentRow grandparentRow, GraphicsContext gc) {
        MutableBounds gp = grandparentRow.getHeadRow();
        gp.setWidth(lastVirtualCanvasWidth);
        double gy = gp.getY();
        gp.setY(gy - lastVirtualViewPortY);
        grandparentDrawer.drawChild(grandparentRow.getGrandparent(), gp, gc);
        gp.setY(gy);
        drawParentRows(grandparentRow.getParentRows(), gc);
    }

    private <C> void drawParentRows(List<ParentRow<C>> parentRows, GraphicsContext gc) {
        TimeLayoutUtil.processVisibleChildrenLayoutBounds(
                parentRows,
                true, true, drawingArea, 0, lastVirtualViewPortY,
                (parentRow, b) -> drawParentRow(parentRow, gc));
    }

    private void drawParentRow(ParentRow<?> parentRow, GraphicsContext gc) {
        if (horizontalStroke != null) {
            gc.setStroke(horizontalStroke);
            gc.setLineWidth(1);
            gc.strokeLine(0, parentRow.getMinY(), gc.getCanvas().getWidth(), parentRow.getMinY());
            gc.strokeLine(0, parentRow.getMaxY(), gc.getCanvas().getWidth(), parentRow.getMaxY());
        }
        if (verticalStroke != null) {
            drawVerticalStrokes(ganttLayout, parentRow, gc);
        }
        parentRow.setWidth(lastVirtualCanvasWidth);
        parentDrawer.drawChild(parentRow.getParent(), parentRow, gc);
    }

    private <T extends Temporal> void drawVerticalStrokes(GanttLayoutImpl<?, T> ganttLayout, MutableBounds b, GraphicsContext gc) {
        gc.setStroke(verticalStroke);
        gc.setLineWidth(1);
        T t0 = ganttLayout.getTimeWindowStart();
        while (t0.until(ganttLayout.getTimeWindowEnd(), ChronoUnit.DAYS) >= 0) {
            double x0 = ganttLayout.getTimeProjector().timeToX(t0, true, false);
            gc.strokeLine(x0, b.getMinY(), x0, b.getMaxY());
            t0 = (T) t0.plus(1, ChronoUnit.DAYS);
        }
    }
}
