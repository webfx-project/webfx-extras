package dev.webfx.extras.panes;

import dev.webfx.extras.util.animation.Animations;
import javafx.animation.Timeline;
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
        return columnsPane != null;
    }

    @Override
    protected void onContentChanged(Node newContent) {
        if (internalSync)
            return;
        if (content == null || newContent == null)
            super.onContentChanged(newContent);
        else {
            double w = getWidth(), h = getHeight();
            if (columnsPane != null)
                columnsPane.getChildren().clear();
            ColumnsPane cp = columnsPane = new ColumnsPane();
            cp.setFixedColumnWidth(w);
            if (direction == HPos.LEFT)
                cp.getChildren().setAll(content, newContent);
            else {
                cp.getChildren().setAll(newContent, content);
                cp.setTranslateX(-w);
            }
            cp.setMinSize(w, h);
            cp.setPrefSize(w, h);
            cp.setMaxSize(w, h);
            internalSync = true;
            getChildren().setAll(content = cp);
            internalSync = false;
            Timeline timeline = Animations.animateProperty(cp.translateXProperty(), direction == HPos.LEFT ? -w : 0);
            timeline.setOnFinished(e -> {
                        if (content == cp) {
                            columnsPane = null;
                            internalSync = true;
                            setContent(newContent);
                            getChildren().setAll(content = newContent);
                            internalSync = false;
                        }
                    });
        }
    }
}
