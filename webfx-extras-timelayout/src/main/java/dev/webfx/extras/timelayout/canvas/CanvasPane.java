package dev.webfx.extras.timelayout.canvas;

import dev.webfx.extras.util.animation.Animations;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

/**
 * @author Bruno Salmon
 */
public class CanvasPane extends Pane {

    private final Canvas canvas;
    private final Runnable layoutAndRedraw;
    private final Runnable redrawOnly;

    private double canvasHeight = -1;

    public CanvasPane(Runnable layoutAndRedraw, Runnable redrawOnly) {
        this(new Canvas(), layoutAndRedraw, redrawOnly);
    }

    public CanvasPane(Canvas canvas, Runnable layoutAndRedraw, Runnable redrawOnly) {
        super(canvas);
        this.canvas = canvas;
        this.layoutAndRedraw = layoutAndRedraw;
        this.redrawOnly = redrawOnly;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvasHeight(double canvasHeight, boolean animateCanvasPane) {
        this.canvasHeight = canvasHeight;
        requestLayout(); // We request a layout to consider the new canvas height
        // We also eventually animate the pref height property to create a smooth transition to that new height (note
        // that this changes the height of the canvas pane only, not the height of the canvas => no perf issue).
        Animations.animateProperty(prefHeightProperty(), canvasHeight, animateCanvasPane);
    }

    @Override
    protected void layoutChildren() {
        double newCanvasWidth = getWidth();
        double newCanvasHeight = canvasHeight >= 0 ? canvasHeight : getHeight();
        boolean canvasWidthChanged  = newCanvasWidth  != canvas.getWidth();
        boolean canvasHeightChanged = newCanvasHeight != canvas.getHeight();
        if (canvasWidthChanged || canvasHeightChanged) {
            canvas.setWidth(newCanvasWidth);
            canvas.setHeight(newCanvasHeight);
            layoutInArea(canvas, 0, 0, newCanvasWidth, newCanvasHeight, 0, HPos.LEFT, VPos.TOP);
            if (canvasWidthChanged)
                layoutAndRedraw.run(); // May also call setLayoutCanvasHeight()
            else
                redrawOnly.run();
        }
    }
}
