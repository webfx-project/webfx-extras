package dev.webfx.extras.panes;

import dev.webfx.extras.util.animation.Animations;
import javafx.animation.Interpolator;
import javafx.animation.Timeline;
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
    private boolean circleAnimation = false;
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
                if (circleAnimation) {
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
            timeline.stop();
            timeline.getOnFinished().handle(null);
            timeline = null;
        }
        leavingNode = enteringNode;
        enteringNode = newContent;
        ObservableList<Node> dualChildren = dualContainer.getChildren();
        if (enteringNode == null)
            dualChildren.clear();
        else if (leavingNode == null || !animate)
            dualChildren.setAll(enteringNode);
        else {
            //dualChildren.setAll(leavingNode, enteringNode);
            if (!dualChildren.contains(enteringNode))
                dualChildren.add(enteringNode);
            if (circleAnimation)
                doCircleClipTransition(newContent);
            else
                doHorizontalTranslationTransition(newContent);
        }
    }

    private void doHorizontalTranslationTransition(Node newContent) {
        Node oldContent = leavingNode;
        double w = getWidth();
        // Preventing the leaving node to increase in height if the entering node is bigger, as this breaks the
        // smoothness of the transition animation (the next layout pass may suddenly move down that node)
        Region leavingRegion = oldContent instanceof Region ? (Region) oldContent : null; // only for regions
        double leavingNodeMaxHeight; // memorising the previous max height (to reestablish it at transition end)
        if (leavingRegion != null) { // necessary only when animated
            leavingNodeMaxHeight = leavingRegion.getMaxHeight();
            leavingRegion.setMaxHeight(getHeight());
        } else
            leavingNodeMaxHeight = -1;
        // Setting the initial translation (final is always 0)
        if (direction == HPos.LEFT) // transition from right to left
            dualContainer.setTranslateX(+w); // new content entering from the right
        else { // transition from left to right
            dualContainer.setTranslateX(-w); // new content entering from the left
        }
        transitingProperty.set(true);
        Animations.scrollToTop(newContent, true);
        timeline = Animations.animateProperty(dualContainer.translateXProperty(), 0);
        timeline.setOnFinished(e -> {
            if (enteringNode == newContent) {
                //dualContainer.getChildren().setAll(newContent);
                dualContainer.getChildren().remove(leavingNode);
                leavingNode = null;
                transitingProperty.set(false);
            }
            // Reestablishing the previous max height of the leaving node
            if (leavingRegion != null)
                leavingRegion.setMaxHeight(leavingNodeMaxHeight);
        });
    }

    private void doCircleClipTransition(Node newContent) {
        Node oldContent = leavingNode;
        double width = getWidth();
        Region oldRegion = oldContent instanceof Region ? (Region) oldContent : null;
        Region newRegion = newContent instanceof Region ? (Region) newContent : null;
        double oldMaxHeight;
        if (oldRegion != null) {
            oldMaxHeight = oldRegion.getMaxHeight();
            oldRegion.setMaxHeight(getHeight());
        } else
            oldMaxHeight = -1;
        Duration duration = Duration.seconds(1);

        DoubleProperty radiusProperty = new SimpleDoubleProperty(-1) {
            @Override
            protected void invalidated() {
                double height = Math.min(oldRegion == null ? getHeight() : oldRegion.getHeight(), newRegion == null ? getHeight() : newRegion.getHeight());
                Bounds lb = newContent.getLayoutBounds();
                newContent.setClip(new Circle(lb.getWidth() / 2, height / 2, get()));
                lb = oldContent.getLayoutBounds();
                Rectangle rectangle = new Rectangle();
                rectangle.setWidth(width);
                rectangle.setHeight(height);
                oldContent.setClip(Shape.subtract(rectangle, new Circle(lb.getWidth() / 2, height / 2, get())));
            }
        };
        transitingProperty.set(true);
        Animations.scrollToTop(newContent, false);
        radiusProperty.set(0);
        timeline = Animations.animateProperty(radiusProperty, 0.7 * Math.max(width, getHeight()), duration, Interpolator.EASE_IN);
        timeline.setOnFinished(e -> {
            oldContent.setClip(null);
            newContent.setClip(null);
            if (oldRegion != null)
                oldRegion.setMaxHeight(oldMaxHeight);
            if (enteringNode == newContent) {
                //dualContainer.getChildren().setAll(newContent);
                dualContainer.getChildren().remove(leavingNode);
                leavingNode = null;
                transitingProperty.set(false);
            }
        });
    }
}
