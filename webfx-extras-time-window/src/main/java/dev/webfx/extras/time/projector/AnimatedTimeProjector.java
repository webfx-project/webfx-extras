package dev.webfx.extras.time.projector;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * @author Bruno Salmon
 */
public class AnimatedTimeProjector<T> extends TranslatedTimeProjector<T> {

    private final DoubleProperty translateXProperty = new SimpleDoubleProperty();

    public AnimatedTimeProjector(TimeProjector<T> otherTimeProjector) {
        super(otherTimeProjector);
    }

    @Override
    public double getTranslateX() {
        return translateXProperty.get();
    }

    public DoubleProperty translateXProperty() {
        return translateXProperty;
    }

    public void setTranslateX(double translateX) {
        translateXProperty.set(translateX);
    }
}
