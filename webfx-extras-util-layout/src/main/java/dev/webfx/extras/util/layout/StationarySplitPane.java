package dev.webfx.extras.util.layout;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * @author Bruno Salmon
 */
public final class StationarySplitPane {

    private final static Background TRANSPARENT_BACKGROUND = new Background(new BackgroundFill(Color.TRANSPARENT, null, null));

    private final SplitPane splitPane;

    private final StackPane stackPaneContainer;

    public StationarySplitPane(Node leftNode, Node rightStationatyNode) {
        Region rightTransparentRegion = new Region();
        rightTransparentRegion.setBackground(TRANSPARENT_BACKGROUND);
        splitPane = new SplitPane(leftNode, rightTransparentRegion);
        splitPane.setBackground(TRANSPARENT_BACKGROUND);
        splitPane.setPadding(Insets.EMPTY);
        stackPaneContainer = new StackPane(rightStationatyNode, splitPane);
        stackPaneContainer.addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
            boolean hoveringRightRegion = e.getX() > splitPane.getDividerPositions()[0] * splitPane.getWidth() + 10;
            splitPane.setMouseTransparent(hoveringRightRegion);
        });
    }

    public SplitPane getSplitPane() {
        return splitPane;
    }

    public StackPane getStackPaneContainer() {
        return stackPaneContainer;
    }

    public static StationarySplitPane createRightStationarySplitPane(Node leftNode, Node rightStationatyNode) {
        return new StationarySplitPane(leftNode, rightStationatyNode);
    }

    public static StackPane createRightStationarySplitPaneAndReturnStackPaneContainer(Node leftNode, Node rightStationatyNode) {
        return createRightStationarySplitPane(leftNode, rightStationatyNode).getStackPaneContainer();
    }

}
