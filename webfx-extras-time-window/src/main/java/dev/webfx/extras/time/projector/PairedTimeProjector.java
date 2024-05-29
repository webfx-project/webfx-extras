package dev.webfx.extras.time.projector;

import javafx.application.Platform;
import javafx.scene.Node;

/**
 * @author Bruno Salmon
 */
public class PairedTimeProjector<T> extends TranslatedTimeProjector<T> {

    private final Node otherNode, thisNode;
    private double cachedNodesDeltaX = Double.NaN;

    public PairedTimeProjector(TimeProjector<T> otherTimeProjector, Node otherNode, Node thisNode) {
        super(otherTimeProjector);
        this.otherNode = otherNode;
        this.thisNode = thisNode;
    }

    @Override
    public double getTranslateX() {
        if (Double.isNaN(cachedNodesDeltaX)) {
            cachedNodesDeltaX = thisNode.localToScene(0, 0).getX() - otherNode.localToScene(0, 0).getX();
            Platform.runLater(() -> cachedNodesDeltaX = Double.NaN);
        }
        return cachedNodesDeltaX;
    }

}
