package dev.webfx.extras.visual.controls.grid;

import javafx.geometry.Orientation;
import javafx.scene.control.Skin;

import java.util.function.Function;

/**
 * @author Bruno Salmon
 */
final class SkinnedVisualGrid extends VisualGrid {

    private final Function<VisualGrid, Skin<?>> skinFactory;

    public SkinnedVisualGrid(Function<VisualGrid, Skin<?>> skinFactory) {
        this.skinFactory = skinFactory;
        // Creating the skin now, otherwise the first layout may have a wrong cached pref size
        setSkin(createDefaultSkin());
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return skinFactory.apply(this);
    }

    @Override
    public Orientation getContentBias() {
        return Orientation.HORIZONTAL;
    }
}
