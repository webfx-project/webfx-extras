package dev.webfx.extras.canvas.pane;

import dev.webfx.extras.canvas.HasCanvas;
import dev.webfx.kit.launcher.WebFxKitLauncher;
import dev.webfx.kit.util.properties.FXProperties;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;

/**
 * @author Bruno Salmon
 */
public class VirtualCanvasPane extends CanvasPane {

    private double viewportHeight;
    private double vValue;

    {
        enableHeightAnimation = false;
    }

    public VirtualCanvasPane(HasCanvas hasCanvas, CanvasRefresher canvasRefresher) {
        super(hasCanvas, canvasRefresher);
    }

    public VirtualCanvasPane(Canvas canvas, CanvasRefresher canvasRefresher) {
        super(canvas, canvasRefresher);
    }

    public void activateVirtualCanvasMode(ObservableObjectValue<Bounds> viewportBoundsProperty, ObservableDoubleValue vvalueProperty) {
        FXProperties.runNowAndOnPropertyChange(bounds -> setViewPortHeight(bounds.getHeight()), viewportBoundsProperty);
        FXProperties.runNowAndOnDoublePropertyChange(this::setVValue, vvalueProperty);
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

    @Override
    protected void layoutChildren() {
        resizeVirtualCanvas();
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

}
