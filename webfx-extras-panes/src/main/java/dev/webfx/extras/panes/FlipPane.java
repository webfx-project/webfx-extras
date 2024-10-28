package dev.webfx.extras.panes;

import dev.webfx.extras.util.animation.Animations;
import dev.webfx.kit.util.properties.FXProperties;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.geometry.Point3D;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.util.Duration;


/**
 * @author Bruno Salmon
 */
public final class FlipPane extends StackPane {

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

    private final MonoPane frontPane = new MonoPane();
    private final MonoPane backPane = new MonoPane();

    // Alternative scale mode that can be used to solve issue with Safari (there is a Safari bug that happens with
    // rotates in some cases => the front & back panes are invisible while flipping). However, the scale mode has some
    // issues when the back & front nodes are also scaled by the application code.
    private final boolean useScaleInsteadOfRotate;
    private final Rotate globalRotate;
    private final Scale globalScale;
    private final Transform globalTransform;
    private final Rotate backRotate;
    private final Scale backScale;
    private final Transform backTransform;
    private Duration flipDuration = Duration.millis(700);
    private Timeline flipTimeline;

    private final ObjectProperty<Orientation> flipDirectionProperty = new SimpleObjectProperty<>() {
        @Override
        public void addListener(InvalidationListener listener) {
            updateRotatesAxisAndPivot(false);
        }
    };


    // ******************** Constructors **************************************
    public FlipPane() {
        this(Orientation.HORIZONTAL);
    }

    public FlipPane(Orientation flipDirection) {
        this(flipDirection, false);
    }

    public FlipPane(boolean useScaleInsteadOfRotate) {
        this(Orientation.HORIZONTAL, useScaleInsteadOfRotate);
    }

    public FlipPane(Orientation flipDirection, boolean useScaleInsteadOfRotate) {
        this.useScaleInsteadOfRotate = useScaleInsteadOfRotate;
        globalRotate = useScaleInsteadOfRotate ? null : new Rotate(0);
        globalScale = useScaleInsteadOfRotate ? new Scale(1, 1) : null;
        globalTransform = useScaleInsteadOfRotate ? globalScale : globalRotate;
        backRotate = useScaleInsteadOfRotate ? null : new Rotate(180);
        backScale = useScaleInsteadOfRotate ? new Scale(-1, 1) : null;
        backTransform = useScaleInsteadOfRotate ? backScale : backRotate;
        getChildren().setAll(backPane, frontPane);
        setFlipDirection(flipDirection);
        FXProperties.runOnPropertiesChange(() -> updateRotatesAxisAndPivot(false), frontPane.widthProperty(), frontPane.heightProperty(), backPane.widthProperty(), backPane.heightProperty());
        FXProperties.runOnPropertiesChange(this::updateVisibilities, globalRotateProperty());
    }

    private DoubleProperty globalRotateProperty() {
        return useScaleInsteadOfRotate ? globalScale.xProperty() : globalRotate.angleProperty();
    }

    private void applyRotates(boolean flipFinished) {
        // Removing rotates when not necessary (ie flip finished)
        if (flipFinished) {
            // When flip is finished, we can remove the rotates because only one side is visible and this side is either
            // with rotate = 0 (front) or rotate = 180 + 180 = 360 (back).
            getTransforms().remove(globalTransform);
            backPane.getTransforms().clear();
            // Removing rotates makes the graph simpler to render, which can solve issues with some browsers, and also
            // on mobiles when there is a WebView inside the front or back slide (the WebView won't be correctly
            // attached if there is a non-zero rotate, ex: 360 doesn't work).
        } else if (backPane.getTransforms().isEmpty()) {
            getTransforms().add(globalTransform);
            backPane.getTransforms().setAll(backTransform);
        }
    }

    private void updateVisibilities() {
        boolean showingFront = isShowingFront();
        frontPane.setVisible(showingFront);
        backPane.setVisible(!showingFront);
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

    public boolean isShowingFront() {
        if (useScaleInsteadOfRotate)
            return globalScale.getX() >= 0;
        return globalRotate.angleProperty().doubleValue() <= 90;
    }

    public boolean isShowingBack() {
        return !isShowingFront();
    }

    private void onNodesChanged() {
        frontPane.setContent(getFront());
        backPane.setContent(getBack());
        updateVisibilities();
        updateRotatesAxisAndPivot(false);
    }

    private void flip(boolean toFront, Runnable onFinished) {
        double endValue = useScaleInsteadOfRotate ? (toFront ? 1 : -1) : (toFront ? 0 : 180);
        double currentValue = globalRotateProperty().get();
        if (currentValue == endValue)
            return;
        if (flipTimeline != null) {
            flipTimeline.jumpTo(flipTimeline.getTotalDuration());
            flipTimeline.stop();
            Animations.callTimelineOnFinishedIfFinished(flipTimeline);
        }
        updateRotatesAxisAndPivot(true);
        applyRotates(false);
        flipTimeline = Animations.animateProperty(globalRotateProperty(), endValue, flipDuration); // new scale version
        frontPane.setCache(true);
        backPane.setCache(true);
        frontPane.setCacheHint(CacheHint.ROTATE);
        backPane.setCacheHint(CacheHint.ROTATE);

        flipTimeline.setOnFinished(event -> {
            frontPane.setCache(false);
            backPane.setCache(false);
            onFlipFinished(onFinished);
        });
        Animations.callTimelineOnFinishedIfFinished(flipTimeline);
    }

    private void onFlipFinished(Runnable onFinished) {
        updateRotatesAxisAndPivot(false);
        applyRotates(true);
        if (onFinished != null)
            onFinished.run();
    }

    public void flipToFront() {
        flipToFront(null);
    }

    public void flipToFront(Runnable onFinished) {
        flip(true, onFinished);
    }

    public void flipToBack() {
        flipToBack(null);
    }

    public void flipToBack(Runnable onFinished) {
        flip(false, onFinished);
    }

    public void flip() {
        flip(null);
    }

    public void flip(Runnable onFinished) {
        flip(!isShowingFront(), onFinished);
    }

    private void updateRotatesAxisAndPivot(boolean freezePrefSize) {
        double width  = Math.max(frontPane.getWidth(),  backPane.getWidth());
        double height = Math.max(frontPane.getHeight(), backPane.getHeight());
        if (width < 0 || height < 0)
            return;
        if (freezePrefSize)
            setPrefSize(width, height);
        else
            setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);

        if (getFlipDirection() == Orientation.HORIZONTAL) {
            setRotatesPivot(0.5, 0);
        } else {
            setRotatesPivot(0, 0.5);
        }
    }

    private void setRotatesPivot(double xFactor, double yFactor) {
        if (useScaleInsteadOfRotate) {
            globalScale.setPivotX(xFactor * getWidth());
            globalScale.setPivotY(yFactor * getHeight());
            backScale.setPivotX(xFactor * backPane.getWidth());
            backScale.setPivotY(yFactor * backPane.getHeight());
        } else {
            Point3D axis = xFactor == 0 ? Rotate.X_AXIS : Rotate.Y_AXIS;
            globalRotate.setAxis(axis);
            globalRotate.setPivotX(xFactor * getWidth());
            globalRotate.setPivotY(yFactor * getHeight());
            backRotate.setAxis(axis);
            backRotate.setPivotX(xFactor * backPane.getWidth());
            backRotate.setPivotY(yFactor * backPane.getHeight());
        }
    }
}
