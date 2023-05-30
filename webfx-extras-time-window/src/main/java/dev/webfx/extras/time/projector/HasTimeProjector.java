package dev.webfx.extras.time.projector;

public interface HasTimeProjector<T> {

    TimeProjector<T> getTimeProjector();

    HasTimeProjector<T> setTimeProjector(TimeProjector<T> timeProjector);

}
