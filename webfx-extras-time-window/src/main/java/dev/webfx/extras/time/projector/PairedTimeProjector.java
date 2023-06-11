package dev.webfx.extras.time.projector;

import javafx.application.Platform;
import javafx.scene.Node;

import java.time.temporal.TemporalUnit;

/**
 * @author Bruno Salmon
 */
public class PairedTimeProjector<T> implements TimeProjector<T> {

    private final TimeProjector<T> otherTimeProjector;
    private final Node otherNode, thisNode;
    private double cachedNodesDeltaX = Double.NaN;

    public PairedTimeProjector(TimeProjector<T> otherTimeProjector, Node otherNode, Node thisNode) {
        this.otherTimeProjector = otherTimeProjector;
        this.otherNode = otherNode;
        this.thisNode = thisNode;
    }

    private double nodesDeltaX() {
        if (Double.isNaN(cachedNodesDeltaX)) {
            cachedNodesDeltaX = thisNode.localToScene(0, 0).getX() - otherNode.localToScene(0, 0).getX();
            Platform.runLater(() -> cachedNodesDeltaX = Double.NaN);
        }
        return cachedNodesDeltaX;
    }

    @Override
    public double timeToX(T time, boolean start, boolean exclusive) {
        return otherTimeProjector.timeToX(time, start, exclusive) - nodesDeltaX();
    }

    @Override
    public T xToTime(double x) {
        return otherTimeProjector.xToTime(x + nodesDeltaX());
    }

    @Override
    public TemporalUnit getTemporalUnit() {
        return otherTimeProjector.getTemporalUnit();
    }
}
