package dev.webfx.extras.panes;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.layout.Region;

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
        // By default, this container can be shrunk or stretched horizontally with no limit
        setMinWidth(0);
        setMaxWidth(Double.MAX_VALUE);
        // And its min and max heights are set to use the preferred height (the application code should override the
        // computePrefHeight() to calculate in dependence of the width).
        setMinHeight(Region.USE_PREF_SIZE);
        setMaxHeight(Region.USE_PREF_SIZE);
    }

    @Override
    public Orientation getContentBias() {
        return Orientation.HORIZONTAL; // To indicate that the height of this pane depends on its width
    }

    /*@Override // Uncomment once DayTemplateTimelineView implements it
    protected abstract double computePrefHeight(double width);*/
}
