package dev.webfx.extras.timelayout.canvas;

import dev.webfx.extras.timelayout.TimeLayout;
import javafx.scene.canvas.Canvas;

import java.time.LocalDate;

/**
 * @author Bruno Salmon
 */
public final class LocalDateCanvasDrawer<C> extends TimeCanvasDrawer<C, LocalDate> {

    public LocalDateCanvasDrawer(TimeLayout<C, LocalDate> timeLayout, ChildDrawer<C, LocalDate> childDrawer) {
        super(timeLayout, childDrawer);
    }

    public LocalDateCanvasDrawer(Canvas canvas, TimeLayout<C, LocalDate> timeLayout, ChildDrawer<C, LocalDate> childDrawer) {
        super(canvas, timeLayout, childDrawer);
    }

}
