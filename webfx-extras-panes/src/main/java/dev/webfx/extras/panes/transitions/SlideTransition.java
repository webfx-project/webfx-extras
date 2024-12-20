package dev.webfx.extras.panes.transitions;

import dev.webfx.extras.util.animation.Animations;
import dev.webfx.kit.util.properties.FXProperties;
import javafx.animation.Interpolator;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.function.Supplier;

/**
 * @author Bruno Salmon
 */
public class SlideTransition implements Transition {

    private final HPos direction;

    public SlideTransition() {
        this(HPos.LEFT);
    }

    public SlideTransition(HPos direction) {
        this.direction = direction;
    }

    @Override
    public Timeline createAndStartTransitionTimeline(Node oldContent, Node newContent, Region oldRegion, Region newRegion, Pane dualContainer, Supplier<Double> widthGetter, Supplier<Double> heightGetter, boolean reverse) {
        double width = widthGetter.get();
        DoubleProperty slideXProperty = FXProperties.newDoubleProperty(-1, slideX -> {
            slideX = Math.max(slideX, 1);
            double clipHeight = Math.min(oldRegion == null ? heightGetter.get() : oldRegion.getHeight(), newRegion == null ? heightGetter.get() : newRegion.getHeight());
            Node frontNode = reverse ? oldContent : newContent;
            Node backNode = reverse ? newContent : oldContent;
            if (frontNode != null) { // should be hidden is height = 0 on start
                frontNode.setClip(new Rectangle(slideX, 0, width - slideX, clipHeight == 0 ? 1 : clipHeight));
            }
            if (backNode != null) { // should be visible if height = 0 on start
                backNode.setClip(clipHeight == 0 ? null : new Rectangle(0, 0, slideX, clipHeight));
            }
        });

        double initialSlideX;
        // Setting the initial translation (final is always 0)
        if (direction == HPos.LEFT) // transition from right to left
            initialSlideX = width; // new content entering from the right
        else { // transition from left to right
            initialSlideX = -width; // new content entering from the left
        }
        double finalTranslateX = 0;
        if (reverse) {
            double swap = initialSlideX;
            initialSlideX = finalTranslateX;
            finalTranslateX = swap;
        }
        slideXProperty.set(initialSlideX);
        return Animations.animateProperty(slideXProperty, finalTranslateX, Duration.seconds(1), Interpolator.EASE_IN, true);
    }

    @Override
    public boolean shouldVerticalScrollBeAnimated() {
        return true;
    }
}
