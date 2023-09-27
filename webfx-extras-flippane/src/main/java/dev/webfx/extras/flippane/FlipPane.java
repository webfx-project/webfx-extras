package dev.webfx.extras.flippane;

import dev.webfx.extras.util.animation.Animations;
import dev.webfx.kit.util.properties.FXProperties;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;


/**
 * User: hansolo
 * Date: 16.04.14
 * Time: 22:06
 */
public class FlipPane extends StackPane {

    private final ObjectProperty<Node> frontProperty = new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
            onNodesChanged();
        }
    };

    private final ObjectProperty<Node> backProperty = new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
            onNodesChanged();
        }
    };

    private final StackPane frontStack = new StackPane();
    private final StackPane backStack = new StackPane();

    private final Rotate rotate = new Rotate(0);
    private final Rotate backRotate = new Rotate(180);
    private Duration flipDuration = Duration.millis(700);
    private Timeline flipTimeline;

    private final ObjectProperty<Orientation> flipDirectionProperty = new SimpleObjectProperty<>() {
        @Override
        public void addListener(InvalidationListener listener) {
            updateRotates();
        }
    };


    // ******************** Constructors **************************************
    public FlipPane() {
        this(Orientation.HORIZONTAL);
    }

    public FlipPane(Orientation flipDirection) {
        getChildren().setAll(backStack, frontStack);
        getTransforms().add(rotate);
        backStack.getTransforms().add(backRotate);
        setFlipDirection(flipDirection);
        FXProperties.runOnPropertiesChange(this::updateRotates, frontStack.widthProperty(), frontStack.heightProperty(), backStack.widthProperty(), backStack.heightProperty());
        FXProperties.runOnPropertiesChange(this::updateVisibilities, rotate.angleProperty());
    }

    private void updateVisibilities() {
        boolean showingFront = isShowingFront();
        frontStack.setVisible(showingFront);
        backStack.setVisible(!showingFront);
    }

    public Duration getFlipDuration() {
        return flipDuration;
    }

    public void setFlipDuration(Duration flipDuration) {
        this.flipDuration = flipDuration;
    }

    public Orientation getFlipDirection() { return flipDirectionProperty.get(); }

    public void setFlipDirection(Orientation flipDirection) {
        flipDirectionProperty.set(flipDirection);
    }

    public ObjectProperty<Orientation> flipDirectionProperty() {
        return flipDirectionProperty;
    }

    public Node getFront() {
        return frontProperty.get();
    }

    public ObjectProperty<Node> frontProperty() {
        return frontProperty;
    }

    public void setFront(Node front) {
        this.frontProperty.set(front);
    }

    public Node getBack() {
        return backProperty.get();
    }

    public ObjectProperty<Node> backProperty() {
        return backProperty;
    }

    public void setBack(Node back) {
        this.backProperty.set(back);
    }

    public boolean isShowingFront() { return rotate.angleProperty().doubleValue() <= 90; }
    public boolean isShowingBack() { return !isShowingFront(); }

    private void onNodesChanged() {
        Node front = getFront(), back = getBack();
        if (front == null)
            frontStack.getChildren().clear();
        else
            frontStack.getChildren().setAll(front);
        if (back == null)
            backStack.getChildren().clear();
        else
            backStack.getChildren().setAll(back);
        updateVisibilities();
        updateRotates();
    }

    private void flip(boolean toFront) {
        double endAngle = toFront ? 0 : 180;
        if (flipTimeline != null)
            flipTimeline.stop();
        flipTimeline = Animations.animateProperty(rotate.angleProperty(), endAngle, flipDuration);
        if (flipTimeline != null) {
            frontStack.setCache(true);
            frontStack.setCacheHint(CacheHint.ROTATE);
            backStack.setCache(true);
            backStack.setCacheHint(CacheHint.ROTATE);

            flipTimeline.setOnFinished(event -> {
                frontStack.setCache(false);
                backStack.setCache(false);
            });
        }
    }

    public void flipToFront() {
        flip(true);
    }

    public void flipToBack() {
        flip(false);
    }

    public void flip() {
        flip(!isShowingFront());
    }

    private void updateRotates() {
        double width  = Math.max(frontStack.getWidth(), backStack.getWidth());
        double height = Math.max(frontStack.getHeight(), backStack.getHeight());
        if (width < 0 || height < 0)
            return;
        setPrefSize(width, height);

        if (getFlipDirection() == Orientation.HORIZONTAL) {
            rotate.setAxis(Rotate.Y_AXIS);
            rotate.setPivotX(0.5 * width);
            rotate.setPivotY(0);
        } else {
            rotate.setAxis(Rotate.X_AXIS);
            rotate.setPivotX(0);
            rotate.setPivotY(0.5 * height);
        }
        backRotate.setAxis(rotate.getAxis());
        backRotate.setPivotX(rotate.getPivotX());
        backRotate.setPivotY(rotate.getPivotY());
    }

}
