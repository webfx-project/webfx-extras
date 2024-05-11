package dev.webfx.extras.util.animation;

import dev.webfx.platform.util.Objects;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.WritableValue;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.function.Function;

/**
 * @author Bruno Salmon
 */
public final class Animations {

    // Ease out interpolator closer to the web standard than the one proposed in JavaFX (ie Interpolator.EASE_OUT)
    public final static Interpolator EASE_OUT_INTERPOLATOR = Interpolator.SPLINE(0, .75, .25, 1);

    public static <T> Timeline animateProperty(WritableValue<T> target, T finalValue) {
        return animateProperty(target, finalValue, true);
    }

    public static <T> Timeline animateProperty(WritableValue<T> target, T finalValue, boolean animate) {
        return animateProperty(target, finalValue, animate ? EASE_OUT_INTERPOLATOR : null);
    }

    public static <T> Timeline animateProperty(WritableValue<T> target, T finalValue, Duration duration) {
        return animateProperty(target, finalValue, duration, EASE_OUT_INTERPOLATOR);
    }

    public static <T> Timeline animateProperty(WritableValue<T> target, T finalValue, Interpolator interpolator) {
        return animateProperty(target, finalValue, Duration.seconds(1), interpolator);
    }

    public static <T> Timeline animateProperty(WritableValue<T> target, T finalValue, Duration duration, Interpolator interpolator) {
        if (!Objects.areEquals(target.getValue(), finalValue)) {
            if (interpolator == null || duration == null || duration.equals(Duration.ZERO))
                target.setValue(finalValue);
            else {
                Timeline timeline = new Timeline(new KeyFrame(duration, new KeyValue(target, finalValue, interpolator)));
                timeline.play();
                return timeline;
            }
        }
        return null;
    }

    public static void shake(Node node) {
        DoubleProperty x = node.layoutXProperty(); // translateX would be better but not yet emulated so using layoutX instead
        double xIni = x.getValue(), xMin = xIni - 10, xMax = xIni + 10;
        new Timeline(
                // Turning node to unmanaged (absolute positioning) to be sure layoutX will be considered
                new KeyFrame(Duration.millis(0),    new KeyValue(node.managedProperty(), false)),
                new KeyFrame(Duration.millis(100),  new KeyValue(x, xMin)),
                new KeyFrame(Duration.millis(200),  new KeyValue(x, xMax)),
                new KeyFrame(Duration.millis(300),  new KeyValue(x, xMin)),
                new KeyFrame(Duration.millis(400),  new KeyValue(x, xMax)),
                new KeyFrame(Duration.millis(500),  new KeyValue(x, xMin)),
                new KeyFrame(Duration.millis(600),  new KeyValue(x, xMax)),
                new KeyFrame(Duration.millis(700),  new KeyValue(x, xMin)),
                new KeyFrame(Duration.millis(800),  new KeyValue(x, xMax)),
                new KeyFrame(Duration.millis(900),  new KeyValue(x, xMin)),
                new KeyFrame(Duration.millis(1000), new KeyValue(x, xIni)),
                // Restoring the managed value
                new KeyFrame(Duration.millis(1000), new KeyValue(node.managedProperty(), node.isManaged()))
        ).play();
    }

    // scrollToTop() feature that is partially implemented here (because we don't want to introduce a dependency to
    // javafx-control here), but its implementation is complemented by dev.webfx.extras.util.control.ControlUtil.
    // So modules that depend on javafx-graphics only can use it, it won't do anything if the final application doesn't
    // use javafx-control (=> no ScrollPane), but it will if the final application uses it (and ControlUtil).

    public static void scrollToTop(Node content, boolean animated) {
        if (scrollPaneAncestorFinder != null && scrollPaneValuePropertyGetter != null) {
            Node scrollPane = scrollPaneAncestorFinder.apply(content);
            if (scrollPane != null) {
                DoubleProperty valueProperty = scrollPaneValuePropertyGetter.apply(scrollPane);
                if (valueProperty != null) {
                    if (!animated)
                        valueProperty.set(0);
                    else
                        animateProperty(valueProperty, 0);
                }
            }
        }
    }

    private static Function<Node, Node> scrollPaneAncestorFinder;
    private static Function<Node, DoubleProperty> scrollPaneValuePropertyGetter;

    public static void setScrollPaneAncestorFinder(Function<Node, Node> scrollPaneAncestorFinder) {
        Animations.scrollPaneAncestorFinder = scrollPaneAncestorFinder;
    }

    public static void setScrollPaneValuePropertyGetter(Function<Node, DoubleProperty> scrollPaneValuePropertyGetter) {
        Animations.scrollPaneValuePropertyGetter = scrollPaneValuePropertyGetter;
    }

}
