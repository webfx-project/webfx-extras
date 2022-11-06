package dev.webfx.extras.led;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.Region;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

/**
 * @author Bruno Salmon
 */
public final class Led extends Region {

    private static final Paint BORDER_FILL = new LinearGradient( 0,  0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0.0,  Color.rgb( 20,  20,  20, 0.65)),
            new Stop(0.15, Color.rgb( 20,  20,  20, 0.65)),
            new Stop(0.26, Color.rgb( 41,  41,  41, 0.65)),
            new Stop(0.4,  Color.rgb(100, 100, 100, 0.80)),
            new Stop(1.0,  Color.rgb( 20,  20,  20, 0.65)));

    private final Circle ledBorder = new Circle(), ledCentre = new Circle(), reflection = new Circle();
    private final Line hLine, vLine;
    private final Node sign;
    private Color ledColor;

    private boolean showBorder = true, showReflectionWhenNotHighlighted = true;
    private Paint pressedFill, releasedFill;
    private final BooleanProperty highlightedProperty = new SimpleBooleanProperty() {
        @Override
        protected void invalidated() {
            updateHighlight();
        }
    };
    private final InnerShadow innerShadow = new InnerShadow(BlurType.TWO_PASS_BOX, Color.rgb(0, 0, 0, 0.65), 0, 0, 0, 0);

    Led(Color ledColor, Boolean plus) {
        ledBorder.setFill(BORDER_FILL);
        ledCentre.setEffect(innerShadow);
        getChildren().setAll(ledBorder, ledCentre, reflection);
        if (plus == null) {
            hLine = vLine = null;
            sign = null;
        } else {
            Color lineColor = Color.IVORY;
            hLine = new Line();
            hLine.setStroke(lineColor);
            hLine.setStrokeLineCap(StrokeLineCap.ROUND);
            vLine = plus ? new Line() : null;
            if (vLine != null) {
                vLine.setStroke(lineColor);
                vLine.setStrokeLineCap(StrokeLineCap.ROUND);
                sign = new Group(hLine, vLine);
            } else
                sign = hLine;
            sign.setOpacity(0.6);
            sign.setMouseTransparent(true);
            getChildren().add(sign);
        }
        reflection.setMouseTransparent(true);
        setColor(ledColor);
    }

    public void setColor(Color ledColor) {
        if (!ledColor.equals(this.ledColor)) {
            this.ledColor = ledColor;
            pressedFill = createFillGradient(ledColor, true);
            releasedFill = createFillGradient(ledColor, false);
            updateHighlight();
        }
    }

    private static Paint createFillGradient(Color ledColor, boolean pressed) {
        return new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, ledColor.deriveColor(0d, 1d, 1, 1d)),
                new Stop(0.49, ledColor.deriveColor(0d, 1d, pressed ? 0.65 : 0.7, 1d)),
                new Stop(1.0, ledColor.deriveColor(0d, 1d, pressed ? 1.3 : 0.35, 1d)));
    }

    public void setShowBorder(boolean showBorder) {
        this.showBorder = showBorder;
    }

    public void setShowReflectionWhenNotHighlighted(boolean showReflectionWhenNotHighlighted) {
        this.showReflectionWhenNotHighlighted = showReflectionWhenNotHighlighted;
    }

    public BooleanProperty highlightedProperty() {
        return highlightedProperty;
    }

    public void setHighlighted(boolean highlighted) {
        highlightedProperty.set(highlighted);
    }

    public boolean isHighlighted() {
        return highlightedProperty.get();
    }

    private void updateHighlight() {
        if (isHighlighted()) {
            ledCentre.setFill(pressedFill);
            ledCentre.setEffect(new DropShadow(ledCentre.getRadius() * 0.3, ledColor));
            reflection.setVisible(true);
        } else {
            ledCentre.setFill(releasedFill);
            ledCentre.setEffect(null);
            reflection.setVisible(showReflectionWhenNotHighlighted);
        }
    }

    public void setOnAction(EventHandler<ActionEvent> actionHandler) {
        ledCentre.setOnMouseClicked(e -> actionHandler.handle(new ActionEvent(this, this)));
        ledCentre.setCursor(Cursor.HAND);
    }

    public void setHighlightOnClick(boolean highlightOnClick) {
        ledCentre.setOnMousePressed(e -> { if (highlightOnClick) setHighlighted(true); });
        ledCentre.setOnMouseReleased(e -> { if (highlightOnClick) setHighlighted(false); });
    }

    private double lastWidth, lastHeight, lastRadius;
    @Override public void layoutChildren() {
        updateSize(getWidth(), getHeight(), true);
    }

    private void updateSize(double width, double height, boolean doLayout) {
        double radius = Math.min(width, height) / 2;
        if (radius != lastRadius) {
            ledBorder.setRadius(radius);
            ledCentre.setRadius((showBorder ? 0.8 : 1.0) * radius);
            reflection.setRadius(0.9 * ledCentre.getRadius());
            innerShadow.setRadius(0.8 * 0.075 / 0.15 * radius);
            Paint highlightFill = new RadialGradient(0, 0, 0 - reflection.getRadius(), 0 - reflection.getRadius(), reflection.getRadius(), false,
                    CycleMethod.NO_CYCLE, new Stop(0.0, Color.WHITE), new Stop(1.0, Color.TRANSPARENT));
            reflection.setFill(highlightFill);
            lastRadius = radius;
        }
        if (!doLayout)
            return;
        if (lastWidth != width || lastHeight != height) {
            for (Node child : getChildren())
                if (!(child instanceof Group))
                    layoutInArea(child, 0, 0, width, height, 0, HPos.CENTER, VPos.CENTER);
            lastWidth = width;
            lastHeight = height;
        }
        if (sign != null) {
            double lineLength = 0.4 * radius;
            if (hLine != null) {
                hLine.setStartX(width / 2 - lineLength);
                hLine.setEndX(width / 2 + lineLength);
                hLine.setStrokeWidth(0.2 * radius);
                layoutInArea(hLine, 0, 0, width, height, 0 , HPos.CENTER, VPos.CENTER);
            }
            if (vLine != null) {
                vLine.setStartY(width / 2 - lineLength);
                vLine.setEndY(width / 2 + lineLength);
                vLine.setStrokeWidth(0.2 * radius);
                layoutInArea(vLine, 0, 0, width, height, 0, HPos.CENTER, VPos.CENTER);
            }
        }
        updateHighlight();
    }

    // Used by Led clock demo
    public void drawInCanvas(GraphicsContext ctx, double x, double y, double size) {
        updateSize(size, size, false);
        ctx.save();
        ctx.setFill(ledCentre.getFill());
        //ctx.setEffect(ledCentre.getEffect()); // Commented as too time-consuming
        ctx.fillOval(x, y, size, size);
        if (reflection.isVisible()) {
            ctx.setFill(reflection.getFill());
            //ctx.setEffect(reflection.getEffect()); // Commented as too time-consuming
            ctx.fillOval(x + 0.05 * size, y + 0.05 * size, 0.9 * size, 0.9 * size);
        }
        ctx.restore();
    }

    public static Led create(Color ledColor, Boolean plus, Runnable actionHandler) {
        Led led = new Led(ledColor, plus);
        led.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        if (actionHandler != null) {
            led.setOnAction(e -> actionHandler.run());
            led.setHighlightOnClick(true);
        }
        return led;
    }

    public static Led create(Color ledColor) {
        return create(ledColor, null, null);
    }

    public static Led createHihlighted(Color ledColor) {
        Led led = create(ledColor);
        led.setHighlighted(true);
        return led;
    }
}