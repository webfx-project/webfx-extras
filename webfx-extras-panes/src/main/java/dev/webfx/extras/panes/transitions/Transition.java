package dev.webfx.extras.panes.transitions;

import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

import java.util.function.Supplier;

/**
 * @author Bruno Salmon
 */
public interface Transition {

    Timeline createAndStartTransitionTimeline(
            Node oldContent,
            Node newContent,
            Region oldRegion,
            Region newRegion,
            Pane dualContainer,
            Supplier<Double> widthGetter,
            Supplier<Double> heightGetter,
            boolean reverse);

    boolean shouldVerticalScrollBeAnimated();

}
