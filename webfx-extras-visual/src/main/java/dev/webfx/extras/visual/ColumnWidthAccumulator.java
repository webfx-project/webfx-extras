package dev.webfx.extras.visual;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import dev.webfx.platform.util.collection.Collections;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bruno Salmon
 */
public final class ColumnWidthAccumulator {

    private double maxWidth;
    private boolean hasChanged;
    private List<ObservableList<Node>> severalColumnNodes;
    private final ListChangeListener<Node> columnNodesListener = c -> hasChanged = true;

    public void registerColumnNodes(ObservableList<Node> columnNodes) {
        if (severalColumnNodes == null)
            severalColumnNodes = new ArrayList<>();
        if (Collections.noneMatch(severalColumnNodes, cn -> cn == columnNodes)) {
            severalColumnNodes.add(columnNodes);
            columnNodes.addListener(columnNodesListener);
        }
        hasChanged = true;
        maxWidth = 0;
    }

    public void update() {
        if (hasChanged) {
            for (ObservableList<Node> columnNodes : severalColumnNodes)
                for (Node node : columnNodes)
                    if (node.getScene() == null)
                        break;
                    else
                        accumulate(node);
            hasChanged = false;
        }
    }

    public void accumulate(Node cellContent) {
        accumulate(cellContent.prefWidth(-1));
    }

    public void accumulate(double columnWidth) {
        if (columnWidth > maxWidth)
            maxWidth = columnWidth;
    }

    public double getMaxWidth() {
        return maxWidth;
    }
}