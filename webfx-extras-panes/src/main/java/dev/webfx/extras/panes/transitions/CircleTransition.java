package dev.webfx.extras.panes.transitions;

import dev.webfx.extras.util.animation.Animations;
import javafx.animation.Interpolator;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.util.function.Supplier;

/**
 * @author Bruno Salmon
 */
public class CircleTransition implements Transition {

    @Override
    public Timeline createAndStartTransitionTimeline(Node oldContent, Node newContent, Region oldRegion, Region newRegion, Pane dualContainer, Supplier<Double> widthGetter, Supplier<Double> heightGetter, boolean reverse, boolean scrollToTop) {
        // Workaround for devices that don't support circle inverse clip (includes iPadOS at this time)
        // => we ensure the front node is on top of back node
        // Also a second condition for that workaround is that the content has a background set, but we leave that
        // responsibility to the application code.
        Node frontNode = reverse ? oldContent : newContent;
        Node backNode  = reverse ? newContent : oldContent;
        if (frontNode != null && backNode != null) {
            ObservableList<Node> children = dualContainer.getChildren();
            int frontIndex = children.indexOf(frontNode);
            int backIndex = children.indexOf(backNode);
            if (frontIndex < backIndex) { // swapping nodes
                children.set(backIndex, new Rectangle()); // dummy node to prevent duplicates during swap
                children.set(frontIndex, backNode);
                children.set(backIndex, frontNode);
            }
        }

        DoubleProperty radiusProperty = new SimpleDoubleProperty(-1) {
            @Override
            protected void invalidated() {
                double radius = get();
                double width = widthGetter.get();
                double height = Math.min(oldRegion == null ? heightGetter.get() : oldRegion.getHeight(), newRegion == null ? heightGetter.get() : newRegion.getHeight());
                if (frontNode != null) {
                    Bounds lb = frontNode.getLayoutBounds();
                    frontNode.setClip(new Circle(lb.getWidth() / 2, height / 2, radius));
                }
                if (backNode != null) {
                    backNode.setClip(null);
                    if (width == 0 || height == 0 || radius == 0) {
                        backNode.setClip(null);
                    } else { // circle inverse clip
                        Bounds lb = backNode.getLayoutBounds();
                        backNode.setClip(Shape.subtract(
                                new Rectangle(width, height),
                                new Circle(lb.getWidth() / 2, height / 2, radius)));
                    }
                }
            }
        };
        double initialRadius = 0;
        double finalRadius = (reverse ? 0.5 : 0.7) * Math.max(widthGetter.get(), heightGetter.get());
        if (reverse) {
            double swap = initialRadius;
            initialRadius = finalRadius;
            finalRadius = swap;
        }
        radiusProperty.set(initialRadius);
        if (scrollToTop)
            Animations.scrollToTop(newContent, false);
        return Animations.animateProperty(radiusProperty, finalRadius, Duration.seconds(reverse ? 0.5 : 1), Interpolator.EASE_IN, true);
    }
}
