package dev.webfx.extras.panes.transitions;

import dev.webfx.extras.util.animation.Animations;
import dev.webfx.platform.useragent.UserAgent;
import dev.webfx.platform.util.collection.Collections;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
        DoubleProperty translateXProperty;
        // For the web platform, we get a smoother translation effect by using a CSS variable (--fx-translate-x declared
        // in the CSS file) because otherwise changing translateXProperty() directly causes a JavaFX layout-pass on each
        // frame which slows things down.
        if (UserAgent.isBrowser()) {
            Collections.addIfNotContains("transition-pane-dual-container", dualContainer.getStyleClass());
            translateXProperty = new SimpleDoubleProperty() {
                @Override protected void invalidated() {
                    dualContainer.setStyle("--fx-translate-x: " + get() + "px;");
                }
            };
        } else { // for the JavaFX platform, we use the built-in translateXProperty() which is ok
            translateXProperty = dualContainer.translateXProperty();
        }
        translateXProperty.set(initialTranslateX);
        return Animations.animateProperty(translateXProperty, finalTranslateX, Duration.seconds(0.7), Animations.EASE_BOTH_INTERPOLATOR, true);
    }

    @Override
    public boolean shouldVerticalScrollBeAnimated() {
        return true;
    }
}
