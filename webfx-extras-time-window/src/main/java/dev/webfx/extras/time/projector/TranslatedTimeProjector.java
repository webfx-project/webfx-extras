package dev.webfx.extras.time.projector;

import java.time.temporal.TemporalUnit;

/**
 * @author Bruno Salmon
 */
public abstract class TranslatedTimeProjector<T> implements TimeProjector<T> {

    private final TimeProjector<T> otherTimeProjector;

    public TranslatedTimeProjector(TimeProjector<T> otherTimeProjector) {
        this.otherTimeProjector = otherTimeProjector;
    }

    public abstract double getTranslateX();

    @Override
    public double timeToX(T time, boolean start, boolean exclusive) {
        return otherTimeProjector.timeToX(time, start, exclusive) - getTranslateX();
    }

    @Override
    public T xToTime(double x) {
        return otherTimeProjector.xToTime(x + getTranslateX());
    }

    @Override
    public TemporalUnit getTemporalUnit() {
        return otherTimeProjector.getTemporalUnit();
    }
}
