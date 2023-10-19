package dev.webfx.extras.panes;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

/**
 * @author Bruno Salmon
 */
public class PrefRatioBorderPane extends BorderPane implements HasPrefRatio {

    @Override
    public void setPrefRatio(double prefRatio) {
        Node center = getCenter();
        if (center instanceof HasPrefRatio) {
            Node top = getTop();
            if (top != null) {
                double topHeight = top.prefHeight(-1);
                Insets margin = getMargin(top);
                if (margin != null)
                    topHeight += margin.getTop() + margin.getBottom();
                double newRation = prefRatio / (1 - topHeight / getHeight());
                System.out.println("RatioBorderPane.prefRatio: " + prefRatio + " -> " + newRation);
                prefRatio = newRation;
            }
            ((HasPrefRatio) center).setPrefRatio(prefRatio);
        }
    }
}
