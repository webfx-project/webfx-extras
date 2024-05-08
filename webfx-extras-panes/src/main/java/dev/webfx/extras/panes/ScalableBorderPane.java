package dev.webfx.extras.panes;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

/**
 * @author Bruno Salmon
 */
public final class ScalableBorderPane extends BorderPane implements Scalable {

    @Override
    public void prepareScale(double additionalContentHeight, ScaleComputer scaleComputer) {
        Node center = getCenter();
        if (center instanceof Scalable) {
            Node top = getTop();
            if (top != null) {
                additionalContentHeight += top.prefHeight(-1);
                Insets margin = getMargin(top);
                if (margin != null)
                    additionalContentHeight += margin.getTop() + margin.getBottom();
            }
            Insets margin = getMargin(center);
            if (margin != null)
                additionalContentHeight += margin.getTop() + margin.getBottom();
            ((Scalable) center).prepareScale(additionalContentHeight, scaleComputer);
        }
    }
}
