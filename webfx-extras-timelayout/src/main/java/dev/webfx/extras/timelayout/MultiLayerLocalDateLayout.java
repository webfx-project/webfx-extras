package dev.webfx.extras.timelayout;

import dev.webfx.extras.timelayout.impl.MultilayerTimeLayoutImpl;

import java.time.LocalDate;

/**
 * @author Bruno Salmon
 */
public class MultiLayerLocalDateLayout extends MultilayerTimeLayoutImpl<LocalDate> {

    public static MultiLayerLocalDateLayout create() {
        return new MultiLayerLocalDateLayout();
    }

}
