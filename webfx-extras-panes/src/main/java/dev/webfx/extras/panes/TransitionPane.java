package dev.webfx.extras.panes;

import dev.webfx.extras.util.animation.Animations;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.layout.Region;

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

    @Override
    protected void onContentChanged(Node newContent) {
        if (internalSync)
            return;
        if (content == null || newContent == null || !animate)
            super.onContentChanged(newContent);
        else {
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
            Region leavingNode = content instanceof Region ? (Region) content : null; // only for regions
            double leavingNodeMaxHeight; // memorising the previous max height (to reestablish it at transition end)
            if (leavingNode != null && animate) { // necessary only when animated
                leavingNodeMaxHeight = leavingNode.getMaxHeight();
                leavingNode.setMaxHeight(getHeight());
            } else
                leavingNodeMaxHeight = -1;
            if (direction == HPos.LEFT) // transition from right to left
                cp.getChildren().setAll(content, newContent); // new content entering from the right
            else { // transition from left to right
                cp.getChildren().setAll(newContent, content); // new content entering from the left
                cp.setTranslateX(-w);
            }
            super.onContentChanged(cp);
            transitingProperty.set(true);
            Animations.scrollToTop(newContent, false);
            Timeline timeline = Animations.animateProperty(cp.translateXProperty(), direction == HPos.LEFT ? -w : 0, animate);
            EventHandler<ActionEvent> onFinished = e -> {
                if (content == cp) {
                    transitingProperty.set(false);
                    super.onContentChanged(newContent);
                }
                // Reestablishing the previous max height of the leaving node
                if (leavingNode != null)
                    leavingNode.setMaxHeight(leavingNodeMaxHeight);
            };
            if (timeline != null)
                timeline.setOnFinished(onFinished);
            else // happens when animate is false
                onFinished.handle(null);
        }
    }
}
