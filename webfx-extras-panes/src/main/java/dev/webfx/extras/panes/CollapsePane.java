package dev.webfx.extras.panes;

import dev.webfx.extras.util.animation.Animations;
import dev.webfx.kit.util.properties.FXProperties;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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

    private double heightDuringCollapseAnimation = -1;

    public CollapsePane() {
    }

    public CollapsePane(Node content) {
        super(content);
    }

    private final BooleanProperty collapsedProperty = new SimpleBooleanProperty() {
        @Override
        protected void invalidated() {
            if (get())
                doCollapse();
            else
                doExpand();
        }
    };

    @Override
    protected double getLayoutHeight() {
        if (heightDuringCollapseAnimation > 0)
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

    private void doCollapse() {
        heightDuringCollapseAnimation = getHeight();
        setMinHeight(USE_PREF_SIZE);
        setMaxHeight(USE_PREF_SIZE);
        setPrefHeight(heightDuringCollapseAnimation);
        Animations.animateProperty(prefHeightProperty(), 0);
    }

    private void doExpand() {
        Animations.animateProperty(prefHeightProperty(), heightDuringCollapseAnimation)
                .setOnFinished(e -> {
                    heightDuringCollapseAnimation = -1;
                    setPrefHeight(USE_COMPUTED_SIZE);
                });
    }

    public static StackPane decorateCollapsePane(CollapsePane collapsePane, boolean hideDecorationOnMouseExit) {
        int radius = 16;
        Circle circle = new Circle(radius, Color.WHITE);
        circle.setStroke(Color.LIGHTGRAY);
        SVGPath chevron = new SVGPath();
        chevron.setContent("M 0,8 8,0 16,8");
        chevron.setStrokeWidth(2);
        chevron.setStrokeLineCap(StrokeLineCap.ROUND);
        chevron.setStrokeLineJoin(StrokeLineJoin.ROUND);
        chevron.setStroke(Color.LIGHTGRAY);
        chevron.setFill(Color.TRANSPARENT);
        StackPane stackPane = new StackPane(collapsePane, circle, chevron);
        // Drawing a 1px borderline at the bottom.
        stackPane.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, null, new BorderWidths(0, 0, 1, 0))));
        StackPane.setAlignment(collapsePane, Pos.TOP_CENTER);
        // Because the circle and chevron will be half outside of the stackPane, we ask stackPane to not manage them
        // (otherwise stackPane will grow in height to include them inside).
        circle.setManaged(false);
        chevron.setManaged(false);
        // We now manage their position ourselves to be in the middle of the bottom line
        FXProperties.runOnPropertiesChange(() -> {
            circle.relocate( stackPane.getWidth() / 2 - radius, stackPane.getHeight() - radius);
            chevron.relocate(stackPane.getWidth() / 2 - chevron.getLayoutBounds().getWidth() / 2, stackPane.getHeight() - chevron.getLayoutBounds().getHeight() / 2);
            // Note: works perfectly in the browser, but the chevron is shifted 1px to left in OpenJFX (don't know why)
        }, stackPane.widthProperty(), stackPane.heightProperty());
        // User interaction management: collapse/expand on circle click
        circle.setOnMouseClicked(e -> collapsePane.toggleCollapse());
        circle.setCursor(Cursor.HAND);
        chevron.setMouseTransparent(true);
        // Rotation animation of the chevron while collapsing or expanding
        FXProperties.runOnPropertiesChange(() ->
            Animations.animateProperty(chevron.rotateProperty(), collapsePane.isCollapsed() ? 180 : 0)
        , collapsePane.collapsedProperty);
        // Hiding the circle & chevron on mouse exit if requested
        if (hideDecorationOnMouseExit) {
            stackPane.setOnMouseEntered(e -> { // Note: works also on touch devices!
                circle.setVisible(true);
                chevron.setVisible(true);
            });
            // However, we always show them when the pane is collapsed (otherwise there is no way to expand it again)
            stackPane.setOnMouseExited(e -> {  // Note: works also on touch devices!
                circle.setVisible(collapsePane.isCollapsed());
                chevron.setVisible(collapsePane.isCollapsed());
            });
        }
        return stackPane;
    }

    public static StackPane createDecoratedCollapsePane(Node content, boolean hideDecorationOnMouseExit) {
        return decorateCollapsePane(new CollapsePane(content), hideDecorationOnMouseExit);
    }

}
