package dev.webfx.extras.timelayout.canvas;

import dev.webfx.extras.timelayout.MultilayerTimeLayout;
import javafx.scene.canvas.Canvas;

import java.time.LocalDate;

/**
 * @author Bruno Salmon
 */
public class MultiLayerLocalDateCanvasDrawer extends MultilayerTimeCanvasDrawer<LocalDate> {

    public MultiLayerLocalDateCanvasDrawer(MultilayerTimeLayout<LocalDate> multilayerTimeLayout) {
        super(multilayerTimeLayout);
    }

    public MultiLayerLocalDateCanvasDrawer(Canvas canvas, MultilayerTimeLayout<LocalDate> multilayerTimeLayout) {
        super(canvas, multilayerTimeLayout);
    }
}
