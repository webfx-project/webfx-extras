package dev.webfx.extras.timelayout.canvas.generic;

import dev.webfx.extras.util.animation.Animations;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

/**
 * @author Bruno Salmon
 */
public class CanvasPane extends Pane {

    protected final Canvas canvas;
    private final CanvasRefresher canvasRefresher;
    protected double requestedCanvasHeight = -1;
    protected boolean enableHeightAnimation = true;
    protected boolean skipAnimationOnVisibilityChange = true;
    private boolean wasVisible;

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


    public void setRequestedCanvasHeight(double requestedCanvasHeight) {
        if (requestedCanvasHeight != this.requestedCanvasHeight) {
            this.requestedCanvasHeight = requestedCanvasHeight;
            requestLayout(); // We request a layout to consider the new canvas height
            // We also update the pref height property for this canvas pane to match the requested canvas height.
            // This change can eventually be animated to create a smooth transition to that new height (note that this
            // animation changes the height of the canvas pane only, not the height of the canvas => no time layout required).
            Animations.animateProperty(prefHeightProperty(), requestedCanvasHeight, shouldAnimateHeightChange());
        }
    }

    protected boolean shouldAnimateHeightChange() {
        if (!enableHeightAnimation)
            return false;
        if (!skipAnimationOnVisibilityChange)
            return true;
        boolean isVisible = isVisible();
        boolean animate = wasVisible && isVisible;
        wasVisible = isVisible;
        return animate;
    }

    @Override
    protected void layoutChildren() {
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

    protected void callCanvasRefresher(double virtualCanvasWidth, double virtualCanvasHeight, boolean canvasSizeChanged) {
        canvasRefresher.refreshCanvas(virtualCanvasWidth, virtualCanvasHeight, canvas.getLayoutY(), canvasSizeChanged);
    }
}
