package dev.webfx.extras.timelayout.canvas;

import dev.webfx.extras.timelayout.CanLayout;
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
public class TimeCanvasPane extends Pane {

    private final Canvas canvas;
    private final CanLayout layout;
    private final CanvasDrawer drawer;
    private double lastLayoutHeight = -1;
    private boolean virtualCanvasMode;
    private double viewportHeight;
    private double vValue;
    private boolean wasManaged;

    public TimeCanvasPane(CanLayout layout, CanvasDrawer drawer) {
        super(drawer.getCanvas());
        this.canvas = drawer.getCanvas();
        this.layout = layout;
        this.drawer = drawer;
        FXProperties.runOnPropertiesChange(() -> setLastLayoutHeight(layout.getHeight()), layout.heightProperty());
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
        double layoutY = vValue * (lastLayoutHeight - viewportHeight);
        canvas.setLayoutY(layoutY);
        // But moving its layoutOrigin, so when it will redraw, it will look like the scroll happened on the virtual canvas
        drawer.setLayoutOriginY(layoutY); // Will cause a canvas redraw
    }

    private void setLastLayoutHeight(double lastLayoutHeight) {
        if (lastLayoutHeight != this.lastLayoutHeight) {
            this.lastLayoutHeight = lastLayoutHeight;
            if (!virtualCanvasMode) {
                // We also eventually animate the pref height property to create a smooth transition to that new height (note
                // that this changes the height of the canvas pane only, not the height of the canvas => no perf issue).
                boolean isManaged = isManaged();
                Animations.animateProperty(prefHeightProperty(), lastLayoutHeight, wasManaged && isManaged);
                wasManaged = isManaged;
            }
            requestLayout(); // We request a layout to consider the new canvas height
        }
    }

    @Override
    protected void layoutChildren() {
        resizeCanvas();
    }

    private void resizeCanvas() {
        if (virtualCanvasMode) {
            resizeVirtualCanvas();
        } else
            resizeStandardCanvas();
    }

    private void resizeStandardCanvas() {
        double newCanvasWidth = getWidth();
        double newCanvasHeight = lastLayoutHeight >= 0 ? lastLayoutHeight : getHeight();
        boolean canvasWidthChanged  = newCanvasWidth  != canvas.getWidth();
        boolean canvasHeightChanged = newCanvasHeight != canvas.getHeight();
        if ((canvasWidthChanged || canvasHeightChanged) && newCanvasWidth > 0 && newCanvasHeight > 0) {
            canvas.setWidth(newCanvasWidth);
            canvas.setHeight(newCanvasHeight);
            callLayout(newCanvasWidth, newCanvasHeight, true);
        }
    }

    private void resizeVirtualCanvas() {
        double newVirtualCanvasWidth = getWidth();
        double newVirtualCanvasHeight = lastLayoutHeight >= 0 ? lastLayoutHeight : getPrefHeight();
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
        callLayout(newCanvasWidth, newVirtualCanvasHeight, canvasWidthChanged || canvasHeightChanged);
    }

    private void callLayout(double layoutWidth, double layoutHeight, boolean redrawRequired) {
        int drawCount = drawer.getDrawCount();
        layout.resize(layoutWidth, layoutHeight); // May cause a layout and also possibly a redraw
        if (redrawRequired && drawer.getDrawCount() == drawCount)
            drawer.markDrawAreaAsDirty();
    }
}
