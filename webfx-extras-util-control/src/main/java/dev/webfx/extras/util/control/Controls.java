package dev.webfx.extras.util.control;

import dev.webfx.extras.panes.MonoPane;
import dev.webfx.extras.panes.ScalePane;
import dev.webfx.extras.util.animation.Animations;
import dev.webfx.extras.util.layout.Layouts;
import dev.webfx.kit.launcher.WebFxKitLauncher;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.platform.useragent.UserAgent;
import javafx.animation.Interpolator;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Bruno Salmon
 */
public final class Controls {

    public static ScrollPane createVerticalScrollPane(Region content) {
        return setupVerticalScrollPane(createScrollPane(), content);
    }

    public static ScrollPane createVerticalScrollPaneWithPadding(double padding, Region content) {
        return createVerticalScrollPane(Layouts.createPadding(content, padding));
    }

    public static ScrollPane setupVerticalScrollPane(ScrollPane scrollPane, Region content) {
        scrollPane.setContent(Layouts.setMinMaxWidthToPref(content));
        double verticalScrollbarExtraWidth = WebFxKitLauncher.getVerticalScrollbarExtraWidth();
        content.prefWidthProperty().bind(scrollPane.widthProperty().map(width -> {
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
        FXProperties.runOnPropertyChange(viewportBounds -> {
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

    public static void onScrollPaneAncestorSet(Node node, Consumer<ScrollPane> scrollPaneConsumer) {
        FXProperties.onPropertySet(node.sceneProperty(), scene -> {
            ScrollPane scrollPane = Controls.findScrollPaneAncestor(node);
            if (scrollPane != null) {
                scrollPaneConsumer.accept(scrollPane);
            }
        });
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
        ScrollPane scrollPane = Controls.findScrollPaneAncestor(node);
        if (scrollPane != null) {
            double contentHeight = scrollPane.getContent().getLayoutBounds().getHeight();
            double viewportHeight = scrollPane.getViewportBounds().getHeight();
            double nodeHeight = node.getLayoutBounds().getHeight();
            double sceneHeight = node.getScene().getHeight();
            VPos wishedPosition = getVerticalScrollNodeWishedPosition(node);
            double wishedSceneNodeTop = wishedPosition == VPos.TOP ? 0 // sceneHeight / 1.618 - nodeHeight / 2
                : wishedPosition == VPos.BOTTOM ? sceneHeight - nodeHeight
                : sceneHeight / 2 - nodeHeight / 2;
            double currentScrollPaneSceneTop = scrollPane.localToScene(0, 0).getY();
            wishedSceneNodeTop = Layouts.boundedSize(wishedSceneNodeTop, currentScrollPaneSceneTop, currentScrollPaneSceneTop + viewportHeight);
            double currentNodeSceneTop = node.localToScene(0, 0).getY();
            double currentViewportSceneTop = computeScrollPaneVTopOffset(scrollPane);
            double wishedViewportSceneTop = currentViewportSceneTop + currentNodeSceneTop - wishedSceneNodeTop;
            //Console.log("👉👉👉👉👉 viewportBounds = " + scrollPane.getViewportBounds() + ", contentHeight = " + contentHeight + ", viewportHeight = " + viewportHeight + ", nodeHeight = " + nodeHeight + ", sceneHeight = " + sceneHeight + ", currentScrollPaneSceneTop = " + currentScrollPaneSceneTop + ", currentNodeSceneTop = " + currentNodeSceneTop + ", currentViewportSceneTop = " + currentViewportSceneTop + ", wishedViewportSceneTop = " + wishedViewportSceneTop);
            double vValue = wishedViewportSceneTop / (contentHeight - viewportHeight);
            vValue = Layouts.boundedSize(vValue, 0, 1);
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
        // But calling setPrefSize() in addition works with WebFX.
        // Another strange issue: calling setMinSize() causes pi to be in the left top corner (observed on a button)
        // until the window is resized (then pi is correctly positioned during the layout pass).
        //pi.setMinSize(size, size); // Commented for the above reason. TODO investigate why
        pi.setPrefSize(size, size);
        pi.setMaxSize(size, size);
        return pi;
    }

    public static Region createSpinner(double size) {
        return createSpinner(size, 0);
    }

    public static Region createSpinner(double size, double padding) {
        SVGPath svgPath = new SVGPath();
        svgPath.setContent("M48.8.7C45.1.8 41.3 1.3 37.6 2.3 20.6 6.9 7.2 20.2 2.6 37.3 2.2 38.8 2.5 40.4 3.2 41.7 4 43.1 5.3 44.1 6.8 44.5 9.9 45.3 13.1 43.4 13.9 40.3 17.4 27.3 27.6 17.1 40.6 13.6 50.2 11 60.3 12.4 68.7 17.1L64.5 19.5C63 20.4 62.1 22 62 23.7 62 25.5 62.8 27.1 64.3 28.1L88.8 44.2C90.1 45.1 91.7 45.1 93.1 44.4 94.4 43.6 95.2 42.1 95.1 40.6L93.3 11.3C93.2 9.5 92.2 8 90.7 7.1 89.2 6.3 87.3 6.3 85.8 7.2L80.1 10.5C71 3.8 60 .3 48.8.7ZM8.8 55.2C7.6 55.1 6.5 55.6 5.7 56.4 4.9 57.2 4.5 58.4 4.6 59.5L6.3 88.8C6.4 90.5 7.4 92.1 9 92.9 10.5 93.8 12.4 93.7 13.9 92.9L19.6 89.6C31.6 98.6 47.3 101.7 62 97.8 79.1 93.2 92.5 79.8 97 62.8 97.9 59.7 96 56.5 92.9 55.6 91.4 55.2 89.8 55.4 88.5 56.2 87.1 57 86.1 58.3 85.7 59.8 82.3 72.8 72.1 83 59 86.5 49.4 89.1 39.4 87.7 30.9 83L35.1 80.6C36.6 79.7 37.6 78.1 37.7 76.4 37.7 74.6 36.8 73 35.4 72L10.9 55.9C10.3 55.4 9.5 55.2 8.8 55.2Z");
        svgPath.getStyleClass().add("webfx-extras-spinner");
        MonoPane monoPane = new MonoPane(svgPath);
        Layouts.setFixedSize(monoPane, 100, 100);
        ScalePane scalePane = new ScalePane(monoPane);
        Layouts.setFixedSize(scalePane, size, size);
        Region spinner = padding == 0 ? scalePane : Layouts.setPadding(new MonoPane(scalePane), padding);
        if (!UserAgent.isBrowser()) { // Web CSS manages rotation in the browser
            // Programmatic animation for OpenJFX
            Timeline[] rotationTimeline = { null };
            IntegerProperty rotationCountProperty = new SimpleIntegerProperty();
            FXProperties.runOnPropertiesChange(() -> {
                boolean shouldRotate = spinner.getScene() != null && spinner.isVisible();
                boolean isRotating = rotationTimeline[0] != null && !rotationTimeline[0].getStatus().equals(Timeline.Status.STOPPED);
                if (isRotating && !shouldRotate) {
                    rotationTimeline[0].stop();
                } else if (shouldRotate && !isRotating) {
                    rotationTimeline[0] = Animations.animateProperty(spinner.rotateProperty(), 360 * (rotationCountProperty.get() + 1), Interpolator.LINEAR);
                    Animations.setOrCallOnTimelineFinished(rotationTimeline[0], e -> rotationCountProperty.set(rotationCountProperty.get() + 1));
                }
            }, spinner.sceneProperty(), spinner.visibleProperty(), rotationCountProperty);
        }
        return spinner;
    }

    public static Region createButtonSizeSpinner() {
        return createSpinner(16); // Or should it be 20 (previous value in AsyncSpinner)
    }

    public static Region createSectionSizeSpinner() {
        return createSpinner(32); // was 50
    }

    public static Region createDialogSizeSpinner() {
        return createSpinner(64, 64);
    }

    public static Region createPageSizeSpinner() {
        return createSpinner(80, 80);
    }

    public static void onSkinReady(Control control, Runnable runnable) {
        onSkinReady(control, skin -> runnable.run());
    }

    public static void onSkinReady(Control control, Consumer<Skin<?>> consumer) {
        FXProperties.onPropertySet(control.skinProperty(), consumer);
    }

    public static void setHtmlInputType(TextField textField, HtmlInputType type) {
        setHtmlInputType(textField, type.name().toLowerCase().replace('_', '-'));
    }

    public static void setHtmlInputType(TextField textField, String type) {
        textField.getProperties().put("webfx-html-input-type", type); // Will be considered by HtmlTextFieldPeer
    }

    public static void setHtmlInputAutocomplete(TextField textField, HtmlInputAutocomplete autocomplete) { // TODO html accept several values => vararg & map to comma separated
        setHtmlInputAutocomplete(textField, autocomplete.name().toLowerCase().replace('_', '-'));
    }

    public static void setHtmlInputAutocomplete(TextField textField, String autocomplete) {
        textField.getProperties().put("webfx-html-input-autocomplete", autocomplete); // Will be considered by HtmlTextFieldPeer
    }

    public static void setHtmlInputTypeAndAutocompleteToEmail(TextField textField) {
        setHtmlInputType(textField, HtmlInputType.EMAIL);
        setHtmlInputAutocomplete(textField, HtmlInputAutocomplete.EMAIL);
    }

    public static void setHtmlInputTypeAndAutocompleteToTel(TextField textField) {
        setHtmlInputType(textField, HtmlInputType.TEL);
        setHtmlInputAutocomplete(textField, HtmlInputAutocomplete.TEL);
    }

    static {
        Animations.setScrollPaneAncestorFinder(Controls::findScrollPaneAncestor);
        Animations.setScrollPaneValuePropertyGetter(node -> {
            if (node instanceof ScrollPane)
                return ((ScrollPane) node).vvalueProperty();
            return null;
        });
        Animations.setComputeVerticalScrollNodeWishedValueGetter(Controls::computeVerticalScrollNodeWishedValue);
    }

    public static <L extends Labeled> L setupTextWrapping(L labeled, boolean autoWrap, boolean ellipsis) {
        if (autoWrap) {
            labeled.setWrapText(true);
            // Note that setting wrapText = true is not enough to make the labeled automatically wrap the text, the other
            // condition being to constraint either 1) the width or 2) the height of the labeled as follows:
            // 1) labeled.prefWidthProperty().bind(vBox.widthProperty());
            // 2) labeled.setMinHeight(Region.USE_PREF_SIZE);
            // We choose 2) as it doesn't need a reference to the container. In addition, it's not sure that the labeled
            // needs to have the exact same width as the container (it may have margin or be laid out differently).
            // So 2) is definitely more generic.
            labeled.setMinHeight(Region.USE_PREF_SIZE);
            // This is the general default behavior for "wrappedLabel" and "ellipsisLabel", but some renderers may
            // wish a different behavior. In that case, they can call removePossibleLabelAutoWrap() method (see for
            // example VisualGridTableSkin.setCellContent() method).
        }
        if (ellipsis)
            labeled.getStyleClass().add("ellipsis");
        return labeled;
    }
}
