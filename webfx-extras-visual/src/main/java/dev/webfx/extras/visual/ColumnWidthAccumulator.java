package dev.webfx.extras.visual;

import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.platform.util.collection.Collections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

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
        double cellContentWidth = cellContent.prefWidth(-1);
        // Workaround for a responsive design bug when the table skin is restored (actually a new instance of it is
        // restored due to an OpenJFX bug - see VisualGridTableSkin.applyResponsiveLayout() ) => the controls inside
        // the column (such as Label) may not have their skin set yet, which cause them to return 0 for their prefWidth.
        // In that case, we invalidate the cached value when the skin is set to force the recomputation of the correct
        // final value subsequently.
        if (cellContentWidth == 0 && cellContent instanceof Control control) {
            Skin<?> skin = control.getSkin();
            if (skin == null) {
                FXProperties.onPropertySet(control.skinProperty(), skin2 -> {
                    hasChanged = true;
                });
            }
        }
        accumulate(cellContentWidth);
    }

    public void accumulate(double columnWidth) {
        if (columnWidth > maxWidth)
            maxWidth = columnWidth;
    }

    public double getMaxWidth() {
        return maxWidth;
    }
}