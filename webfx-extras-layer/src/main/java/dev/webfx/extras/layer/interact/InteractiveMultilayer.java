package dev.webfx.extras.layer.interact;

import dev.webfx.extras.layer.Layer;
import dev.webfx.extras.layer.Multilayer;

import java.util.Objects;

public interface InteractiveMultilayer<L extends InteractiveLayer<?>> extends Multilayer<L>, CanSelectChild<Object> {

    @Override
    default void setSelectedChild(Object child) {
        throw new UnsupportedOperationException("Use InteractiveMultilayer.setSelectedChild(child, childLayer) instead");
    }

    <C, L extends InteractiveLayer<C>> void setSelectedChild(C child, L childLayer);

    L getSelectedChildLayer();

    default Object pickChildAt(double x, double y, boolean onlyIfSelectable) {
        return getLayers().stream()
                .filter(Layer::isVisible)
                .map(layer -> layer.pickChildAt(x, y, onlyIfSelectable))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    @Override
    default Object selectChildAt(double x, double y) {
        if (!isSelectionEnabled())
            return null;
        return getLayers().stream()
                .filter(Layer::isVisible)
                .map(layer -> layer.selectChildAt(x, y))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

}
