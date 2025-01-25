package dev.webfx.extras.panes;

import javafx.geometry.Orientation;
import javafx.scene.Node;

/**
 * @author Bruno Salmon
 */
public abstract class HorizontalBiasLayoutPane extends LayoutPane {

    public HorizontalBiasLayoutPane() {
    }

    public HorizontalBiasLayoutPane(Node... children) {
        super(children);
    }

    {
        // Not necessary but may speed up min & max computations in parent container
        setMinWidth(0);
        setMaxWidth(Double.MAX_VALUE);
        setMinHeight(0);
        setMaxHeight(Double.MAX_VALUE);
    }

    @Override
    public Orientation getContentBias() {
        return Orientation.HORIZONTAL;
    }

}
