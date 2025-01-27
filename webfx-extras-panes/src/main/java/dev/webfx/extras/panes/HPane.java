package dev.webfx.extras.panes;

import javafx.geometry.Orientation;
import javafx.scene.Node;

/**
 * A layout pane that returns HORIZONTAL as its content bias, which means that its height depends on its width.
 *
 * @author Bruno Salmon
 */
public abstract class HPane extends LayoutPane {

    public HPane() {
    }

    public HPane(Node... children) {
        super(children);
    }

    {
        // By default, we apply these fixed values for min/max width/height, as an optimization to speed up the layout
        // computations.
        setMinWidth(0);
        setMaxWidth(Double.MAX_VALUE);
        setMinHeight(0);
        setMaxHeight(Double.MAX_VALUE);
    }

    @Override
    public Orientation getContentBias() {
        return Orientation.HORIZONTAL; // To indicate that the height of this pane depends on its width
    }

}
