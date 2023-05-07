package dev.webfx.extras.time.layout.gantt.canvas;

import dev.webfx.extras.canvas.CanvasDrawer;
import dev.webfx.extras.canvas.layer.interact.CanvasInteractionHandler;
import dev.webfx.extras.time.layout.gantt.GanttLayout;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;

/**
 * @author Bruno Salmon
 */
public class ParentsCanvasInteractionHandler implements CanvasInteractionHandler {

    private final GanttLayout<?, ?> ganttLayout;
    private final CanvasDrawer childrenDrawer;
    private final ParentsCanvasRefresher parentsCanvasRefresher;
    private boolean draggingSlider;

    public ParentsCanvasInteractionHandler(GanttLayout<?, ?> ganttLayout, CanvasDrawer childrenDrawer, ParentsCanvasRefresher parentsCanvasRefresher) {
        this.ganttLayout = ganttLayout;
        this.childrenDrawer = childrenDrawer;
        this.parentsCanvasRefresher = parentsCanvasRefresher;
    }

    private boolean isHoveringSlider(MouseEvent e, Canvas canvas) {
        // The slider is a virtual vertical bar separating parents and children, so located at x = parent width.
        double sliderX = ganttLayout.getParentWidth();
        // If the slider is at the right end of the canvas or even further (this can happen when reducing the window
        // size), we still offer the possibility to slide it back to left.
        sliderX = Math.min(sliderX, canvas.getWidth() - 16); // 16px = because of possible presence of vertical scrollbar
        // We consider the mouse is hovering it when closer than 10px from it,
        return (Math.abs(e.getX() - sliderX) < 10)
                // except if it is hovering a grandparent row
                && !isHoveringGrandparent(e);
    }

    private boolean isHoveringGrandparent(MouseEvent e) {
        return ganttLayout.getGrandparentRows().stream().anyMatch(gr -> gr.getRowPosition().contains(0, e.getY() + parentsCanvasRefresher.getLastVirtualViewPortY()));
    }

    public boolean handleMouseMoved(MouseEvent e, Canvas canvas) {
        if (isHoveringSlider(e, canvas)) {
            canvas.setCursor(Cursor.H_RESIZE);
            return false; // Stopping propagation (we don't want the cursor to be changed again by another handler)
        }
        return true; // Ok to continue propagation
    }

    @Override
    public boolean handleMousePressed(MouseEvent e, Canvas canvas) {
        if (isHoveringSlider(e, canvas)) { // Indicates that the user pressed the slider bar, and therefore wants to drag it
            draggingSlider = true;
            return false; // Stopping propagation
        }
        return true; // Otherwise ok to continue propagation
    }


    @Override
    public boolean handleMouseDragged(MouseEvent e, Canvas canvas) {
        if (draggingSlider) { // Indicates that this mouse drag is for the slider
            double parentWidth = Math.max(0, e.getX());
            parentWidth = Math.min(parentWidth, canvas.getWidth());
            ganttLayout.setParentWidth(parentWidth);
            childrenDrawer.markDrawAreaAsDirty();
            return false; // Stopping propagation
        }
        return true; // Otherwise ok to continue propagation
    }

    @Override
    public boolean handleMouseClicked(MouseEvent e, Canvas canvas) {
        if (draggingSlider) { // Indicates that this mouse click is the end of this slider drag
            draggingSlider = false;
            return false; // Stopping propagation
        }
        return true; // Otherwise ok to continue propagation
    }
}
