package dev.webfx.extras.time.layout.canvas;

import dev.webfx.extras.canvas.layer.ChildDrawer;
import dev.webfx.extras.time.layout.TimeLayout;
import javafx.scene.canvas.Canvas;

import java.time.LocalDate;

/**
 * @author Bruno Salmon
 */
public final class LocalDateCanvasDrawer<C> extends TimeCanvasDrawer<C, LocalDate> {

    public LocalDateCanvasDrawer(TimeLayout<C, LocalDate> timeLayout, ChildDrawer<C> childDrawer) {
        super(timeLayout, childDrawer);
    }

    public LocalDateCanvasDrawer(Canvas canvas, TimeLayout<C, LocalDate> timeLayout, ChildDrawer<C> childDrawer) {
        super(canvas, timeLayout, childDrawer);
    }

}
