package dev.webfx.extras.time.projector;

import dev.webfx.platform.uischeduler.UiScheduler;
import javafx.scene.Node;

/**
 * @author Bruno Salmon
 */
public class PairedTimeProjector<T> extends TranslatedTimeProjector<T> {

    private final Node otherNode, thisNode;
    private double nodesDeltaX;
    private int nodesDeltaXAnimationFrameNumber;

    public PairedTimeProjector(TimeProjector<T> otherTimeProjector, Node otherNode, Node thisNode) {
        super(otherTimeProjector);
        this.otherNode = otherNode;
        this.thisNode = thisNode;
    }

    @Override
    public double getTranslateX() {
        int animationFrameNumber = UiScheduler.getAnimationFrameNumber();
        if (nodesDeltaXAnimationFrameNumber != animationFrameNumber) { // might be dirty
            nodesDeltaX = thisNode.localToScene(0, 0).getX() - otherNode.localToScene(0, 0).getX();
            nodesDeltaXAnimationFrameNumber = animationFrameNumber;
        }
        return nodesDeltaX;
    }

}
