package dev.webfx.extras.time.layout.gantt.canvas;

import dev.webfx.extras.canvas.CanvasDrawer;
import dev.webfx.extras.canvas.layer.ChildDrawer;
import dev.webfx.extras.canvas.layer.interact.CanvasInteractionManager;
import dev.webfx.extras.canvas.layer.interact.HasCanvasInteractionManager;
import dev.webfx.extras.geometry.Bounds;
import dev.webfx.extras.geometry.MutableBounds;
import dev.webfx.extras.time.layout.gantt.impl.GanttLayoutImpl;
import dev.webfx.extras.time.layout.gantt.impl.GrandparentRow;
import dev.webfx.extras.time.layout.gantt.impl.ParentRow;
import dev.webfx.extras.time.layout.impl.TimeLayoutUtil;
import javafx.geometry.BoundingBox;
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
    private ChildDrawer<Integer> childRowHeaderDrawer;
    private final MutableBounds childRowHeaderBounds = new MutableBounds();
    private Paint horizontalStroke;
    private boolean horizontalStrokeForeground = true;
    private Paint verticalStroke;
    private boolean verticalStrokeForeground = true;
    private double lastVirtualCanvasWidth, lastVirtualViewPortY;
    private Paint tetrisAreaFill;
    private javafx.geometry.Bounds drawingArea;

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
            childrenDrawer.addOnBeforeDraw(() ->
                    onBeforeChildrenDraw(ganttLayout.getParentWidth(), childrenDrawer.getLayoutOriginY())
            );
            childrenDrawer.addOnAfterDraw(() ->
                    onAfterChildrenDraw(ganttLayout.getParentWidth(), childrenDrawer.getLayoutOriginY())
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

    public ParentsCanvasDrawer setChildRowHeaderDrawer(ChildDrawer<Integer> childRowHeaderDrawer) {
        this.childRowHeaderDrawer = childRowHeaderDrawer;
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

    public ParentsCanvasDrawer setHorizontalStrokeForeground(boolean horizontalStrokeForeground) {
        this.horizontalStrokeForeground = horizontalStrokeForeground;
        return this;
    }

    public ParentsCanvasDrawer setHorizontalStroke(Paint horizontalStroke, boolean horizontalStrokeForeground) {
        return setHorizontalStroke(horizontalStroke).setHorizontalStrokeForeground(horizontalStrokeForeground);
    }

    public ParentsCanvasDrawer setVerticalStroke(Paint verticalStroke) {
        this.verticalStroke = verticalStroke;
        return this;
    }

    public ParentsCanvasDrawer setVerticalStrokeForeground(boolean verticalStrokeForeground) {
        this.verticalStrokeForeground = verticalStrokeForeground;
        return this;
    }

    public ParentsCanvasDrawer setVerticalStroke(Paint verticalStroke, boolean verticalStrokeForeground) {
        return setVerticalStroke(verticalStroke).setVerticalStrokeForeground(verticalStrokeForeground);
    }

    public ParentsCanvasDrawer setTetrisAreaFill(Paint tetrisAreaFill) {
        this.tetrisAreaFill = tetrisAreaFill;
        return this;
    }

    double getLastVirtualViewPortY() {
        return lastVirtualViewPortY;
    }

    // This method is called by (Virtual)CanvasPane (when this ParentsCanvasDrawer is in a slider - left side)
    // See GanttCanvasUtil.createParent(Virtual)CanvasPane()
    void refreshCanvas(double virtualCanvasWidth, double virtualCanvasHeight, double virtualViewPortY, boolean canvasSizeChanged) {
        onBeforeChildrenDraw(virtualCanvasWidth, virtualViewPortY);
        onAfterChildrenDraw(virtualCanvasWidth, virtualViewPortY);
    }

    private void onBeforeChildrenDraw(double virtualCanvasWidth, double virtualViewPortY) {
        drawAll(virtualCanvasWidth, virtualViewPortY, false);
    }

    private void onAfterChildrenDraw(double virtualCanvasWidth, double virtualViewPortY) {
        drawAll(virtualCanvasWidth, virtualViewPortY, true);
    }

    private void drawAll(double virtualCanvasWidth, double virtualViewPortY, boolean afterChildrenPass) {
        lastVirtualCanvasWidth = virtualCanvasWidth;
        lastVirtualViewPortY = virtualViewPortY;
        // The only thing we draw before the children are the horizontal & vertical strokes, if set to be drawn in the
        // background. If they are not set so, and we are in that background pass (ie before drawing children), we can
        // return immediately as there is nothing to draw in that pass (this prevents looping for nothing).
        if (!afterChildrenPass &&
                (horizontalStroke == null || horizontalStrokeForeground) &&
                (verticalStroke == null || verticalStrokeForeground) &&
                (!ganttLayout.isTetrisPacking() || tetrisAreaFill == null))
            return;
        drawingArea = new BoundingBox(0, 0, Math.min(canvas.getWidth(), virtualCanvasWidth), canvas.getHeight());
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(drawingArea.getMinX(), drawingArea.getMinY(), drawingArea.getWidth(), drawingArea.getHeight());
        // Translating the canvas to consider the effect of the virtual view port
        gc.save();
        gc.translate(0, -lastVirtualViewPortY);
        // Drawing all grandparent and parents and strokes
        List<GrandparentRow> grandparentRows = ganttLayout.getGrandparentRows();
        if (!grandparentRows.isEmpty())
            drawGrandparentsWithTheirParentsAndStrokes(grandparentRows, gc, afterChildrenPass);
        else
            drawParentsAndStrokes(ganttLayout.getParentRows(), gc, afterChildrenPass);
        // Restoring the canvas context (rolls back the translation)
        gc.restore();
    }

    private void drawGrandparentsWithTheirParentsAndStrokes(List<GrandparentRow> grandparentRows, GraphicsContext gc, boolean afterChildrenPass) {
        TimeLayoutUtil.processVisibleObjectBounds(
                grandparentRows,
                true, drawingArea, 0, lastVirtualViewPortY,
                (grandparentRow, b) -> drawGrandparentWithItsParentAndStrokes(grandparentRow, gc, afterChildrenPass)
        );
    }

    private void drawGrandparentWithItsParentAndStrokes(GrandparentRow grandparentRow, GraphicsContext gc, boolean afterChildrenPass) {
        if (afterChildrenPass) {
            MutableBounds gp = grandparentRow.getHeader();
            grandparentDrawer.drawChild(grandparentRow.getGrandparent(), gp, gc);
            /*if (verticalStroke != null*//* && verticalStrokeForeground == afterChildrenPass*//*) {
                drawVerticalStrokes(ganttLayout, gp, gc);
            }*/
        }
        drawParentsAndStrokes(grandparentRow.getParentRows(), gc, afterChildrenPass);
    }

    private <C> void drawParentsAndStrokes(List<ParentRow<C>> parentRows, GraphicsContext gc, boolean afterChildrenPass) {
        TimeLayoutUtil.processVisibleObjectBounds(
                parentRows,
                true, drawingArea, 0, lastVirtualViewPortY,
                (parentRow, b) -> drawParentAndStrokes(parentRow, gc, afterChildrenPass));
    }

    private void drawParentAndStrokes(ParentRow<?> parentRow, GraphicsContext gc, boolean afterChildrenPass) {
        if (!afterChildrenPass && ganttLayout.isTetrisPacking() && tetrisAreaFill != null) {
            gc.setFill(tetrisAreaFill);
            gc.fillRect(0, parentRow.getMinY(), canvas.getWidth(), parentRow.getHeight());
        }
        // We draw the strokes before the parent row (that may override them)
        if (horizontalStroke != null && horizontalStrokeForeground == afterChildrenPass) {
            drawHorizontalStrokes(parentRow, gc);
        }
        if (verticalStroke != null && verticalStrokeForeground == afterChildrenPass) {
            drawVerticalStrokes(ganttLayout, parentRow, gc);
        }
        // We draw the parent row only once, and it's after children
        if (afterChildrenPass) {
            parentRow.setWidth(lastVirtualCanvasWidth);
            parentDrawer.drawChild(parentRow.getParent(), parentRow, gc);
            if (childRowHeaderDrawer != null) {
                childRowHeaderBounds.setX(lastVirtualCanvasWidth / 2);
                childRowHeaderBounds.setY(parentRow.getY() + ganttLayout.getVSpacing());
                childRowHeaderBounds.setWidth(lastVirtualCanvasWidth / 2);
                childRowHeaderBounds.setHeight(ganttLayout.getChildFixedHeight());
                for (int i = 0; i < parentRow.getRowsCount(); i++) {
                    childRowHeaderDrawer.drawChild(i, childRowHeaderBounds, gc);
                    childRowHeaderBounds.setY(childRowHeaderBounds.getY() + childRowHeaderBounds.getHeight() + ganttLayout.getVSpacing());
                }
            }
        }
    }

    private void drawHorizontalStrokes(Bounds b, GraphicsContext gc) {
        gc.setStroke(horizontalStroke);
        gc.setLineWidth(1);
        gc.strokeLine(0, b.getMinY(), gc.getCanvas().getWidth(), b.getMinY());
        gc.strokeLine(0, b.getMaxY(), gc.getCanvas().getWidth(), b.getMaxY());
    }

    private <T extends Temporal> void drawVerticalStrokes(GanttLayoutImpl<?, T> ganttLayout, Bounds b, GraphicsContext gc) {
        gc.setStroke(verticalStroke);
        gc.setLineWidth(1);
        T t0 = ganttLayout.getTimeWindowStart();
        while (t0.until(ganttLayout.getTimeWindowEnd(), ChronoUnit.DAYS) >= 0) {
            double x0 = ganttLayout.getTimeProjector().timeToX(t0, true, false);
            //if (x0 > ganttLayout.getParentWidth())
                gc.strokeLine(x0, b.getMinY(), x0, b.getMaxY());
            t0 = (T) t0.plus(1, ChronoUnit.DAYS);
        }
    }

    // static method

    public static ParentsCanvasDrawer create(GanttLayoutImpl<?, ? extends Temporal> ganttLayout, CanvasDrawer childrenDrawer) {
        return new ParentsCanvasDrawer(ganttLayout, childrenDrawer, null);
    }

    public static <P> ParentsCanvasDrawer create(GanttLayoutImpl<?, ? extends Temporal> ganttLayout, CanvasDrawer childrenDrawer, ChildDrawer<P> parentDrawer) {
        return new ParentsCanvasDrawer(ganttLayout, childrenDrawer, parentDrawer, null);
    }

    public static <P, G> ParentsCanvasDrawer create(GanttLayoutImpl<?, ? extends Temporal> ganttLayout, CanvasDrawer childrenDrawer, ChildDrawer<P> parentDrawer, ChildDrawer<G> grandparentDrawer) {
        return new ParentsCanvasDrawer(ganttLayout, childrenDrawer.getCanvas(), childrenDrawer, parentDrawer, grandparentDrawer);
    }

    public static ParentsCanvasDrawer create(GanttLayoutImpl<?, ? extends Temporal> ganttLayout, Canvas canvas) {
        return new ParentsCanvasDrawer(ganttLayout, canvas, null);
    }

    public static <P> ParentsCanvasDrawer create(GanttLayoutImpl<?, ? extends Temporal> ganttLayout, Canvas canvas, ChildDrawer<P> parentDrawer) {
        return new ParentsCanvasDrawer(ganttLayout, canvas, parentDrawer, null);
    }

    public static <P, G> ParentsCanvasDrawer create(GanttLayoutImpl<?, ? extends Temporal> ganttLayout, Canvas canvas, ChildDrawer<P> parentDrawer, ChildDrawer<G> grandparentDrawer) {
        return new ParentsCanvasDrawer(ganttLayout, canvas, null, parentDrawer, grandparentDrawer);
    }
}
