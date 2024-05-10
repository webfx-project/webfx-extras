package dev.webfx.extras.panes;

import dev.webfx.extras.util.animation.Animations;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.HPos;
import javafx.scene.Node;

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

    public TransitionPane() {
    }

    public TransitionPane(Node content) {
        super(content);
    }

    public HPos getDirection() {
        return direction;
    }

    public void setDirection(HPos direction) {
        this.direction = direction;
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
        if (content == null || newContent == null)
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
            if (direction == HPos.LEFT) // transition from right to left
                cp.getChildren().setAll(content, newContent); // new content entering from the right
            else { // transition from left to right
                cp.getChildren().setAll(newContent, content); // new content entering from the left
                cp.setTranslateX(-w);
            }
            super.onContentChanged(cp);
            transitingProperty.set(true);
            Timeline timeline = Animations.animateProperty(cp.translateXProperty(), direction == HPos.LEFT ? -w : 0);
            timeline.setOnFinished(e -> {
                        if (content == cp) {
                            transitingProperty.set(false);
                            super.onContentChanged(newContent);
                        }
                    });
            Animations.scrollToTop(newContent);
        }
    }
}
