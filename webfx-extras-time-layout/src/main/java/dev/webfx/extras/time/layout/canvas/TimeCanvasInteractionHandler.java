package dev.webfx.extras.time.layout.canvas;

import dev.webfx.extras.canvas.layer.interact.CanvasInteractionHandler;
import dev.webfx.extras.layer.interact.CanSelectChild;
import dev.webfx.extras.time.window.TimeWindow;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;

/**
 * @author Bruno Salmon
 */
public class TimeCanvasInteractionHandler<T extends Temporal> implements CanvasInteractionHandler {

    private final TimeWindow<T> timeWindow;
    private final TemporalUnit temporalUnit;
    private final CanSelectChild<?> canSelectChild;
    private double mousePressedX;
    private T mousePressedStart;
    private long mousePressedDuration;
    private boolean mouseDragged;

    public TimeCanvasInteractionHandler(TimeWindow<T> timeWindow, TemporalUnit temporalUnit, CanSelectChild<?> canSelectChild) {
        this.timeWindow = timeWindow;
        this.temporalUnit = temporalUnit;
        this.canSelectChild = canSelectChild;
    }

    @Override
    public boolean handleMousePressed(MouseEvent e, Canvas canvas) {
        mousePressedX = e.getX();
        mousePressedStart = timeWindow.getTimeWindowStart();
        if (mousePressedStart != null)
            mousePressedDuration = temporalUnit.between(mousePressedStart, timeWindow.getTimeWindowEnd());
        mouseDragged = false;
        updateCanvasCursor(e, true, canvas);
        return false; // -> Stopping propagation (next handlers won't be called)
    }

    @Override
    public boolean handleMouseDragged(MouseEvent e, Canvas canvas) {
        boolean wasPressedHere = mousePressedStart != null;
        if (wasPressedHere) {
            double deltaX = mousePressedX - e.getX();
            double dayWidth = canvas.getWidth() / (mousePressedDuration + 1);
            long deltaDay = (long) (deltaX / dayWidth);
            if (deltaDay != 0) {
                setTimeWindow((T) mousePressedStart.plus(deltaDay, temporalUnit), mousePressedDuration);
                mouseDragged = true;
            }
            updateCanvasCursor(e, true, canvas);
            return false; // -> Stopping propagation
        }
        return true; // Otherwise ok to continue propagation
    }

    @Override
    public boolean handleMouseClicked(MouseEvent e, Canvas canvas) {
        boolean wasPressedHere = mousePressedStart != null;
        if (wasPressedHere) {
            selectObjectAt(e.getX(), e.getY());
            updateCanvasCursor(e, false, canvas);
            mousePressedStart = null;
            return false; // -> Stopping propagation
        }
        return true; // Otherwise ok to continue propagation
    }

    @Override
    public boolean handleMouseMoved(MouseEvent e, Canvas canvas) {
        updateCanvasCursor(e, false, canvas);
        return true; // -> Ok to continue propagation
    }

    @Override
    public boolean handleScroll(ScrollEvent e, Canvas canvas) {
        if (e.isControlDown()) {
            T start = timeWindow.getTimeWindowStart();
            T end = timeWindow.getTimeWindowEnd();
            long duration = temporalUnit.between(start, end);
            T middle = (T) start.plus(duration / 2, temporalUnit);
            if (e.getDeltaY() > 0) // Mouse wheel up => Zoom in
                duration = (long) (duration / 1.10);
            else // Mouse wheel down => Zoom out
                duration = Math.max(duration + 1, (long) (duration * 1.10));
            duration = Math.min(duration, 10_000);
            setTimeWindow((T) middle.minus(duration / 2, temporalUnit), duration);
            // We consume the event to prevent the standard scrolling while zooming
            e.consume();
            return false; // -> Stopping propagation
        }
        return true; // Otherwise ok to continue propagation
    }

    private void setTimeWindow(T start, long duration) {
        timeWindow.setTimeWindow(start, (T) start.plus(duration, temporalUnit));
    }

    private void updateCanvasCursor(MouseEvent e, boolean mouseDown, Canvas canvas) {
        canvas.setCursor(mouseDown && mouseDragged ? Cursor.CLOSED_HAND : isSelectableObjectPresentAt(e.getX(), e.getY()) ? Cursor.HAND : Cursor.OPEN_HAND);
    }

    private boolean isSelectableObjectPresentAt(double x, double y) {
        return canSelectChild != null && canSelectChild.pickChildAt(x, y, true) != null;
    }

    private void selectObjectAt(double x, double y) {
        canSelectChild.selectChildAt(x, y);
    }

}
