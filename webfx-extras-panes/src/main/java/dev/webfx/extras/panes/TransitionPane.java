package dev.webfx.extras.panes;

import dev.webfx.extras.util.animation.Animations;
import javafx.animation.Interpolator;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.util.Objects;

/**
 * This Pane creates an animation when changing its content, so the old node looks like leaving, and the new node looks
 * like entering. For now, only horizontal translation animation is supported (from left to right or right to left).
 *
 * @author Bruno Salmon
 */
public final class TransitionPane extends MonoClipPane {

    private HPos direction = HPos.LEFT;
    private final ObjectProperty<Node> requestedEnteringNodeProperty = new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
            onEnteringNodeRequested(get());
        }
    };
    private final BooleanProperty transitingProperty = new SimpleBooleanProperty();
    private Node enteringNode, leavingNode;

    private boolean animate = true;
    private boolean animateFirstContent = false;
    private boolean reverse;
    private boolean keepsLeavingNodes = false;
    private boolean slideAnimation = false;
    private boolean circleAnimation = false;
    private boolean scrollToTop = false;
    private Timeline timeline;

    private final Pane dualContainer = new Pane() {
        @Override
        protected void layoutChildren() {
            double width = getWidth(), height = getHeight();
            double enteringHeight = enteringNode == null ? 0 : Math.min(height, enteringNode.prefHeight(width));
            double leavingHeight = leavingNode == null ? 0 : Math.min(height, leavingNode.prefHeight(width));
            Pos pos = circleAnimation ? Pos.TOP_CENTER : getAlignment();
            if (enteringNode != null) {
                layoutInArea(enteringNode, 0, 0, width, enteringHeight, 0, pos.getHpos(), pos.getVpos());
            }
            if (leavingNode != null) {
                if (circleAnimation || slideAnimation) {
                    layoutInArea(leavingNode, 0, 0, width, leavingHeight, 0, pos.getHpos(), pos.getVpos());
                } else if (direction == HPos.LEFT) { // transition from right to left => leaving node is on the left
                    layoutInArea(leavingNode, -width, 0, width, leavingHeight, 0, pos.getHpos(), pos.getVpos());
                } else { // transition from left to right => leaving node is on the right
                    layoutInArea(leavingNode,  width, 0, width, leavingHeight, 0, pos.getHpos(), pos.getVpos());
                }
            }
        }

        @Override
        public Orientation getContentBias() {
            Orientation bias = enteringNode == null ? null : enteringNode.getContentBias();
            if (bias == null && leavingNode != null) {
                bias = leavingNode.getContentBias();
            }
            return bias;
        }

        @Override
        protected double computeMinWidth(double height) {
            double enteringMinWidth = enteringNode == null ? 0 : enteringNode.minWidth(height);
            double leavingMinWidth  = leavingNode  == null ? 0 : leavingNode .minWidth(height);
            return isTransitingHorizontally() ? enteringMinWidth + leavingMinWidth : Math.max(enteringMinWidth, leavingMinWidth);
        }

        @Override
        protected double computeMinHeight(double width) {
            double enteringMinHeight = enteringNode == null ? 0 : enteringNode.minHeight(width);
            double leavingMinHeight  = leavingNode  == null ? 0 : leavingNode .minHeight(width);
            return Math.max(enteringMinHeight, leavingMinHeight);
        }

        @Override
        protected double computePrefWidth(double height) {
            double enteringPrefWidth = enteringNode == null ? 0 : enteringNode.prefWidth(height);
            double leavingPrefWidth =  leavingNode  == null ? 0 : leavingNode .prefWidth(height);
            return isTransitingHorizontally() ? enteringPrefWidth + leavingPrefWidth : Math.max(enteringPrefWidth, leavingPrefWidth);
        }

        @Override
        protected double computePrefHeight(double width) {
            double enteringPrefHeight = enteringNode == null ? 0 : enteringNode.prefHeight(width);
            double leavingPrefHeight  = leavingNode  == null ? 0 : leavingNode .prefHeight(width);
            return Math.max(enteringPrefHeight, leavingPrefHeight);
        }

        @Override
        protected double computeMaxWidth(double height) {
            double enteringMaxWidth = enteringNode == null ? 0 : enteringNode.maxWidth(height);
            double leavingMaxWidth  = leavingNode  == null ? 0 : leavingNode .maxWidth(height);
            return isTransitingHorizontally() ? enteringMaxWidth + leavingMaxWidth : Math.max(enteringMaxWidth, leavingMaxWidth);
        }

        @Override
        protected double computeMaxHeight(double width) {
            double enteringMaxHeight = enteringNode == null ? 0 : enteringNode.maxHeight(width);
            double leavingMaxHeight  = leavingNode  == null ? 0 : leavingNode .maxHeight(width);
            return Math.max(enteringMaxHeight, leavingMaxHeight);
        }
    };

    public static void setKeepsLeavingNode(Node node, boolean keepsLeavingNode) {
        node.getProperties().put("webfx-extras-keepsLeavingNode", keepsLeavingNode);
    }

    private static boolean isKeepsLeavingNode(Node node) {
        return Boolean.TRUE.equals(node.getProperties().get("webfx-extras-keepsLeavingNode"));
    }

    public TransitionPane() {
        this(null);
    }

    public TransitionPane(Node initialContent) {
        setContent(dualContainer);
        // enabling the clip only during the transition
        clipEnabledProperty().bind(transitingProperty);
        transitToContent(initialContent);
    }

    public HPos getDirection() {
        return direction;
    }

    public void setDirection(HPos direction) {
        this.direction = direction;
    }

    public boolean isSlideAnimation() {
        return slideAnimation;
    }

    public void setSlideAnimation(boolean slideAnimation) {
        this.slideAnimation = slideAnimation;
    }

    public boolean isCircleAnimation() {
        return circleAnimation;
    }

    public void setCircleAnimation(boolean circleAnimation) {
        this.circleAnimation = circleAnimation;
    }

    public boolean isAnimate() {
        return animate;
    }

    public void setAnimate(boolean animate) {
        this.animate = animate;
    }

    public boolean isAnimateFirstContent() {
        return animateFirstContent;
    }

    public void setAnimateFirstContent(boolean animateFirstContent) {
        this.animateFirstContent = animateFirstContent;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public boolean isScrollToTop() {
        return scrollToTop;
    }

    public void setScrollToTop(boolean scrollToTop) {
        this.scrollToTop = scrollToTop;
    }

    public boolean isKeepsLeavingNodes() {
        return keepsLeavingNodes;
    }

    public void setKeepsLeavingNodes(boolean keepsLeavingNodes) {
        this.keepsLeavingNodes = keepsLeavingNodes;
    }

    public boolean isTransiting() {
        return transitingProperty.get();
    }

    private boolean isTransitingHorizontally() {
        return !circleAnimation && isTransiting();
    }

    public ReadOnlyBooleanProperty transitingProperty() {
        return transitingProperty;
    }

    public Node getRequestedEnteringNode() {
        return requestedEnteringNodeProperty.get();
    }

    public ObjectProperty<Node> requestedEnteringNodeProperty() {
        return requestedEnteringNodeProperty;
    }

    public void transitToContent(Node enteringNode) {
        requestedEnteringNodeProperty.set(enteringNode);
    }

    public void replaceContentNoAnimation(Node content) {
        boolean oldAnimate = animate;
        setAnimate(false);
        transitToContent(content);
        setAnimate(oldAnimate);
    }

    private void onEnteringNodeRequested(Node newContent) {
        if (timeline != null) {
            timeline.jumpTo(timeline.getTotalDuration());
            timeline.stop();
            callTimelineOnFinishedIfFinished();
        }
        leavingNode = enteringNode;
        enteringNode = newContent;
        ObservableList<Node> dualChildren = dualContainer.getChildren();
        if (enteringNode == null)
            dualChildren.clear();
        else {
            if (keepsLeavingNodes)
                enteringNode.setVisible(true);
            if (!dualChildren.contains(enteringNode)) {
                dualChildren.add(enteringNode);
            }
            if (animate && (leavingNode != null || animateFirstContent))
                doAnimate(newContent);
            else {
                if (leavingNode != null) {
                    if (keepsLeavingNodes)
                        leavingNode.setVisible(false);
                    else
                        dualChildren.remove(leavingNode);
                }
            }
        }
    }

    private void doAnimate(Node newContent) {
        if (getWidth() == 0) { // may happen on first time this transition pane is displayed
            // In that case, we postpone the animation when the transition pane is resized (presumably on next layout pass)
            widthProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    widthProperty().removeListener(this);
                    doAnimate(newContent);
                }
            });
            return;
        }
        if (circleAnimation)
            doCircleClipTransition(newContent);
        else if (slideAnimation)
            doSlideClipTransition(newContent);
        else
            doTranslationTransition(newContent);
    }

    private void doTranslationTransition(Node newContent) {
        Node oldContent = leavingNode;
        double width = getWidth();
        // Preventing the leaving node to increase in height if the entering node is bigger, as this breaks the
        // smoothness of the transition animation (the next layout pass may suddenly move down that node)
        Region oldRegion = oldContent instanceof Region ? (Region) oldContent : null; // only for regions
        double oldRegionMaxHeight = -1; // memorising the previous max height (to reestablish it at transition end)
        if (oldRegion != null) {
            oldRegionMaxHeight = oldRegion.getMaxHeight();
            oldRegion.setMaxHeight(getHeight());
        }
        double initialTranslateX;
        // Setting the initial translation (final is always 0)
        if (direction == HPos.LEFT) // transition from right to left
            initialTranslateX = width; // new content entering from the right
        else { // transition from left to right
            initialTranslateX = -width; // new content entering from the left
        }
        double finalTranslateX = 0;
        if (reverse) {
            double swap = initialTranslateX;
            initialTranslateX = finalTranslateX;
            finalTranslateX = swap;
        }
        dualContainer.setTranslateX(initialTranslateX);
        transitingProperty.set(true);
        if (scrollToTop)
            Animations.scrollToTop(newContent, true);
        timeline = Animations.animateProperty(dualContainer.translateXProperty(), finalTranslateX);
        finishTimeline(newContent, oldContent, oldRegion, oldRegionMaxHeight);
    }

    private void doSlideClipTransition(Node newContent) {
        Node oldContent = leavingNode;
        double width = getWidth();
        // Preventing the leaving node to increase in height if the entering node is bigger, as this breaks the
        // smoothness of the transition animation (the next layout pass may suddenly move down that node)
        Region oldRegion = oldContent instanceof Region ? (Region) oldContent : null;
        Region newRegion = newContent instanceof Region ? (Region) newContent : null;
        double oldRegionMaxHeight = -1; // memorising the previous max height (to reestablish it at transition end)
        if (oldRegion != null) {
            oldRegionMaxHeight = oldRegion.getMaxHeight();
            oldRegion.setMaxHeight(getHeight());
        }

        DoubleProperty slideXProperty = new SimpleDoubleProperty(-1) {
            @Override
            protected void invalidated() {
                double slideX = Math.max(get(), 1);
                double height = Math.min(oldRegion == null ? getHeight() : oldRegion.getHeight(), newRegion == null ? getHeight() : newRegion.getHeight());
                Node frontNode = reverse ? oldContent : newContent;
                Node backNode  = reverse ? newContent : oldContent;
                if (frontNode != null) { // should be hidden is height = 0 on start
                    frontNode.setClip(new Rectangle(slideX, 0, width - slideX, height == 0 ? 1 : height));
                }
                if (backNode != null) { // should be visible if height = 0 on start
                    backNode.setClip(height == 0 ? null : new Rectangle(0, 0, slideX, height));
                }
            }
        };

        transitingProperty.set(true);
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
        if (scrollToTop)
            Animations.scrollToTop(newContent, true);
        timeline = Animations.animateProperty(slideXProperty, finalTranslateX, Duration.seconds(1), Interpolator.EASE_IN, true);
        finishTimeline(newContent, oldContent, oldRegion, oldRegionMaxHeight);
    }

    private void doCircleClipTransition(Node newContent) {
        Node oldContent = leavingNode;
        double width = getWidth();
        Region oldRegion = oldContent instanceof Region ? (Region) oldContent : null;
        Region newRegion = newContent instanceof Region ? (Region) newContent : null;
        double oldRegionMaxHeight = -1;
        if (oldRegion != null) {
            oldRegionMaxHeight = oldRegion.getMaxHeight();
            oldRegion.setMaxHeight(getHeight());
        }
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
                double height = Math.min(oldRegion == null ? getHeight() : oldRegion.getHeight(), newRegion == null ? getHeight() : newRegion.getHeight());
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
        transitingProperty.set(true);
        double initialRadius = 0;
        double finalRadius = (reverse ? 0.5 : 0.7) * Math.max(width, getHeight());
        if (reverse) {
            double swap = initialRadius;
            initialRadius = finalRadius;
            finalRadius = swap;
        }
        radiusProperty.set(initialRadius);
        if (scrollToTop)
            Animations.scrollToTop(newContent, false);
        timeline = Animations.animateProperty(radiusProperty, finalRadius, Duration.seconds(1), Interpolator.EASE_IN, true);
        finishTimeline(newContent, oldContent, oldRegion, oldRegionMaxHeight);
    }

    private void finishTimeline(Node newContent, Node oldContent, Region oldRegion, double oldRegionMaxHeight) {
        timeline.setOnFinished(e -> {
            newContent.setClip(null);
            if (oldContent != null)
                oldContent.setClip(null);
            // Reestablishing the previous max height of the leaving node
            if (oldRegion != null)
                oldRegion.setMaxHeight(oldRegionMaxHeight);
            if (oldContent != null) {
                if (oldContent != enteringNode) {
                    if (keepsLeavingNodes && isKeepsLeavingNode(oldContent))
                        oldContent.setVisible(false);
                    else
                        dualContainer.getChildren().remove(oldContent);
                }
                if (leavingNode == oldContent)
                    leavingNode = null;
            }
            if (enteringNode == newContent) {
                transitingProperty.set(false);
            }
        });
        callTimelineOnFinishedIfFinished();
    }

    private void callTimelineOnFinishedIfFinished() {
        if (Objects.equals(timeline.getCurrentTime(), timeline.getTotalDuration())) {
            timeline.getOnFinished().handle(null);
            timeline = null;
        }
    }
}
