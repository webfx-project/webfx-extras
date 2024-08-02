package dev.webfx.extras.panes;

import dev.webfx.extras.util.animation.Animations;
import javafx.animation.Interpolator;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
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
    private ColumnsPane columnsPane;
    private final BooleanProperty transitingProperty = new SimpleBooleanProperty();
    private boolean animate = true;
    private boolean circleAnimation = false;
    private Timeline timeline;

    public TransitionPane() {
        this(null);
    }

    public TransitionPane(Node content) {
        super(content);
        // enabling the clip only during the transition
        clipEnabledProperty().bind(transitingProperty);
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

    public ReadOnlyBooleanProperty transitingProperty() {
        return transitingProperty;
    }

    public void setContentImmediately(Node content) {
        boolean oldAnimate = animate;
        setAnimate(false);
        setContent(content);
        setAnimate(oldAnimate);
    }

    @Override
    protected void onContentChanged(Node newContent) {
        if (internalSync)
            return;
        if (timeline != null) {
            timeline.stop();
            timeline.getOnFinished().handle(null);
            timeline = null;
        }
        if (content == null || newContent == null || !animate)
            super.onContentChanged(newContent);
        else {
            if (circleAnimation)
                doCircleClipTransition(newContent);
            else
                doHorizontalTranslationTransition(newContent);
        }
    }

    private void doHorizontalTranslationTransition(Node newContent) {
        Node oldContent = content;
        double w = getWidth();
        if (columnsPane != null)
            columnsPane.getChildren().clear();
        ColumnsPane cp = columnsPane = new ColumnsPane();
        cp.setMinWidth(w);
        cp.setPrefWidth(w);
        cp.setMaxWidth(w);
        cp.setFixedColumnWidth(w);
        cp.setAlignment(getAlignment());
        // Preventing the leaving node to increase in height if the entering node is bigger, as this breaks the
        // smoothness of the transition animation (the next layout pass may suddenly move down that node)
        Region leavingNode = oldContent instanceof Region ? (Region) oldContent : null; // only for regions
        double leavingNodeMaxHeight; // memorising the previous max height (to reestablish it at transition end)
        if (leavingNode != null) { // necessary only when animated
            leavingNodeMaxHeight = leavingNode.getMaxHeight();
            leavingNode.setMaxHeight(getHeight());
        } else
            leavingNodeMaxHeight = -1;
        if (direction == HPos.LEFT) // transition from right to left
            cp.getChildren().setAll(oldContent, newContent); // new content entering from the right
        else { // transition from left to right
            cp.getChildren().setAll(newContent, oldContent); // new content entering from the left
            cp.setTranslateX(-w);
        }
        super.onContentChanged(cp);
        transitingProperty.set(true);
        Animations.scrollToTop(newContent, true);
        timeline = Animations.animateProperty(cp.translateXProperty(), direction == HPos.LEFT ? -w : 0);
        timeline.setOnFinished(e -> {
                if (content == cp) {
                    super.onContentChanged(newContent);
                    transitingProperty.set(false);
                }
                // Reestablishing the previous max height of the leaving node
                if (leavingNode != null)
                    leavingNode.setMaxHeight(leavingNodeMaxHeight);
            });
    }

    private void doCircleClipTransition(Node newContent) {
        Node oldContent = content;
        double width = getWidth();
        StackPane stackPane = new StackPane(oldContent, newContent);
        Region oldRegion = oldContent instanceof Region ? (Region) oldContent : null;
        Region newRegion = newContent instanceof Region ? (Region) newContent : null;
        double oldMaxHeight;
        if (oldRegion != null) {
            oldMaxHeight = oldRegion.getMaxHeight();
            oldRegion.setMaxHeight(getHeight());
        } else
            oldMaxHeight = -1;
        stackPane.setAlignment(Pos.TOP_CENTER);
        super.onContentChanged(stackPane);
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
                if (content == stackPane) {
                    super.onContentChanged(newContent);
                    transitingProperty.set(false);
                }
            });
    }
}
