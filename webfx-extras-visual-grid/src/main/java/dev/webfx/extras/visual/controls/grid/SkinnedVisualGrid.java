package dev.webfx.extras.visual.controls.grid;

import dev.webfx.extras.visual.VisualResult;
import javafx.scene.control.Skin;

/**
 * @author Bruno Salmon
 */
public final class SkinnedVisualGrid extends VisualGrid {

    public SkinnedVisualGrid() {
        this(null);
    }

    public SkinnedVisualGrid(VisualResult rs) {
        super(rs);
        // Creating the skin now, otherwise the first layout may have a wrong cached pref size
        skinProperty().set(createDefaultSkin());
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new VisualGridSkin(this);
    }
}
