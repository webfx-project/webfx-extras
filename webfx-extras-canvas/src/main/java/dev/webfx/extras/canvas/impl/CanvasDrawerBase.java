package dev.webfx.extras.canvas.impl;

import dev.webfx.extras.canvas.CanvasDrawer;
import dev.webfx.extras.util.DirtyMarker;
import javafx.beans.property.*;
import javafx.beans.value.ObservableIntegerValue;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

/**
 * @author Bruno Salmon
 */
public abstract class CanvasDrawerBase implements CanvasDrawer {

    protected final Canvas canvas;
    protected final GraphicsContext gc;
    private final DoubleProperty layoutOriginXProperty = new SimpleDoubleProperty() {
        @Override
        protected void invalidated() {
            markDrawAreaAsDirty();
        }
    };
    private final DoubleProperty layoutOriginYProperty = new SimpleDoubleProperty() {
        @Override
        protected void invalidated() {
            markDrawAreaAsDirty();
        }
    };
    private final ObjectProperty<Bounds> drawAreaBoundsProperty = new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
            markDrawAreaAsDirty();
        }
    };
    private final ObjectProperty<Paint> drawAreaBackgroundFillProperty = new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
            markDrawAreaAsDirty();
        }
    };
    private final IntegerProperty drawCountProperty = new SimpleIntegerProperty();
    private final DirtyMarker drawAreaDirtyMarker = new DirtyMarker(this::drawArea);

    public CanvasDrawerBase() {
        this(new Canvas());
    }

    public CanvasDrawerBase(Canvas canvas) {
        this.canvas = canvas;
        gc = canvas.getGraphicsContext2D();
    }

    @Override
    public Canvas getCanvas() {
        return gc.getCanvas();
    }

    @Override
    public DoubleProperty layoutOriginXProperty() {
        return layoutOriginXProperty;
    }

    @Override
    public DoubleProperty layoutOriginYProperty() {
        return layoutOriginYProperty;
    }

    @Override
    public ObjectProperty<Bounds> drawAreaBoundsProperty() {
        return drawAreaBoundsProperty;
    }

    @Override
    public ObjectProperty<Paint> drawAreaBackgroundFillProperty() {
        return drawAreaBackgroundFillProperty;
    }

    @Override
    public ObservableIntegerValue drawCountProperty() {
        return drawCountProperty;
    }

    @Override
    public void markDrawAreaAsDirty() {
        drawAreaDirtyMarker.markAsDirty();
    }

    @Override
    public void drawArea() {
        int newDrawCount = getDrawCount() + 1;
        clearArea();
        drawCountProperty.set(-newDrawCount); // may trigger onBeforeDraw runnable(s)
        drawObjectsInArea();
        drawCountProperty.set(newDrawCount); // may trigger onAfterDraw runnable(s)
    }

    protected abstract void drawObjectsInArea();

    public void clearArea() {
        Bounds area = getDrawAreaBounds();
        if (area == null) {
            clearArea(0, 0, canvas.getWidth(), canvas.getHeight());
        } else {
            clearArea(area.getMinX(), area.getMinY(), area.getWidth(), area.getHeight());
        }
    }

    private void clearArea(double x, double y, double width, double height) {
        Paint backgroundFill = getDrawAreaBackgroundFill();
        if (backgroundFill == null)
            gc.clearRect(x, y, width, height);
        else {
            gc.setFill(backgroundFill);
            gc.fillRect(x, y, width, height);
        }
    }
}
