package dev.webfx.extras.time.layout.canvas;

import dev.webfx.extras.canvas.HasCanvas;
import dev.webfx.extras.layer.interact.InteractiveLayer;
import dev.webfx.extras.time.window.TimeWindow;
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

    public LocalDateCanvasInteractionManager(HasCanvas hasCanvas, TimeWindow<LocalDate> timeWindow, InteractiveLayer<?> canSelectChild) {
        super(hasCanvas.getCanvas(), timeWindow, canSelectChild, ChronoUnit.DAYS);
    }

    public LocalDateCanvasInteractionManager(Canvas canvas, TimeWindow<LocalDate> timeWindow) {
        super(canvas, timeWindow, ChronoUnit.DAYS);
    }

    public LocalDateCanvasInteractionManager(Canvas canvas, TimeWindow<LocalDate> timeWindow, InteractiveLayer<?> canSelectChild) {
        super(canvas, timeWindow, canSelectChild, ChronoUnit.DAYS);
    }


    // Static methods

    public static void makeCanvasInteractive(HasCanvas hasCanvas, TimeWindow<LocalDate> timeWindow) {
        new LocalDateCanvasInteractionManager(hasCanvas, timeWindow).makeCanvasInteractive();
    }

    public static void makeCanvasInteractive(HasCanvas hasCanvas, TimeWindow<LocalDate> timeWindow, InteractiveLayer<?> canSelectChild) {
        new LocalDateCanvasInteractionManager(hasCanvas, timeWindow, canSelectChild).makeCanvasInteractive();
    }

    public static void makeCanvasInteractive(Canvas canvas, TimeWindow<LocalDate> timeWindow) {
        new LocalDateCanvasInteractionManager(canvas, timeWindow).makeCanvasInteractive();
    }

    public static void makeCanvasInteractive(Canvas canvas, TimeWindow<LocalDate> timeWindow, InteractiveLayer<?> canSelectChild) {
        new LocalDateCanvasInteractionManager(canvas, timeWindow, canSelectChild).makeCanvasInteractive();
    }

}
