package dev.webfx.extras.time.layout;

import dev.webfx.extras.layer.interact.InteractiveMultilayer;
import dev.webfx.extras.time.layout.impl.MultilayerTimeLayoutImpl;
import dev.webfx.extras.time.projector.HasTimeProjector;
import dev.webfx.extras.time.window.ListenableTimeWindow;

public interface MultilayerTimeLayout<T> extends ListenableTimeWindow<T>, CanLayout, HasTimeProjector<T>, InteractiveMultilayer<TimeLayout<?, T>> {

    void setCanvasDirtyMarker(Runnable canvasDirtyMarker);

    static <T> MultilayerTimeLayout<T> create() {
        return new MultilayerTimeLayoutImpl<>();
    }
}
