package dev.webfx.extras.time.layout.gantt.canvas;

import dev.webfx.extras.canvas.layer.interact.CanvasInteractionHandler;
import dev.webfx.extras.time.layout.gantt.impl.GanttLayoutImpl;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;

/**
 * @author Bruno Salmon
 */
public class ParentsCanvasInteractionHandler implements CanvasInteractionHandler {

    private final GanttLayoutImpl<?, ?> ganttLayout;
    private final ParentsCanvasDrawer parentsCanvasDrawer;
    private boolean draggingGrandparentHeaderSlider;
    private boolean draggingParentHeaderSlider;
    private boolean draggingChildRowHeaderSlider;

    public ParentsCanvasInteractionHandler(GanttLayoutImpl<?, ?> ganttLayout, ParentsCanvasDrawer parentsCanvasDrawer) {
        this.ganttLayout = ganttLayout;
        this.parentsCanvasDrawer = parentsCanvasDrawer;
    }

    private boolean isHoveringGrandparentHeaderSlider(MouseEvent e, Canvas canvas) {
        if (!ganttLayout.isGrandparentHeaderOnLeftOrRight())
            return false;
        return isHoveringSlider(e, grandparentHeaderBorderX(canvas, true, true));
    }

    private double grandparentHeaderBorderX(Canvas canvas, boolean sliderBorder, boolean adjustRight) {
        double x;
        if (ganttLayout.isGrandparentHeaderOnRight() == sliderBorder)
            x = ganttLayout.getGrandparentHeaderMinX();
        else { // LEFT
            x = ganttLayout.getGrandparentHeaderMaxX();
            if (adjustRight)
                x = adjustRight(x, canvas);
        }
        return x;
    }

    private boolean isHoveringSlider(MouseEvent e, double sliderX) {
        // We consider the mouse is hovering it when closer than 10px from it
        return (Math.abs(e.getX() - sliderX) < 10);
    }

    private boolean isHoveringParentHeaderSlider(MouseEvent e, Canvas canvas) {
        if (!ganttLayout.isParentHeaderOnLeftOrRight())
            return false;
        if (ganttLayout.isGrandparentHeaderOnTopOrBottom() && isHoveringGrandparentHeader(e))
            return false;
        return isHoveringSlider(e, parentHeaderBorderX(canvas, true, true));
    }

    private double parentHeaderBorderX(Canvas canvas, boolean sliderBorder, boolean adjustRight) {
        // The slider is a virtual vertical bar separating parents and children, so located at x = parent maxX.
        double x;
        if (ganttLayout.isParentHeaderOnRight() == sliderBorder)
            x = ganttLayout.getParentHeaderMinX();
        else { // LEFT
            x = ganttLayout.getParentHeaderMaxX();
            if (adjustRight)
                x = adjustRight(x, canvas);
        }
        return x;
    }

    private double adjustRight(double sliderX, Canvas canvas) {
        // If the slider is at the right end of the canvas or even further (this can happen when reducing the window
        // size), we still offer the possibility to slide it back to left.
        return Math.min(sliderX, canvas.getWidth() - 16); // 16px = because of possible presence of vertical scrollbar
    }

    private boolean isHoveringChildRowHeaderSlider(MouseEvent e, Canvas canvas) {
        if (!ganttLayout.isParentHeaderOnLeftOrRight())
            return false;
        if (ganttLayout.isGrandparentHeaderOnTopOrBottom() && isHoveringGrandparentHeader(e))
            return false;
        return isHoveringSlider(e, childRowHeaderBorderX(canvas, true, true));
    }

    private double childRowHeaderBorderX(Canvas canvas, boolean sliderBorder, boolean adjustRight) {
        double x = parentHeaderBorderX(canvas, true, false);
        if (sliderBorder) {
            double childRowHeaderWidth = parentsCanvasDrawer.getChildRowHeaderWidth();
            if (ganttLayout.isParentHeaderOnLeft())
                x += childRowHeaderWidth;
            else {
                x -= childRowHeaderWidth;
                if (adjustRight)
                    x = adjustRight(x, canvas);
            }
        }
        return x;
    }

    private boolean isHoveringGrandparentHeader(MouseEvent e) {
        return ganttLayout.getGrandparentRows().stream().anyMatch(gr -> gr.getHeader().contains(0, e.getY() + parentsCanvasDrawer.getLastVirtualViewPortY()));
    }

    public boolean handleMouseMoved(MouseEvent e, Canvas canvas) {
        if (isHoveringGrandparentHeaderSlider(e, canvas) || isHoveringParentHeaderSlider(e, canvas) || isHoveringChildRowHeaderSlider(e, canvas)) {
            canvas.setCursor(Cursor.H_RESIZE);
            return false; // Stopping propagation (we don't want the cursor to be changed again by another handler)
        }
        return true; // Ok to continue propagation
    }

    @Override
    public boolean handleMousePressed(MouseEvent e, Canvas canvas) {
        if (isHoveringGrandparentHeaderSlider(e, canvas)) { // Indicates that the user pressed the slider bar, and therefore wants to drag it
            draggingGrandparentHeaderSlider = true;
            return false; // Stopping propagation
        }
        if (isHoveringParentHeaderSlider(e, canvas)) { // Indicates that the user pressed the slider bar, and therefore wants to drag it
            draggingParentHeaderSlider = true;
            return false; // Stopping propagation
        }
        if (isHoveringChildRowHeaderSlider(e, canvas)) { // Indicates that the user pressed the slider bar, and therefore wants to drag it
            draggingChildRowHeaderSlider = true;
            return false; // Stopping propagation
        }
        return true; // Otherwise ok to continue propagation
    }


    @Override
    public boolean handleMouseDragged(MouseEvent e, Canvas canvas) {
        if (draggingGrandparentHeaderSlider) { // Indicates that this mouse drag is for the grandparent slider
            ganttLayout.setGrandparentHeaderWidth(grandparentHeaderDragWidth(e, canvas)); // Will cause a quick layout pass + canvas refresh
            return false; // Stopping propagation
        } else if (draggingParentHeaderSlider) { // Indicates that this mouse drag is for the parent slider
            ganttLayout.setParentHeaderWidth(parentHeaderDragWidth(e, canvas)); // Will cause a quick layout pass + canvas refresh
            return false; // Stopping propagation
        } else if (draggingChildRowHeaderSlider) { // Indicates that this mouse drag is for the child row header slider
            parentsCanvasDrawer.setChildRowHeaderWidth(childRowHeaderDragWidth(e, canvas));
            return false; // Stopping propagation
        }
        return true; // Otherwise ok to continue propagation
    }

    private double grandparentHeaderDragWidth(MouseEvent e, Canvas canvas) {
        double headerOppositeSliderX = grandparentHeaderBorderX(canvas, false, false);
        return headerDragWidth(headerOppositeSliderX, ganttLayout.isGrandparentHeaderOnLeft(), e, canvas);
    }

    private double headerDragWidth(double oppositeBorderX, boolean isOppositeOnLeft, MouseEvent e, Canvas canvas) {
        double dragWidth;
        if (isOppositeOnLeft)
            dragWidth = e.getX() - oppositeBorderX;
        else
            dragWidth = oppositeBorderX - e.getX();
        dragWidth = Math.max(0, Math.min(dragWidth, canvas.getWidth()));
        return  dragWidth;
    }

    private double parentHeaderDragWidth(MouseEvent e, Canvas canvas) {
        double headerOppositeSliderX = parentHeaderBorderX(canvas, false, false);
        double dragWidth = headerDragWidth(headerOppositeSliderX, ganttLayout.isParentHeaderOnLeft(), e, canvas);
        return dragWidth;
    }

    private double childRowHeaderDragWidth(MouseEvent e, Canvas canvas) {
        double headerOppositeSliderX = childRowHeaderBorderX(canvas, false, false);
        double dragWidth = headerDragWidth(headerOppositeSliderX, ganttLayout.isParentHeaderOnLeft(), e, canvas);
        return dragWidth;
    }

    @Override
    public boolean handleMouseClicked(MouseEvent e, Canvas canvas) {
        if (draggingGrandparentHeaderSlider || draggingParentHeaderSlider) { // Indicates that this mouse click is the end of this slider drag
            draggingGrandparentHeaderSlider = draggingParentHeaderSlider = false;
            return false; // Stopping propagation
        }
        return true; // Otherwise ok to continue propagation
    }
}
