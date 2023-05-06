package dev.webfx.extras.time.layout;

import dev.webfx.extras.time.layout.impl.MultilayerTimeLayoutImpl;

import java.time.LocalDate;

/**
 * @author Bruno Salmon
 */
public class MultiLayerLocalDateLayout extends MultilayerTimeLayoutImpl<LocalDate> {

    public static MultiLayerLocalDateLayout create() {
        return new MultiLayerLocalDateLayout();
    }

}
