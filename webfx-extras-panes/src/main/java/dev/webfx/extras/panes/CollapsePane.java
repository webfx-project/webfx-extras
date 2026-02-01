package dev.webfx.extras.panes;

import dev.webfx.extras.util.animation.Animations;
import dev.webfx.extras.util.layout.Layouts;
import dev.webfx.kit.util.aria.Aria;
import dev.webfx.kit.util.aria.AriaRole;
import dev.webfx.kit.util.properties.FXProperties;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.geometry.Pos;
import javafx.geometry.Side;
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
 * A Pane that can be collapsed (and then expanded) programmatically or through user interaction (see static methods).
 * This is the first version where collapse works correctly only for bottom and right slides. Other sides will be added later.
 *
 * @author Bruno Salmon
 */
public class CollapsePane extends MonoClipPane {

    private final ObjectProperty<Side> collapseSideProperty = new SimpleObjectProperty<>(Side.BOTTOM);

    private final BooleanProperty collapsedProperty = FXProperties.newBooleanProperty(collapsed -> {
        if (collapsed)
            doCollapse();
        else
            doExpand();
        // We make this pane disabled when collapsed so that none of its children can get the focus through the tab
        // keyboard navigation, until expanded again.
        setDisable(collapsed);
        Aria.setAriaExpanded(this, !collapsed);
    });

    private final BooleanProperty animateProperty = new SimpleBooleanProperty(true);
    private final BooleanProperty transitingProperty = new SimpleBooleanProperty();

    private Timeline timeline;
    private double expandedWidthOrHeight;
    private double widthOrHeightDuringCollapseAnimation;

    {
        Aria.setAriaRole(this, AriaRole.DISCLOSURE);
        Aria.setAriaExpanded(this, isExpanded());
    }

    public CollapsePane() {
    }

    public CollapsePane(Node content) {
        super(content);
    }

    public CollapsePane(Side side) {
        setCollapseSide(side);
    }

    public CollapsePane(Side side, Node content) {
        super(content);
        setCollapseSide(side);
    }

    @Override
    protected double getLayoutWidth() {
        if (timeline != null && isHorizontal())
            return widthOrHeightDuringCollapseAnimation;
        return super.getLayoutWidth();
    }

    @Override
    protected double getLayoutHeight() {
        // During the collapse animation, the layout is performed with the captured height of the content to make the
        // sliding effect (otherwise the layout would try to shrink the content to this CollapsePane height).
        // Even if the content height exceeds this CollapsePane height, it doesn't matter as it's clipped.
        if (timeline != null && isVertical())
            return widthOrHeightDuringCollapseAnimation;
        return super.getLayoutHeight();
    }

    private boolean isHorizontal() { // refers to the collapse direction
        return getCollapseSide().isVertical(); // refers to the rectangle side (i.e., LEFT or RIGHT)
    }

    private boolean isVertical() {
        return !isHorizontal();
    }

    public Side getCollapseSide() {
        return collapseSideProperty.get();
    }

    public ObjectProperty<Side> collapseSideProperty() {
        return collapseSideProperty;
    }

    public void setCollapseSide(Side side) {
        collapseSideProperty.set(side);
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

    public boolean isExpanded() {
        return !isCollapsed();
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

    public boolean isTransiting() {
        return transitingProperty.get();
    }

    public ReadOnlyBooleanProperty transitingProperty() {
        return transitingProperty;
    }

    private void doCollapse() {
        boolean isHorizontal = isHorizontal();
        if (timeline == null) {
            expandedWidthOrHeight = isHorizontal ? getWidth() : getHeight();
        }
        widthOrHeightDuringCollapseAnimation = expandedWidthOrHeight;
        if (isHorizontal)
            setPrefWidth(expandedWidthOrHeight);
        else
            setPrefHeight(expandedWidthOrHeight);
        animateWidthOrHeight(0);
    }

    private void doExpand() {
        setWidthOrHeightComputationMode(USE_COMPUTED_SIZE);
        if (isHorizontal()) {
            double height = getHeight();
            double minWidth = minWidth(height);
            double prefWidth = prefWidth(height);
            double maxWidth = maxWidth(height);
            expandedWidthOrHeight = Layouts.boundedSize(minWidth, prefWidth, maxWidth);
        } else {
            double width = getWidth();
            double minHeight = minHeight(width);
            double prefHeight = prefHeight(width);
            double maxHeight = maxHeight(width);
            expandedWidthOrHeight = Layouts.boundedSize(minHeight, prefHeight, maxHeight);
        }
        widthOrHeightDuringCollapseAnimation = expandedWidthOrHeight;
        if (expandedWidthOrHeight > 0) {
            animateWidthOrHeight(expandedWidthOrHeight);
        }
    }

    private void setWidthOrHeightComputationMode(double widthOrHeightComputationMode) {
        if (isHorizontal()) {
            if (widthOrHeightComputationMode == USE_COMPUTED_SIZE)
                setPrefWidth(USE_COMPUTED_SIZE);
            else {
                setMinWidth(widthOrHeightComputationMode);
                setMaxWidth(widthOrHeightComputationMode);
            }
        } else {
            if (widthOrHeightComputationMode == USE_COMPUTED_SIZE)
                setPrefHeight(USE_COMPUTED_SIZE);
            else {
                setMinHeight(widthOrHeightComputationMode);
                setMaxHeight(widthOrHeightComputationMode);
            }
        }
    }

    private void animateWidthOrHeight(double finalValue) {
        if (timeline != null)
            timeline.stop();
        setWidthOrHeightComputationMode(USE_PREF_SIZE);
        transitingProperty.set(true);
        // Making a translation effect for horizontal sliding (ex: Mobile left menu). TODO: add a boolean property for this effect
        if (content != null && isHorizontal() && isAnimate() && getCollapseSide() == Side.RIGHT) {
            if (finalValue > 0)
                content.translateXProperty().bind(widthProperty().subtract(finalValue));
            else
                content.translateXProperty().bind(widthProperty().subtract(getWidth()));
        }
        timeline = Animations.animateProperty(isHorizontal() ? prefWidthProperty() : prefHeightProperty(), finalValue);
        Animations.setOrCallOnTimelineFinished(timeline, e -> {
            if (content != null)
                content.translateXProperty().unbind();
            if (finalValue > 0) {
                setWidthOrHeightComputationMode(USE_COMPUTED_SIZE);
            }
            timeline = null;
            transitingProperty.set(false);
        });
    }

    //============================================== Static API ========================================================

    public static Node createPlainChevron(Paint stroke) {
        SVGPath chevron = new SVGPath();
        chevron.setContent("M 0,0 8,8 16,0");
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
        // Rotation of the chevron depending on the collapsed state
        animateChevron(chevron, collapsedProperty);
        // Hiding the circle and chevron on mouse exit if requested
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

    public static <N extends Node> N animateChevron(N chevron, CollapsePane collapsePane) {
        return animateChevron(chevron, collapsePane.collapsedProperty());
    }

    public static <N extends Node> N animateChevron(N chevron, BooleanProperty collapsedProperty) {
        // Rotation of the chevron depending on the collapsed state
        chevron.setRotate(collapsedProperty.get() ? 0 : 180); // initial state (no animation)
        FXProperties.runOnPropertyChange(collapsed -> // animating subsequent changes
                Animations.animateProperty(chevron.rotateProperty(), collapsed ? 0 : 180)
            , collapsedProperty);
        return chevron;
    }

    public static StackPane decorateCollapsePane(CollapsePane collapsePane, boolean hideDecorationOnMouseExit) {
        Node chevronNode = createCircledChevron();
        StackPane stackPane = new StackPane(collapsePane, chevronNode);
        // Drawing a 1 px borderline at the bottom.
        stackPane.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, null, new BorderWidths(0, 0, 1, 0))));
        StackPane.setAlignment(collapsePane, Pos.TOP_CENTER);
        // Because the circle and chevron will be half outside the stackPane, we ask stackPane to not manage them
        // (otherwise stackPane will grow in height to include them inside).
        chevronNode.setManaged(false);
        // We now manage their position ourselves to be in the middle of the bottom line
        FXProperties.runOnPropertiesChange(() -> {
            chevronNode.relocate(stackPane.getWidth() / 2 - chevronNode.getLayoutBounds().getWidth() / 2, stackPane.getHeight() - chevronNode.getLayoutBounds().getHeight() / 2);
            // Note: works perfectly in the browser, but the chevron is shifted 1 px to left in OpenJFX (don't know why)
        }, stackPane.widthProperty(), stackPane.heightProperty());
        // User interaction management: collapse/expand on circle click
        armChevron(chevronNode, collapsePane, hideDecorationOnMouseExit ? stackPane : null);
        return stackPane;
    }

    public static StackPane createDecoratedCollapsePane(Node content, boolean hideDecorationOnMouseExit) {
        return decorateCollapsePane(new CollapsePane(content), hideDecorationOnMouseExit);
    }

}
