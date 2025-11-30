package dev.webfx.extras.time.layout.gantt.canvas;

import dev.webfx.extras.canvas.layer.interact.CanvasInteractionHandler;
import dev.webfx.extras.geometry.Bounds;
import dev.webfx.extras.geometry.MutableBounds;
import dev.webfx.extras.time.layout.gantt.impl.EnclosingRow;
import dev.webfx.extras.time.layout.gantt.impl.GanttLayoutImpl;
import dev.webfx.extras.time.layout.gantt.impl.ParentRow;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;

import java.util.List;

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

    public boolean handleMouseMoved(MouseEvent e, Canvas canvas) {
        // Showing a horizontal resize cursor when hovering a slider (grandparent, parent or child row header)
        if (isHoveringGrandparentHeaderSlider(e, canvas)
                || isHoveringParentHeaderSlider(e, canvas)
                || isHoveringChildRowHeader(e, canvas, true)) {
            canvas.setCursor(Cursor.H_RESIZE);
            return false; // Stopping propagation (we don't want the cursor to be changed again by another handler)
        }
        // Showing a hand cursor when hovering a clickable header (it's clickable when a click handler is set on the header)
        if (parentsCanvasDrawer.getChildRowHeaderClickHandler() != null && isHoveringChildRowHeader(e, canvas, false)) {
            canvas.setCursor(Cursor.HAND);
            return false;
        }
        // Showing a default cursor when hovering a header (grandparent, parent or child row header)
        if (isHoveringChildRowHeader(e, canvas, false) || isHoveringParentHeader(e) || isHoveringGrandparentHeader(e)) {
            canvas.setCursor(Cursor.DEFAULT);
            return false;
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
        if (isHoveringChildRowHeader(e, canvas, true)) { // Indicates that the user pressed the slider bar, and therefore wants to drag it
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
        return !isHoveringParentHeader(e) && !isHoveringGrandparentHeader(e); // Otherwise ok to continue propagation
    }

    @Override
    public boolean handleMouseClicked(MouseEvent e, Canvas canvas) {
        if (draggingGrandparentHeaderSlider || draggingParentHeaderSlider || draggingChildRowHeaderSlider) { // Indicates that this mouse click is the end of this slider drag
            draggingGrandparentHeaderSlider = draggingParentHeaderSlider = draggingChildRowHeaderSlider = false;
            return false; // Stopping propagation
        }
        double y = e.getY() + parentsCanvasDrawer.getLastVirtualViewPortY();
        ParentRow<?> parentRow = ganttLayout.getParentRowAtY(y);
        if (parentRow != null) {
            if (ganttLayout.isParentRowCollapseEnabled()) {
                Bounds chevronLocalBounds = ganttLayout.getParentRowCollapseChevronLocalBounds();
                if (new MutableBounds(parentRow.getMinX() + chevronLocalBounds.getMinX(), parentRow.getMinY() + chevronLocalBounds.getMinY(), chevronLocalBounds.getWidth(), chevronLocalBounds.getHeight()).contains(e.getX(), e.getY())) {
                    parentRow.toggleCollapse();
                    return false;
                }
            }
            if (parentsCanvasDrawer.getChildRowHeaderClickHandler() != null && isHoveringChildRowHeader(e, canvas, false)) {
                int rowIndex = parentRow.getChildRowIndexAtY(y);
                parentsCanvasDrawer.getChildRowHeaderClickHandler().accept(parentRow.getParent(), rowIndex);
                return false;
            }
        }
        return true; // Otherwise ok to continue propagation
    }

    // Private implementation

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
        // We consider the mouse is hovering it when closer than 5 px from it
        return (Math.abs(e.getX() - sliderX) < 5);
    }

    private boolean isHoveringHeader(MouseEvent e, double headerBorder1X, double headerBorder2X) {
        double minX = Math.min(headerBorder1X, headerBorder2X);
        double maxX = Math.max(headerBorder1X, headerBorder2X);
        return e.getX() >= minX && e.getX() <= maxX;
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

    private boolean isHoveringChildRowHeader(MouseEvent e, Canvas canvas, boolean slider) {
        if (!ganttLayout.isParentHeaderOnLeftOrRight())
            return false;
        if (ganttLayout.isGrandparentHeaderOnTopOrBottom() && isHoveringGrandparentHeader(e))
            return false;
        if (slider)
            return isHoveringSlider(e, childRowHeaderBorderX(canvas, true, true));
        return isHoveringHeader(e, childRowHeaderBorderX(canvas, false, false), childRowHeaderBorderX(canvas, true, false));
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
        return isHoveringHeader(e, ganttLayout.getGrandparentRows());
    }

    private boolean isHoveringParentHeader(MouseEvent e) {
        return isHoveringHeader(e, ganttLayout.getParentRows());
    }

    private boolean isHoveringHeader(MouseEvent e, List<? extends EnclosingRow<?>> rows) {
        return rows.stream().anyMatch(gr -> gr.getHeader().contains(e.getX(), e.getY() + parentsCanvasDrawer.getLastVirtualViewPortY()));
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
}
