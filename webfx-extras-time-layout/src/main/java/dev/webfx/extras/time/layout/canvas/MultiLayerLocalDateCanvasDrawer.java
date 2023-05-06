package dev.webfx.extras.time.layout.canvas;

import dev.webfx.extras.time.layout.MultilayerTimeLayout;
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
