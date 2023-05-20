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
    private boolean draggingGrandparentSlider;
    private boolean draggingParentSlider;

    public ParentsCanvasInteractionHandler(GanttLayoutImpl<?, ?> ganttLayout, ParentsCanvasDrawer parentsCanvasDrawer) {
        this.ganttLayout = ganttLayout;
        this.parentsCanvasDrawer = parentsCanvasDrawer;
    }

    private boolean isHoveringGrandparentSlider(MouseEvent e, Canvas canvas) {
        if (!ganttLayout.isGrandparentHeaderOnLeftOrRight())
            return false;
        double sliderX;
        if (ganttLayout.isGrandparentHeaderOnRight())
            sliderX = ganttLayout.getGrandparentHeaderMinX();
        else { // LEFT
            sliderX = ganttLayout.getGrandparentHeaderMaxX();
            sliderX = Math.min(sliderX, canvas.getWidth() - 16); // 16px = because of possible presence of vertical scrollbar
        }
        return (Math.abs(e.getX() - sliderX) < 10);
    }

    private boolean isHoveringParentSlider(MouseEvent e, Canvas canvas) {
        // The slider is a virtual vertical bar separating parents and children, so located at x = parent maxX.
        double sliderX = ganttLayout.getParentHeaderMaxX();
        // If the slider is at the right end of the canvas or even further (this can happen when reducing the window
        // size), we still offer the possibility to slide it back to left.
        sliderX = Math.min(sliderX, canvas.getWidth() - 16); // 16px = because of possible presence of vertical scrollbar
        // We consider the mouse is hovering it when closer than 10px from it,
        return (Math.abs(e.getX() - sliderX) < 10)
                // except if it is hovering a grandparent row on top or bottom
                && !(ganttLayout.isGrandparentHeaderOnTopOrBottom() && isHoveringGrandparentHeader(e));
    }

    private boolean isHoveringGrandparentHeader(MouseEvent e) {
        return ganttLayout.getGrandparentRows().stream().anyMatch(gr -> gr.getHeader().contains(0, e.getY() + parentsCanvasDrawer.getLastVirtualViewPortY()));
    }

    public boolean handleMouseMoved(MouseEvent e, Canvas canvas) {
        if (isHoveringGrandparentSlider(e, canvas) || isHoveringParentSlider(e, canvas)) {
            canvas.setCursor(Cursor.H_RESIZE);
            return false; // Stopping propagation (we don't want the cursor to be changed again by another handler)
        }
        return true; // Ok to continue propagation
    }

    @Override
    public boolean handleMousePressed(MouseEvent e, Canvas canvas) {
        if (isHoveringGrandparentSlider(e, canvas)) { // Indicates that the user pressed the slider bar, and therefore wants to drag it
            draggingGrandparentSlider = true;
            return false; // Stopping propagation
        }
        if (isHoveringParentSlider(e, canvas)) { // Indicates that the user pressed the slider bar, and therefore wants to drag it
            draggingParentSlider = true;
            return false; // Stopping propagation
        }
        return true; // Otherwise ok to continue propagation
    }


    @Override
    public boolean handleMouseDragged(MouseEvent e, Canvas canvas) {
        if (draggingGrandparentSlider) { // Indicates that this mouse drag is for the slider
            double grandparentWidth;
            if (ganttLayout.isGrandparentHeaderOnLeft())
                grandparentWidth = e.getX() - ganttLayout.getGrandparentHeaderMinX();
            else
                grandparentWidth = ganttLayout.getGrandparentHeaderMaxX() - e.getX();
            grandparentWidth = Math.max(0, Math.min(grandparentWidth, canvas.getWidth()));
            ganttLayout.setGrandparentHeaderWidth(grandparentWidth); // Will cause a quick layout pass + canvas refresh
            return false; // Stopping propagation
        }
        if (draggingParentSlider) { // Indicates that this mouse drag is for the slider
            double parentWidth = Math.max(0, e.getX() - ganttLayout.getParentHeaderMinX());
            parentWidth = Math.min(parentWidth, canvas.getWidth());
            ganttLayout.setParentHeaderWidth(parentWidth); // Will cause a quick layout pass + canvas refresh
            return false; // Stopping propagation
        }
        return true; // Otherwise ok to continue propagation
    }

    @Override
    public boolean handleMouseClicked(MouseEvent e, Canvas canvas) {
        if (draggingGrandparentSlider || draggingParentSlider) { // Indicates that this mouse click is the end of this slider drag
            draggingGrandparentSlider = draggingParentSlider = false;
            return false; // Stopping propagation
        }
        return true; // Otherwise ok to continue propagation
    }
}
