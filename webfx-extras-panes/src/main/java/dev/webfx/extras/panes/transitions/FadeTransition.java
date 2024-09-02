package dev.webfx.extras.panes.transitions;

import dev.webfx.extras.util.animation.Animations;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.util.Duration;

import java.util.function.Supplier;

/**
 * @author Bruno Salmon
 */
public class FadeTransition implements Transition {

    @Override
    public Timeline createAndStartTransitionTimeline(Node oldContent, Node newContent, Region oldRegion, Region newRegion, Pane dualContainer, Supplier<Double> widthGetter, Supplier<Double> heightGetter, boolean reverse) {
        DoubleProperty fadeProperty = new SimpleDoubleProperty(-1) {
            @Override
            protected void invalidated() {
                double fade = get();
                if (newContent != null)
                    newContent.setOpacity(fade);
                if (oldRegion != null)
                    oldRegion.setOpacity(1 - fade);
            }
        };
        fadeProperty.set(0);
        return Animations.animateProperty(fadeProperty, 1, Duration.seconds(reverse ? 0.4 : 0.7), Animations.EASE_BOTH_INTERPOLATOR, true);
    }

    @Override
    public boolean shouldVerticalScrollBeAnimated() {
        return false;
    }
}
