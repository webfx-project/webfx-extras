package dev.webfx.extras.layer.interact;

import java.util.function.Supplier;

/**
 * @author Bruno Salmon
 */
public class TranslatedCanSelectChild<T> extends DelegatedCanSelectChild<T> {

    private final Supplier<Double> originXGetter;
    private final Supplier<Double> originYGetter;

    public TranslatedCanSelectChild(CanSelectChild<T> delegate, Supplier<Double> originXGetter, Supplier<Double> originYGetter) {
        super(delegate);
        this.originXGetter = originXGetter;
        this.originYGetter = originYGetter;
    }

    protected double getOriginX() {
        return originXGetter.get();
    }

    protected double getOriginY() {
        return originYGetter.get();
    }

    @Override
    public T pickChildAt(double x, double y, boolean onlyIfSelectable) {
        return super.pickChildAt(getOriginX() + x, getOriginY() + y, onlyIfSelectable);
    }

    @Override
    public T selectChildAt(double x, double y) {
        return super.selectChildAt(getOriginX() + x, getOriginY() + y);
    }


}
