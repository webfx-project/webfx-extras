package dev.webfx.extras.util.animation;

import dev.webfx.platform.uischeduler.UiScheduler;
import dev.webfx.platform.util.Objects;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.WritableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.function.Function;

/**
 * @author Bruno Salmon
 */
public final class Animations {

    // Ease out interpolator closer to the web standard than the one proposed in JavaFX (ie Interpolator.EASE_OUT)
    public final static Interpolator EASE_OUT_INTERPOLATOR = Interpolator.SPLINE(0, .75, .25, 1);
    public final static Interpolator EASE_BOTH_INTERPOLATOR = Interpolator.SPLINE(0.42, 0, .58, 1);

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
        return animateProperty(target, finalValue, duration, interpolator, false);
    }

    public static <T> Timeline animateProperty(WritableValue<T> target, T finalValue, Duration duration, Interpolator interpolator, boolean onIdle) {
        //System.out.println("animateProperty() target = " + target + ", finalValue = " + finalValue);
        Timeline timeline;
        if (!Objects.areEquals(target.getValue(), finalValue)) {
            if (interpolator == null || duration == null || duration.equals(Duration.ZERO)) {
                target.setValue(finalValue);
                timeline = new Timeline();
            } else {
                timeline = new Timeline(new KeyFrame(duration, new KeyValue(target, finalValue, interpolator)));
            }
        } else
            timeline = new Timeline();
        if (onIdle) {
            // TODO: implement UiScheduler.scheduleInNextIdleAnimationFrame()
            UiScheduler.scheduleInAnimationFrame(() -> {
                if (timeline.getCurrentTime().toMillis() == 0)
                    timeline.play();
            }, 5); // for now, we assume 5 animation frames (80ms) are enough to pass a possible UI rush
        } else {
            timeline.play();
        }
        return timeline;
    }

    public static boolean isTimelineFinished(Timeline timeline) {
        return java.util.Objects.equals(timeline.getCurrentTime(), timeline.getTotalDuration());
    }

    public static void callTimelineOnFinishedIfFinished(Timeline timeline) {
        if (timeline != null && isTimelineFinished(timeline)) {
            EventHandler<ActionEvent> onFinished = timeline.getOnFinished();
            if (onFinished != null)
                onFinished.handle(null);
        }
    }

    public static void setOrCallOnTimelineFinished(Timeline timeline, EventHandler<ActionEvent> onFinished) {
        timeline.setOnFinished(onFinished);
        callTimelineOnFinishedIfFinished(timeline);
    }

    public static void forceTimelineToFinish(Timeline timeline) {
        if (timeline != null && !isTimelineFinished(timeline)) {
            timeline.jumpTo(timeline.getTotalDuration());
            timeline.stop();
            callTimelineOnFinishedIfFinished(timeline);
        }
    }

    public static void shake(Node node) {
        DoubleProperty x = node.translateXProperty();
        double xIni = x.getValue(), xMin = xIni - 10, xMax = xIni + 10;
        new Timeline(
                new KeyFrame(Duration.millis(100),  new KeyValue(x, xMin)),
                new KeyFrame(Duration.millis(200),  new KeyValue(x, xMax)),
                new KeyFrame(Duration.millis(300),  new KeyValue(x, xMin)),
                new KeyFrame(Duration.millis(400),  new KeyValue(x, xMax)),
                new KeyFrame(Duration.millis(500),  new KeyValue(x, xMin)),
                new KeyFrame(Duration.millis(600),  new KeyValue(x, xMax)),
                new KeyFrame(Duration.millis(700),  new KeyValue(x, xMin)),
                new KeyFrame(Duration.millis(800),  new KeyValue(x, xMax)),
                new KeyFrame(Duration.millis(900),  new KeyValue(x, xMin)),
                new KeyFrame(Duration.millis(1000), new KeyValue(x, xIni))
        ).play();
    }

    // scrollToTop() feature that is partially implemented here (because we don't want to introduce a dependency to
    // javafx-control here), but its implementation is complemented by dev.webfx.extras.util.control.ControlUtil.
    // So modules that depend on javafx-graphics only can use it, it won't do anything if the final application doesn't
    // use javafx-control (=> no ScrollPane), but it will if the final application uses it (and ControlUtil).

    public static Timeline scrollToTop(Node content, boolean animated) {
        if (scrollPaneAncestorFinder != null && scrollPaneValuePropertyGetter != null) {
            Node scrollPane = scrollPaneAncestorFinder.apply(content);
            if (scrollPane != null) {
                DoubleProperty valueProperty = scrollPaneValuePropertyGetter.apply(scrollPane);
                if (valueProperty != null) {
                    double vValue = 0;
                    Node hotNode = getScrollToTopTargetNode(content);
                    if (hotNode != null && computeVerticalScrollNodeWishedValueGetter != null)
                        vValue = computeVerticalScrollNodeWishedValueGetter.apply(hotNode);
                    //Console.log("👉👉👉👉👉 vValue = " + vValue);
                    return animateProperty(valueProperty, vValue, animated);
                }
            }
        }
        return null;
    }

    public static void setScrollToTopTargetNode(Node content, Node hotNode) {
        content.getProperties().put("scrollToTopTargetNode", hotNode);
    }

    private static Node getScrollToTopTargetNode(Node content) {
        return (Node) content.getProperties().get("scrollToTopTargetNode");
    }

    private static Function<Node, Node> scrollPaneAncestorFinder;
    private static Function<Node, DoubleProperty> scrollPaneValuePropertyGetter;
    private static Function<Node, Double> computeVerticalScrollNodeWishedValueGetter;

    public static void setScrollPaneAncestorFinder(Function<Node, Node> scrollPaneAncestorFinder) {
        Animations.scrollPaneAncestorFinder = scrollPaneAncestorFinder;
    }

    public static void setScrollPaneValuePropertyGetter(Function<Node, DoubleProperty> scrollPaneValuePropertyGetter) {
        Animations.scrollPaneValuePropertyGetter = scrollPaneValuePropertyGetter;
    }

    public static void setComputeVerticalScrollNodeWishedValueGetter(Function<Node, Double> computeVerticalScrollNodeWishedValueGetter) {
        Animations.computeVerticalScrollNodeWishedValueGetter = computeVerticalScrollNodeWishedValueGetter;
    }
}
