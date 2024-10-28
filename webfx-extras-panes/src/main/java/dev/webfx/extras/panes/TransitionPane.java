package dev.webfx.extras.panes;

import dev.webfx.extras.panes.transitions.CircleTransition;
import dev.webfx.extras.panes.transitions.FadeTransition;
import dev.webfx.extras.panes.transitions.Transition;
import dev.webfx.extras.panes.transitions.TranslateTransition;
import dev.webfx.extras.util.animation.Animations;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

/**
 * This Pane creates an animation when changing its content, so the old node looks like leaving, and the new node looks
 * like entering. For now, only horizontal translation animation is supported (from left to right or right to left).
 *
 * @author Bruno Salmon
 */
public final class TransitionPane extends MonoClipPane {

    private final ObjectProperty<Node> requestedEnteringNodeProperty = new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
            onEnteringNodeRequested(get());
        }
    };
    private final BooleanProperty transitingProperty = new SimpleBooleanProperty();
    private Transition transition = new TranslateTransition();
    private Node enteringNode, leavingNode;

    private boolean animate = true;
    private boolean animateFirstContent = false;
    private boolean reverse;
    private boolean keepsLeavingNodes = false;
    private boolean scrollToTop = false;
    private Timeline transitionTimeline;
    private Timeline scrollToTopTimeline;

    private final Pane dualContainer = new Pane() {
        @Override
        protected void layoutChildren() {
            double width = getWidth(), height = getHeight();
            double enteringHeight = enteringNode == null ? 0 : Math.min(height, enteringNode.prefHeight(width));
            double leavingHeight = leavingNode == null ? 0 : Math.min(height, leavingNode.prefHeight(width));
            // Temporary hack to make account page correctly laid out with circle & fade transition
            Pos pos = transition instanceof CircleTransition || transition instanceof FadeTransition ? Pos.TOP_CENTER : getAlignment();
            if (enteringNode != null) {
                layoutInArea(enteringNode, 0, 0, width, enteringHeight, 0, pos.getHpos(), pos.getVpos());
            }
            if (leavingNode != null) {
                if (transition instanceof TranslateTransition) {
                    // transition from right to left => leaving node is on the left
                    if (((TranslateTransition) transition).getDirection() == HPos.LEFT) {
                        layoutInArea(leavingNode, -width, 0, width, leavingHeight, 0, pos.getHpos(), pos.getVpos());
                    } else { // transition from left to right => leaving node is on the right
                        layoutInArea(leavingNode,  width, 0, width, leavingHeight, 0, pos.getHpos(), pos.getVpos());
                    }
                } else {
                    layoutInArea(leavingNode, 0, 0, width, leavingHeight, 0, pos.getHpos(), pos.getVpos());
                }
            }
            if (scrollToTop && scrollToTopTimeline == null && enteringHeight > 0) {
                // Postponing to ensure the layout bound is set on the possible target node for scrollTop
                Platform.runLater(() -> {
                    scrollToTopTimeline = Animations.scrollToTop(enteringNode, transition.shouldVerticalScrollBeAnimated());
                });
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

    public void setUnmanagedDuringTransition() {
        // This is a performance optimization that can be activated in some cases for transitions triggering layouts
        // such as TranslateTransition. It sets the dual container as unmanaged during the transition, which stops the
        // layout propagation between its transiting children and the rest of the scene (this solved the slow
        // TranslateTransition on mobiles when the dual container was in a HtmlScrollPanePeer in SIMPLE_CSS mode).
        // To avoid however if the transiting children have images not yet loaded, because if these images are loaded
        // before the end of the transition, they won't be positioned correctly during the transition.
        dualContainer.managedProperty().bind(transitingProperty.not());
    }

    public Transition getTransition() {
        return transition;
    }

    public void setTransition(Transition transition) {
        this.transition = transition;
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
        return isTransiting() && transition instanceof TranslateTransition;
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
        if (transitionTimeline != null) {
            transitionTimeline.jumpTo(transitionTimeline.getTotalDuration());
            transitionTimeline.stop();
            Animations.callTimelineOnFinishedIfFinished(transitionTimeline);
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
        Node oldContent = leavingNode;
        // Preventing the leaving node to increase in height if the entering node is bigger, as this breaks the
        // smoothness of the transition animation (the next layout pass may suddenly move down that node)
        Region oldRegion = oldContent instanceof Region ? (Region) oldContent : null;
        Region newRegion = newContent instanceof Region ? (Region) newContent : null;
        double oldRegionMaxHeight = -1; // memorising the previous max height (to reestablish it at transition end)
        if (oldRegion != null) {
            oldRegionMaxHeight = oldRegion.getMaxHeight();
            oldRegion.setMaxHeight(getHeight());
        }
        rescheduleScrollTopTimeline();
        transitionTimeline = transition.createAndStartTransitionTimeline(oldContent, newContent, oldRegion, newRegion, dualContainer, this::getWidth, this::getHeight, reverse);
        transitingProperty.set(true);
        finishTimeline(newContent, oldContent, oldRegion, oldRegionMaxHeight);
    }

    private void rescheduleScrollTopTimeline() {
        if (scrollToTop) {
            if (scrollToTopTimeline != null)
                scrollToTopTimeline.stop();
            scrollToTopTimeline = null;
        }
    }

    private void finishTimeline(Node newContent, Node oldContent, Region oldRegion, double oldRegionMaxHeight) {
        transitionTimeline.setOnFinished(e -> {
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
                    else {
                        dualContainer.getChildren().remove(oldContent);
                        rescheduleScrollTopTimeline();
                    }
                }
                if (leavingNode == oldContent)
                    leavingNode = null;
            }
            if (enteringNode == newContent) {
                transitingProperty.set(false);
            }
        });
        Animations.callTimelineOnFinishedIfFinished(transitionTimeline);
    }

}
