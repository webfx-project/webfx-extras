package dev.webfx.extras.panes.transitions;

import dev.webfx.extras.util.animation.Animations;
import javafx.animation.Timeline;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.util.Duration;

import java.util.function.Supplier;

/**
 * @author Bruno Salmon
 */
public class TranslateTransition implements Transition {

    private final HPos direction;

    public TranslateTransition() {
        this(HPos.LEFT);
    }

    public TranslateTransition(HPos direction) {
        this.direction = direction;
    }

    public HPos getDirection() {
        return direction;
    }

    @Override
    public Timeline createAndStartTransitionTimeline(Node oldContent, Node newContent, Region oldRegion, Region newRegion, Pane dualContainer, Supplier<Double> widthGetter, Supplier<Double> heightGetter, boolean reverse) {
        double width = widthGetter.get();
        double initialTranslateX, finalTranslateX = 0;
        // Computing the initial translation (final is always 0)
        if (direction == (reverse ? HPos.RIGHT : HPos.LEFT)) // transition from right to left
            initialTranslateX = width; // new content entering from the right
        else { // transition from left to right
            initialTranslateX = -width; // new content entering from the left
        }
        dualContainer.setTranslateX(initialTranslateX);
        return Animations.animateProperty(dualContainer.translateXProperty(), finalTranslateX, Duration.seconds(0.7), Animations.EASE_BOTH_INTERPOLATOR, true);
    }

    @Override
    public boolean shouldVerticalScrollBeAnimated() {
        return true;
    }
}
