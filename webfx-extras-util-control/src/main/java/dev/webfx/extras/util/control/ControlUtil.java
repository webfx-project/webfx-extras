package dev.webfx.extras.util.control;

import dev.webfx.extras.panes.ScalePane;
import dev.webfx.extras.util.animation.Animations;
import dev.webfx.extras.util.layout.LayoutUtil;
import dev.webfx.kit.launcher.WebFxKitLauncher;
import dev.webfx.kit.util.properties.FXProperties;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ProgressIndicator;
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

    public static ScrollPane createVerticalScrollPaneWithPadding(double padding, Region content) {
        return createVerticalScrollPane(LayoutUtil.createPadding(content, padding));
    }

    public static ScrollPane setupVerticalScrollPane(ScrollPane scrollPane, Region content) {
        scrollPane.setContent(LayoutUtil.setMinMaxWidthToPref(content));
        double verticalScrollbarExtraWidth = WebFxKitLauncher.getVerticalScrollbarExtraWidth();
        content.prefWidthProperty().bind(
              FXProperties.compute(scrollPane.widthProperty(), width -> {
                  double contentWidth = width.doubleValue() - verticalScrollbarExtraWidth;
                  double maxWidth = content.getMaxWidth();
                  if (maxWidth > 0 && contentWidth > maxWidth)
                      contentWidth = maxWidth;
                  return contentWidth;
              })
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

    public static double computeScrollPaneHLeftOffset(ScrollPane scrollPane) {
        return computeScrollPaneHOffset(scrollPane, false);
    }

    public static double computeScrollPaneHRightOffset(ScrollPane scrollPane) {
        return computeScrollPaneHOffset(scrollPane, true);
    }

    public static double computeScrollPaneHOffset(ScrollPane scrollPane, boolean addViewportWidth) {
        double hmin = scrollPane.getHmin();
        double hmax = scrollPane.getHmax();
        double hvalue = scrollPane.getHvalue();
        double contentWidth = scrollPane.getContent().getLayoutBounds().getWidth();
        double viewportWidth = scrollPane.getViewportBounds().getWidth();
        double hoffset = Math.max(0, contentWidth - viewportWidth) * (hvalue - hmin) / (hmax - hmin);
        if (addViewportWidth)
            hoffset += viewportWidth;
        return hoffset;
    }

    public static double computeScrollPaneVTopOffset(ScrollPane scrollPane) {
        return computeScrollPaneVOffset(scrollPane, false);
    }

    public static double computeScrollPaneVBottomOffset(ScrollPane scrollPane) {
        return computeScrollPaneVOffset(scrollPane, true);
    }

    public static double computeScrollPaneVOffset(ScrollPane scrollPane, boolean addViewportHeight) {
        double vmin = scrollPane.getVmin();
        double vmax = scrollPane.getVmax();
        double vvalue = scrollPane.getVvalue();
        double contentHeight = scrollPane.getContent().getLayoutBounds().getHeight();
        double viewportHeight = scrollPane.getViewportBounds().getHeight();
        double voffset = Math.max(0, contentHeight - viewportHeight) * (vvalue - vmin) / (vmax - vmin);
        if (addViewportHeight)
            voffset += viewportHeight;
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

    public static double computeVerticalScrollNodeWishedValue(Node node) {
        ScrollPane scrollPane = ControlUtil.findScrollPaneAncestor(node);
        if (scrollPane != null) {
            double contentHeight = scrollPane.getContent().getLayoutBounds().getHeight();
            double viewportHeight = scrollPane.getViewportBounds().getHeight();
            double nodeHeight = node.getLayoutBounds().getHeight();
            double sceneHeight = node.getScene().getHeight();
            VPos wishedPosition = getVerticalScrollNodeWishedPosition(node);
            double wishedSceneNodeTop = wishedPosition == VPos.TOP ? sceneHeight / 1.618 - nodeHeight / 2
                : wishedPosition == VPos.BOTTOM ? sceneHeight - nodeHeight
                : sceneHeight / 2 - nodeHeight / 2;
            double currentScrollPaneSceneTop = scrollPane.localToScene(0, 0).getY();
            wishedSceneNodeTop = LayoutUtil.boundedSize(wishedSceneNodeTop, currentScrollPaneSceneTop, currentScrollPaneSceneTop + viewportHeight);
            double currentNodeSceneTop = node.localToScene(0, 0).getY();
            double currentViewportSceneTop = computeScrollPaneVTopOffset(scrollPane);
            double wishedViewportSceneTop = currentViewportSceneTop + currentNodeSceneTop - wishedSceneNodeTop;
            //Console.log("👉👉👉👉👉 viewportBounds = " + scrollPane.getViewportBounds() + ", contentHeight = " + contentHeight + ", viewportHeight = " + viewportHeight + ", nodeHeight = " + nodeHeight + ", sceneHeight = " + sceneHeight + ", currentScrollPaneSceneTop = " + currentScrollPaneSceneTop + ", currentNodeSceneTop = " + currentNodeSceneTop + ", currentViewportSceneTop = " + currentViewportSceneTop + ", wishedViewportSceneTop = " + wishedViewportSceneTop);
            double vValue = wishedViewportSceneTop / (contentHeight - viewportHeight);
            vValue = LayoutUtil.boundedSize(vValue, 0, 1);
            return vValue;
        }
        return 0;
    }

    public static void setVerticalScrollNodeWishedPosition(Node node, VPos wishedPosition) {
        node.getProperties().put("verticalScrollNodeWishedPosition", wishedPosition);
    }

    public static VPos getVerticalScrollNodeWishedPosition(Node node) {
        Object wishedPosition = node.getProperties().get("verticalScrollNodeWishedPosition");
        return wishedPosition instanceof VPos ? (VPos) wishedPosition : VPos.CENTER;
    }

    public static ProgressIndicator createProgressIndicator(double size) {
        ProgressIndicator pi = new ProgressIndicator();
        // Note: calling setMaxSize() is enough with OpenJFX but not with WebFX (in the browser) TODO investigate why
        // Anyway, we set all min/pref/max to the passed size
        pi.setMinSize(size, size);
        pi.setPrefSize(size, size);
        pi.setMaxSize(size, size);
        return pi;
    }

    static {
        Animations.setScrollPaneAncestorFinder(ControlUtil::findScrollPaneAncestor);
        Animations.setScrollPaneValuePropertyGetter(node -> {
            if (node instanceof ScrollPane)
                return ((ScrollPane) node).vvalueProperty();
            return null;
        });
        Animations.setComputeVerticalScrollNodeWishedValueGetter(ControlUtil::computeVerticalScrollNodeWishedValue);
    }

}
