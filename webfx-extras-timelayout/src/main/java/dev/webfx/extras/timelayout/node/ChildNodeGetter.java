package dev.webfx.extras.timelayout.node;

import javafx.scene.Node;

public interface ChildNodeGetter<C> {

    Node getNode(C child);

}
