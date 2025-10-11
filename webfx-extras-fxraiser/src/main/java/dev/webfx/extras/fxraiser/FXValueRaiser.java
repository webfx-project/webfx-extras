package dev.webfx.extras.fxraiser;

public interface FXValueRaiser {

    <T> T raiseValue(Object value, Class<T> raisedClass, Object... args);

}
