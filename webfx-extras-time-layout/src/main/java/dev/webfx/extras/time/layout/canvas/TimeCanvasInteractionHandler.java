package dev.webfx.extras.time.layout.canvas;

import dev.webfx.extras.canvas.layer.interact.CanvasInteractionHandler;
import dev.webfx.extras.layer.interact.CanSelectChild;
import dev.webfx.extras.time.window.TimeWindow;
import dev.webfx.extras.time.window.TimeWindowUtil;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.Objects;

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
        mousePressedDuration = TimeWindowUtil.getTimeWindowDuration(timeWindow, temporalUnit);
        mouseDragged = false;
        updateCanvasCursor(e, true, canvas);
        return false; // -> Stopping propagation (next handlers won't be called)
    }

    @Override
    public boolean handleMouseDragged(MouseEvent e, Canvas canvas) {
        boolean wasPressedHere = mousePressedStart != null;
        if (wasPressedHere) {
            double deltaX = mousePressedX - e.getX();
            double dayWidth = canvas.getWidth() / mousePressedDuration;
            long deltaDay = (long) (deltaX / dayWidth);
            if (deltaDay != 0) {
                T start = (T) mousePressedStart.plus(deltaDay, temporalUnit);
                TimeWindowUtil.setTimeWindowStartAndDuration(timeWindow, start, mousePressedDuration, temporalUnit);
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
        // if the scroll time window feature is disabled on this canvas, we immediately return
        if (isScrollTimeWindowDisabledOnCanvas(canvas)) {
            return true; // -> Ok to continue propagation (ex: ScrollPane)
        }
        if (e.isControlDown()) { // Zoom in/out
            long duration = TimeWindowUtil.getTimeWindowDuration(timeWindow, temporalUnit);
            if (e.getDeltaY() > 0) // Mouse wheel up => Zoom in
                duration = (long) (duration / 1.10);
            else // Mouse wheel down => Zoom out
                duration = Math.max(duration + 1, (long) (duration * 1.10));
            duration = Math.min(duration, 10_000);
            TimeWindowUtil.setTimeWindowDurationKeepCentered(timeWindow, duration, temporalUnit);
        } else { // Horizontal scroll
            long amount = e.getDeltaY() > 0 ? 1 : -1;
            TimeWindowUtil.shiftTimeWindow(timeWindow, amount, temporalUnit);
        }
        // We consume the event to prevent the standard scrolling while zooming
        e.consume();
        return false; // -> Stopping propagation
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

    public static void disableScrollTimeWindowOnCanvas(Canvas canvas) {
        canvas.getProperties().put("disableScollTimeWindow", true);
    }

    public static boolean isScrollTimeWindowDisabledOnCanvas(Canvas canvas) {
        return Objects.equals(canvas.getProperties().get("disableScollTimeWindow"), true);
    }

}
