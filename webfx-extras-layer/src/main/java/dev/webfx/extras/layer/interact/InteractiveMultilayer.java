package dev.webfx.extras.layer.interact;

import dev.webfx.extras.layer.Layer;
import dev.webfx.extras.layer.Multilayer;

import java.util.Objects;
import java.util.function.Function;

public interface InteractiveMultilayer<L extends InteractiveLayer<?>> extends Multilayer<L>, CanSelectChild<Object> {

    @Override
    default void setSelectedChild(Object child) {
        throw new UnsupportedOperationException("Use InteractiveMultilayer.setSelectedChild(child, childLayer) instead");
    }

    <C, L extends InteractiveLayer<C>> void setSelectedChild(C child, L childLayer);

    L getSelectedChildLayer();

    default Object pickChildAt(double x, double y, boolean onlyIfSelectable) {
        return pickOrSelectChildAt(x, y, layer -> layer.pickChildAt(x, y, onlyIfSelectable));
    }

    @Override
    default Object selectChildAt(double x, double y) {
        if (!isSelectionEnabled())
            return null;
        return pickOrSelectChildAt(x, y, layer -> layer.selectChildAt(x, y));
    }

    private Object pickOrSelectChildAt(double x, double y, Function<L, Object> pickerOrSelector) {
        // Iterating in reverse order, as the last layer is above and the first is behind
        for (int i = getLayers().size() - 1; i >= 0; i--) {
            L layer = getLayers().get(i);
            if (layer.isVisible()) {
                Object selectedChild = pickerOrSelector.apply(layer);
                if (selectedChild != null)
                    return selectedChild;
            }
        }
        return null;
    }

}
