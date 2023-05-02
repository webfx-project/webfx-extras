package dev.webfx.extras.timelayout.canvas;

import dev.webfx.extras.timelayout.CanSelectChild;
import dev.webfx.extras.timelayout.TimeWindow;
import dev.webfx.extras.timelayout.canvas.generic.HasCanvas;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;

/**
 * @author Bruno Salmon
 */
public class TimeCanvasInteractionManager<T extends Temporal> {

    private final Canvas canvas;
    private final TimeWindow<T> timeWindow;
    private final TemporalUnit temporalUnit;
    private final CanSelectChild<?> canSelectChild;
    private double mousePressedX;
    private T mousePressedStart;
    private long mousePressedDuration;
    private boolean mouseDragged;

    public TimeCanvasInteractionManager(HasCanvas hasCanvas, TimeWindow<T> timeWindow, TemporalUnit temporalUnit) {
        this(hasCanvas.getCanvas(), timeWindow, timeWindow instanceof CanSelectChild ? (CanSelectChild<?>) timeWindow : null, temporalUnit);
    }

    public TimeCanvasInteractionManager(HasCanvas hasCanvas, TimeWindow<T> timeWindow, CanSelectChild<?> canSelectChild, TemporalUnit temporalUnit) {
        this(hasCanvas.getCanvas(), timeWindow, canSelectChild, temporalUnit);
    }

    public TimeCanvasInteractionManager(Canvas canvas, TimeWindow<T> timeWindow, TemporalUnit temporalUnit) {
        this(canvas, timeWindow, timeWindow instanceof CanSelectChild ? (CanSelectChild<?>) timeWindow : null, temporalUnit);
    }

    public TimeCanvasInteractionManager(Canvas canvas, TimeWindow<T> timeWindow, CanSelectChild<?> canSelectChild, TemporalUnit temporalUnit) {
        this.canvas = canvas;
        this.timeWindow = timeWindow;
        this.temporalUnit = temporalUnit;
        this.canSelectChild = canSelectChild;
    }

    public void makeCanvasInteractive() {
        setInteractive(true);
    }

    public void setInteractive(boolean interactive) {
        boolean off = !interactive;
        canvas.setOnMousePressed(off ? null : e -> {
            mousePressedX = e.getX();
            mousePressedStart = timeWindow.getTimeWindowStart();
            if (mousePressedStart != null)
                mousePressedDuration = temporalUnit.between(mousePressedStart, timeWindow.getTimeWindowEnd());
            mouseDragged = false;
            updateCanvasCursor(e, true);
        });
        canvas.setOnMouseDragged(off ? null : e -> {
            double deltaX = mousePressedX - e.getX();
            double dayWidth = canvas.getWidth() / (mousePressedDuration + 1);
            long deltaDay = (long) (deltaX / dayWidth);
            if (deltaDay != 0 && mousePressedStart != null) {
                setTimeWindow((T) mousePressedStart.plus(deltaDay, temporalUnit), mousePressedDuration);
                mouseDragged = true;
            }
            updateCanvasCursor(e, true);
        });
        // Selecting the event when clicked
        canvas.setOnMouseClicked(off ? null : e -> {
            if (!mouseDragged)
                selectObjectAt(e.getX(), e.getY());
            updateCanvasCursor(e, false);
            mousePressedStart = null;
        });
        // Changing cursor to hand cursor when hovering an event (to indicate it's clickable)
        canvas.setOnMouseMoved(off ? null : e -> updateCanvasCursor(e, false));
        canvas.setOnScroll(off ? null : e -> {
            if (e.isControlDown()) {
                T start = timeWindow.getTimeWindowStart();
                T end = timeWindow.getTimeWindowEnd();
                long duration = ChronoUnit.DAYS.between(start, end);
                T middle = (T) start.plus(duration / 2, temporalUnit);
                if (e.getDeltaY() > 0) // Mouse wheel up => Zoom in
                    duration = (long) (duration / 1.10);
                else // Mouse wheel down => Zoom out
                    duration = Math.max(duration + 1, (long) (duration * 1.10));
                duration = Math.min(duration, 10_000);
                setTimeWindow((T) middle.minus(duration / 2, temporalUnit), duration);
            }
        });
    }

    private void setTimeWindow(T start, long duration) {
        timeWindow.setTimeWindow(start, (T) start.plus(duration, ChronoUnit.DAYS));
    }

    private void updateCanvasCursor(MouseEvent e, boolean mouseDown) {
        canvas.setCursor(mouseDown && mouseDragged ? Cursor.CLOSED_HAND : isSelectableObjectPresentAt(e.getX(), e.getY()) ? Cursor.HAND : Cursor.OPEN_HAND);
    }

    private boolean isSelectableObjectPresentAt(double x, double y) {
        return canSelectChild != null && canSelectChild.pickChildAt(x, y, true) != null;
    }

    private void selectObjectAt(double x, double y) {
        canSelectChild.selectChildAt(x, y);
    }

}
