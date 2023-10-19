package dev.webfx.extras.util.control;

import dev.webfx.extras.panes.ScalePane;
import dev.webfx.extras.util.layout.LayoutUtil;
import dev.webfx.kit.launcher.WebFxKitLauncher;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.platform.util.Numbers;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;

import java.util.function.Predicate;

/**
 * @author Bruno Salmon
 */
public class ControlUtil {

    public static ScrollPane createVerticalScrollPane(Region content) {
        return setupVerticalScrollPane(createScrollPane(), content);
    }

    public static ScrollPane createVerticalScrollPaneWithPadding(Region content) {
        return createVerticalScrollPane(LayoutUtil.createPadding(content));
    }

    public static ScrollPane setupVerticalScrollPane(ScrollPane scrollPane, Region content) {
        scrollPane.setContent(LayoutUtil.setMinMaxWidthToPref(content));
        double verticalScrollbarExtraWidth = WebFxKitLauncher.getVerticalScrollbarExtraWidth();
        if (verticalScrollbarExtraWidth == 0)
            content.prefWidthProperty().bind(scrollPane.widthProperty());
        else
            content.prefWidthProperty().bind(
                    // scrollPane.widthProperty().subtract(verticalScrollbarExtraWidth) // doesn't compile with GWT
                    FXProperties.compute(scrollPane.widthProperty(), width -> Numbers.toDouble(width.doubleValue() - verticalScrollbarExtraWidth))
            );
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        registerParentScrollPaneProperty(scrollPane);
        return scrollPane;
    }

    public static ScrollPane createScalableVerticalScrollPane(Region content, boolean stretchWidth) {
        ScalePane scalePane = new ScalePane(content);
        scalePane.setCanShrink(false);
        scalePane.setFillWidth(false);
        scalePane.setFillHeight(false);
        scalePane.setScaleRegion(true);
        scalePane.setStretchWidth(stretchWidth);
        /*scalePane.setOnMouseClicked(e -> {
            scalePane.setScaleEnabled(!scalePane.isScaleEnabled());
        });*/
        ScrollPane scrollPane = createVerticalScrollPane(scalePane);
        FXProperties.runOnPropertiesChange(p -> {
            Bounds viewportBounds = scrollPane.getViewportBounds();
            double width = viewportBounds.getWidth();
            double height = viewportBounds.getHeight();
            scalePane.setFixedSize(width, height);
            scalePane.setVAlignment(height > content.prefHeight(width) ? VPos.CENTER : VPos.TOP);
        }, scrollPane.viewportBoundsProperty());
        return scrollPane;
    }

    public static ScrollPane createScrollPane(Node content) {
        ScrollPane scrollPane = createScrollPane();
        scrollPane.setContent(content);
        return scrollPane;
    }

    public static ScrollPane createScrollPane() {
        ScrollPane scrollPane = new ScrollPane();
        registerParentScrollPaneProperty(scrollPane);
        return scrollPane;
    }

    private static void registerParentScrollPaneProperty(ScrollPane scrollPane) {
        Node content = scrollPane.getContent();
        if (content != null)
            content.getProperties().put("webfx-parentScrollPane", scrollPane); // Used by findScrollPaneAncestor()
    }

    public static ScrollPane findScrollPaneAncestor(Node node) {
        while (true) {
            if (node == null)
                return null;
            // Assuming ScrollPane has been created using createScrollPane() which stores the scrollPane into "webfx-parentScrollPane" node property
            ScrollPane parentScrollPane = (ScrollPane) node.getProperties().get("webfx-parentScrollPane");
            if (parentScrollPane != null)
                return parentScrollPane;
            node = node.getParent();
            if (node instanceof ScrollPane)
                return (ScrollPane) node;
        }
    }

    public static double computeScrollPaneHoffset(ScrollPane scrollPane) {
        double hmin = scrollPane.getHmin();
        double hmax = scrollPane.getHmax();
        double hvalue = scrollPane.getHvalue();
        double contentWidth = scrollPane.getContent().getLayoutBounds().getWidth();
        double viewportWidth = scrollPane.getViewportBounds().getWidth();
        double hoffset = Math.max(0, contentWidth - viewportWidth) * (hvalue - hmin) / (hmax - hmin);
        return hoffset;
    }

    public static double computeScrollPaneVoffset(ScrollPane scrollPane) {
        double vmin = scrollPane.getVmin();
        double vmax = scrollPane.getVmax();
        double vvalue = scrollPane.getVvalue();
        double contentHeight = scrollPane.getContent().getLayoutBounds().getHeight();
        double viewportHeight = scrollPane.getViewportBounds().getHeight();
        double voffset = Math.max(0, contentHeight - viewportHeight) * (vvalue - vmin) / (vmax - vmin);
        return voffset;
    }

    public static Node lookupChild(Node node, Predicate<Node> predicate) {
        if (node != null) {
            if (predicate.test(node))
                return node;
            if (node instanceof Parent) {
                ObservableList<Node> children = node instanceof SplitPane ? ((SplitPane) node).getItems() : ((Parent) node).getChildrenUnmodifiable();
                for (Node child : children) {
                    Node n = lookupChild(child, predicate);
                    if (n != null)
                        return n;
                }
            }
        }
        return null;
    }

}
