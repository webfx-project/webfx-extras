package dev.webfx.extras.panes;

import dev.webfx.extras.util.animation.Animations;
import dev.webfx.extras.util.layout.LayoutUtil;
import dev.webfx.kit.util.properties.FXProperties;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

/**
 * A Pane that can be collapsed (and then expanded) programmatically, or through user interaction (see static methods).
 * This is a first version where collapse works only from bottom to top. Other directions will be added later.
 *
 * @author Bruno Salmon
 */
public class CollapsePane extends MonoClipPane {

    private final BooleanProperty collapsedProperty = FXProperties.newBooleanProperty(collapsed -> {
        if (collapsed)
            doCollapse();
        else
            doExpand();
    });

    private final BooleanProperty animateProperty = new SimpleBooleanProperty(true);

    private Timeline timeline;
    private double heightDuringCollapseAnimation;

    public CollapsePane() {
    }

    public CollapsePane(Node content) {
        super(content);
    }

    @Override
    protected double getLayoutHeight() {
        // During the collapse animation, the layout is performed with the captured height of the content in order to
        // make the sliding effect (otherwise the layout would try to shrink the content to this CollapsePane height).
        // Even if the content height exceeds this CollapsePane height, it doesn't matter as it's clipped.
        if (timeline != null)
            return heightDuringCollapseAnimation;
        return super.getLayoutHeight();
    }

    public boolean isCollapsed() {
        return collapsedProperty.get();
    }

    public BooleanProperty collapsedProperty() {
        return collapsedProperty;
    }

    public void setCollapsed(boolean collapsed) {
        collapsedProperty.set(collapsed);
    }

    public void collapse() {
        setCollapsed(true);
    }

    public void expand() {
        setCollapsed(false);
    }

    public void toggleCollapse() {
        setCollapsed(!isCollapsed());
    }

    public boolean isAnimate() {
        return animateProperty.get();
    }

    public BooleanProperty animateProperty() {
        return animateProperty;
    }

    public void setAnimate(boolean animate) {
        animateProperty.set(animate);
    }

    private void doCollapse() {
        heightDuringCollapseAnimation = getHeight();
        setPrefHeight(heightDuringCollapseAnimation);
        animateHeight(0);
    }

    private void doExpand() {
        setHeightComputationMode(USE_COMPUTED_SIZE);
        double minHeight  = minHeight(getWidth());
        double prefHeight = prefHeight(getWidth());
        double maxHeight  = maxHeight(getWidth());
        heightDuringCollapseAnimation = LayoutUtil.boundedSize(minHeight, prefHeight, maxHeight);
        if (heightDuringCollapseAnimation > 0) {
            animateHeight(heightDuringCollapseAnimation);
        }
    }

    private void setHeightComputationMode(double mode) {
        setMinHeight(mode);
        if (mode == USE_COMPUTED_SIZE)
            setPrefHeight(mode);
        setMaxHeight(mode);
    }

    private void animateHeight(double finalValue) {
        setHeightComputationMode(USE_PREF_SIZE);
        Animations.forceTimelineToFinish(timeline);
        timeline = Animations.animateProperty(prefHeightProperty(), finalValue, isAnimate());
        Animations.setOrCallOnTimelineFinished(timeline, e -> {
            if (finalValue > 0) {
                setHeightComputationMode(USE_COMPUTED_SIZE);
            }
            timeline = null;
        });
    }

    public static Node createPlainChevron(Paint stroke) {
        SVGPath chevron = new SVGPath();
        chevron.setContent("M 0,8 8,0 16,8");
        chevron.setFill(Color.TRANSPARENT);
        chevron.setStroke(stroke);
        chevron.setStrokeWidth(2);
        chevron.setStrokeLineCap(StrokeLineCap.ROUND);
        chevron.setStrokeLineJoin(StrokeLineJoin.ROUND);
        return chevron;
    }

    public static Node createBlackChevron() {
        return createPlainChevron(Color.BLACK);
    }

    public static Node createCircledChevron() {
        int radius = 16;
        Circle circle = new Circle(radius, Color.WHITE);
        circle.setStroke(Color.LIGHTGRAY);
        Node plainChevronNode = createPlainChevron(Color.LIGHTGRAY);
        plainChevronNode.setMouseTransparent(true);
        return new StackPane(circle, plainChevronNode);
    }

    public static <N extends Node> N armChevron(N chevron, CollapsePane collapsePane) {
        return armChevron(chevron, collapsePane, null);
    }

    public static <N extends Node> N armChevron(N chevron, CollapsePane collapsePane, Node hideChevronOnMouseExitNode) {
        return armChevron(chevron, collapsePane.collapsedProperty(), hideChevronOnMouseExitNode);
    }

    public static <N extends Node> N armChevron(N chevron, BooleanProperty collapsedProperty) {
        return armChevron(chevron, collapsedProperty, null);
    }

    public static <N extends Node> N armChevron(N chevron, BooleanProperty collapsedProperty, Node hideChevronOnMouseExitNode) {
        // User interaction management: collapse/expand on circle click
        chevron.setOnMouseClicked(e -> FXProperties.toggleProperty(collapsedProperty));
        chevron.setCursor(Cursor.HAND); // Note that in OpenJFX, only the part inside the StackPane is showing the hand cursor
        // Rotation animation of the chevron while collapsing or expanding
        FXProperties.runOnPropertyChange(collapsed ->
                Animations.animateProperty(chevron.rotateProperty(), collapsed ? 180 : 0)
            , collapsedProperty);
        // Hiding the circle & chevron on mouse exit if requested
        if (hideChevronOnMouseExitNode != null) {
            hideChevronOnMouseExitNode.setOnMouseEntered(e -> { // Note: works also on touch devices!
                chevron.setVisible(true);
            });
            // However, we always show them when the pane is collapsed (otherwise there is no way to expand it again)
            hideChevronOnMouseExitNode.setOnMouseExited(e -> {  // Note: works also on touch devices!
                chevron.setVisible(collapsedProperty.get());
            });
        }
        return chevron;
    }

    public static StackPane decorateCollapsePane(CollapsePane collapsePane, boolean hideDecorationOnMouseExit) {
        Node chevronNode = createCircledChevron();
        StackPane stackPane = new StackPane(collapsePane, chevronNode);
        // Drawing a 1px borderline at the bottom.
        stackPane.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, null, new BorderWidths(0, 0, 1, 0))));
        StackPane.setAlignment(collapsePane, Pos.TOP_CENTER);
        // Because the circle and chevron will be half outside of the stackPane, we ask stackPane to not manage them
        // (otherwise stackPane will grow in height to include them inside).
        chevronNode.setManaged(false);
        // We now manage their position ourselves to be in the middle of the bottom line
        FXProperties.runOnPropertiesChange(() -> {
            chevronNode.relocate(stackPane.getWidth() / 2 - chevronNode.getLayoutBounds().getWidth() / 2, stackPane.getHeight() - chevronNode.getLayoutBounds().getHeight() / 2);
            // Note: works perfectly in the browser, but the chevron is shifted 1px to left in OpenJFX (don't know why)
        }, stackPane.widthProperty(), stackPane.heightProperty());
        // User interaction management: collapse/expand on circle click
        armChevron(chevronNode, collapsePane, hideDecorationOnMouseExit ? stackPane : null);
        return stackPane;
    }

    public static StackPane createDecoratedCollapsePane(Node content, boolean hideDecorationOnMouseExit) {
        return decorateCollapsePane(new CollapsePane(content), hideDecorationOnMouseExit);
    }

}
