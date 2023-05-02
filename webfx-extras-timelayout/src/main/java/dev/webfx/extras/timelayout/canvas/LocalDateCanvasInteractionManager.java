package dev.webfx.extras.timelayout.canvas;

import dev.webfx.extras.timelayout.CanSelectChild;
import dev.webfx.extras.timelayout.TimeWindow;
import dev.webfx.extras.timelayout.canvas.generic.HasCanvas;
import javafx.scene.canvas.Canvas;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * @author Bruno Salmon
 */
public final class LocalDateCanvasInteractionManager extends TimeCanvasInteractionManager<LocalDate> {

    public LocalDateCanvasInteractionManager(HasCanvas hasCanvas, TimeWindow<LocalDate> timeWindow) {
        super(hasCanvas, timeWindow, ChronoUnit.DAYS);
    }

    public LocalDateCanvasInteractionManager(HasCanvas hasCanvas, TimeWindow<LocalDate> timeWindow, CanSelectChild<?> canSelectChild) {
        super(hasCanvas.getCanvas(), timeWindow, canSelectChild, ChronoUnit.DAYS);
    }

    public LocalDateCanvasInteractionManager(Canvas canvas, TimeWindow<LocalDate> timeWindow) {
        super(canvas, timeWindow, ChronoUnit.DAYS);
    }

    public LocalDateCanvasInteractionManager(Canvas canvas, TimeWindow<LocalDate> timeWindow, CanSelectChild<?> canSelectChild) {
        super(canvas, timeWindow, canSelectChild, ChronoUnit.DAYS);
    }


    // Static methods

    public static void makeCanvasInteractive(HasCanvas hasCanvas, TimeWindow<LocalDate> timeWindow) {
        new LocalDateCanvasInteractionManager(hasCanvas, timeWindow).makeCanvasInteractive();
    }

    public static void makeCanvasInteractive(HasCanvas hasCanvas, TimeWindow<LocalDate> timeWindow, CanSelectChild<?> canSelectChild) {
        new LocalDateCanvasInteractionManager(hasCanvas, timeWindow, canSelectChild).makeCanvasInteractive();
    }

    public static void makeCanvasInteractive(Canvas canvas, TimeWindow<LocalDate> timeWindow) {
        new LocalDateCanvasInteractionManager(canvas, timeWindow).makeCanvasInteractive();
    }

    public static void makeCanvasInteractive(Canvas canvas, TimeWindow<LocalDate> timeWindow, CanSelectChild<?> canSelectChild) {
        new LocalDateCanvasInteractionManager(canvas, timeWindow, canSelectChild).makeCanvasInteractive();
    }

}
