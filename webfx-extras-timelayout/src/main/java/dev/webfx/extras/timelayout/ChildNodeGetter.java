package dev.webfx.extras.timelayout;

import javafx.scene.Node;

public interface ChildNodeGetter<C> {

    Node getNode(C child);

}
