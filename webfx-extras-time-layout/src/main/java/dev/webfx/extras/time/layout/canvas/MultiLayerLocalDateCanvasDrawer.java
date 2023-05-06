package dev.webfx.extras.time.layout.canvas;

import dev.webfx.extras.time.layout.MultilayerTimeLayout;
import javafx.scene.canvas.Canvas;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * @author Bruno Salmon
 */
public class MultiLayerLocalDateCanvasDrawer extends MultilayerTimeCanvasDrawer<LocalDate> {

    public MultiLayerLocalDateCanvasDrawer(MultilayerTimeLayout<LocalDate> multilayerTimeLayout) {
        super(multilayerTimeLayout, ChronoUnit.DAYS);
    }

    public MultiLayerLocalDateCanvasDrawer(Canvas canvas, MultilayerTimeLayout<LocalDate> multilayerTimeLayout) {
        super(canvas, multilayerTimeLayout, ChronoUnit.DAYS);
    }
}
