package dev.webfx.extras.timelayout.canvas.generic;

import dev.webfx.extras.util.animation.Animations;
import dev.webfx.kit.launcher.WebFxKitLauncher;
import dev.webfx.kit.util.properties.FXProperties;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

/**
 * @author Bruno Salmon
 */
public class CanvasPane extends Pane {

    protected final Canvas canvas;
    private final CanvasRefresher canvasRefresher;
    protected double requestedCanvasHeight = -1;
    private boolean virtualCanvasMode;
    private double viewportHeight;
    private double vValue;
    private boolean wasManaged;

    public CanvasPane(HasCanvas hasCanvas, CanvasRefresher canvasRefresher) {
        this(hasCanvas.getCanvas(), canvasRefresher);
    }

    public CanvasPane(Canvas canvas, CanvasRefresher canvasRefresher) {
        super(canvas);
        this.canvas = canvas;
        this.canvasRefresher = canvasRefresher;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void activateVirtualCanvasMode(ObservableObjectValue<Bounds> viewportBoundsProperty, ObservableDoubleValue vvalueProperty) {
        virtualCanvasMode = true;
        FXProperties.runNowAndOnPropertiesChange(() -> setViewPortHeight(viewportBoundsProperty.get().getHeight()), viewportBoundsProperty);
        FXProperties.runNowAndOnPropertiesChange(() -> setVValue(vvalueProperty.get()), vvalueProperty);
    }

    private void setViewPortHeight(double viewportHeight) {
        if (viewportHeight != this.viewportHeight) {
            this.viewportHeight = viewportHeight;
            setVValue(vValue);
            requestLayout();
        }
    }

    private void setVValue(double vValue) {
        this.vValue = vValue;
        // Moving the canvas vertically, so it finally doesn't scroll but stay immobile in the viewport
        double layoutY = vValue * (requestedCanvasHeight - viewportHeight);
        canvas.setLayoutY(layoutY);
        // But moving its layoutOrigin, so when it will redraw, it will look like the scroll happened on the virtual canvas
        callCanvasRefresher(getWidth(), requestedCanvasHeight, false);
    }

    public void setRequestedCanvasHeight(double requestedCanvasHeight) {
        if (requestedCanvasHeight != this.requestedCanvasHeight) {
            this.requestedCanvasHeight = requestedCanvasHeight;
            if (!virtualCanvasMode) {
                // We also eventually animate the pref height property to create a smooth transition to that new height (note
                // that this changes the height of the canvas pane only, not the height of the canvas => no perf issue).
                boolean isManaged = isManaged();
                Animations.animateProperty(prefHeightProperty(), requestedCanvasHeight, wasManaged && isManaged);
                wasManaged = isManaged;
            }
            requestLayout(); // We request a layout to consider the new canvas height
        }
    }

    @Override
    protected void layoutChildren() {
        if (virtualCanvasMode) {
            resizeVirtualCanvas();
        } else
            resizeStandardCanvas();
    }

    private void resizeStandardCanvas() {
        double newCanvasWidth = getWidth();
        double newCanvasHeight = requestedCanvasHeight >= 0 ? requestedCanvasHeight : getHeight();
        boolean canvasWidthChanged  = newCanvasWidth  != canvas.getWidth();
        boolean canvasHeightChanged = newCanvasHeight != canvas.getHeight();
        if ((canvasWidthChanged || canvasHeightChanged) && newCanvasWidth > 0 && newCanvasHeight > 0) {
            canvas.setWidth(newCanvasWidth);
            canvas.setHeight(newCanvasHeight);
            callCanvasRefresher(newCanvasWidth, newCanvasHeight, true);
        }
    }

    private void resizeVirtualCanvas() {
        double newVirtualCanvasWidth = getWidth();
        double newVirtualCanvasHeight = requestedCanvasHeight >= 0 ? requestedCanvasHeight : getPrefHeight();
        boolean virtualCanvasHeightChanged = newVirtualCanvasHeight != getPrefHeight();
        if (virtualCanvasHeightChanged)
            setPrefHeight(newVirtualCanvasHeight);
        double newCanvasWidth = newVirtualCanvasWidth + WebFxKitLauncher.getVerticalScrollbarExtraWidth();
        double newCanvasHeight = viewportHeight;
        boolean canvasWidthChanged  = newCanvasWidth  != canvas.getWidth();
        boolean canvasHeightChanged = newCanvasHeight != canvas.getHeight();
        if (canvasWidthChanged || canvasHeightChanged) {
            canvas.setWidth(newCanvasWidth);
            canvas.setHeight(newCanvasHeight);
        }
        callCanvasRefresher(newCanvasWidth, newVirtualCanvasHeight, canvasWidthChanged || canvasHeightChanged);
    }

    protected void callCanvasRefresher(double virtualCanvasWidth, double virtualCanvasHeight, boolean canvasSizeChanged) {
        canvasRefresher.refreshCanvas(virtualCanvasWidth, virtualCanvasHeight, canvas.getLayoutY(), canvasSizeChanged);
    }
}
