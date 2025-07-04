package dev.webfx.extras.fxraiser.impl.converters;

import dev.webfx.platform.util.Objects;
import dev.webfx.extras.fxraiser.FXValueRaiser;
import javafx.scene.paint.Paint;

/**
 * @author Bruno Salmon
 */
public class StringToPaintConverter implements FXValueRaiser {

    @Override
    public <T> T raiseValue(Object value, Class<T> raisedClass, Object... args) {
        if (value instanceof String && Objects.isAssignableFrom(raisedClass, Paint.class)) {
            try {
                return (T) Paint.valueOf((String) value);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

}
