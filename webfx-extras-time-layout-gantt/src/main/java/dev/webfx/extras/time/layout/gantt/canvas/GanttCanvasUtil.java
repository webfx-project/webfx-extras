package dev.webfx.extras.time.layout.gantt.canvas;

import dev.webfx.extras.canvas.CanvasDrawer;
import dev.webfx.extras.canvas.layer.ChildDrawer;
import dev.webfx.extras.canvas.pane.CanvasPane;
import dev.webfx.extras.canvas.pane.VirtualCanvasPane;
import dev.webfx.extras.time.layout.gantt.impl.GanttLayoutImpl;
import dev.webfx.kit.util.properties.FXProperties;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;

import java.time.temporal.Temporal;

/**
 * @author Bruno Salmon
 */
public final class GanttCanvasUtil {

    public static CanvasPane createParentCanvasPane(GanttLayoutImpl<?, Temporal> ganttLayout, Canvas canvas, ChildDrawer<?> parentDrawer) {
        ParentsCanvasDrawer parentsCanvasDrawer = new ParentsCanvasDrawer(ganttLayout, canvas, parentDrawer);
        return new CanvasPane(canvas, parentsCanvasDrawer::refreshCanvas);
    }

    public static CanvasPane createParentCanvasPane(GanttLayoutImpl<?, Temporal> ganttLayout, Canvas canvas, ChildDrawer<?> parentDrawer, double parentMaxWidth) {
        CanvasPane parentCanvasPane = createParentCanvasPane(ganttLayout, canvas, parentDrawer);
        parentCanvasPane.setMaxWidth(parentMaxWidth);
        return parentCanvasPane;
    }

    public static VirtualCanvasPane createParentVirtualCanvasPane(GanttLayoutImpl<?, Temporal> ganttLayout, Canvas canvas, ChildDrawer<?> parentDrawer, ObservableObjectValue<Bounds> viewportBoundsProperty, ObservableDoubleValue vvalueProperty) {
        ParentsCanvasDrawer parentsCanvasDrawer = new ParentsCanvasDrawer(ganttLayout, canvas, parentDrawer);
        VirtualCanvasPane parentVirtualCanvasPane = new VirtualCanvasPane(canvas, parentsCanvasDrawer::refreshCanvas);
        parentVirtualCanvasPane.activateVirtualCanvasMode(viewportBoundsProperty, vvalueProperty);
        FXProperties.runOnPropertiesChange(() -> parentVirtualCanvasPane.setRequestedCanvasHeight(ganttLayout.getHeight()), ganttLayout.heightProperty());
        return parentVirtualCanvasPane;
    }

    public static VirtualCanvasPane createParentVirtualCanvasPane(GanttLayoutImpl<?, Temporal> ganttLayout, Canvas canvas, ChildDrawer<?> parentDrawer, double parentMaxWidth, ObservableObjectValue<Bounds> viewportBoundsProperty, ObservableDoubleValue vvalueProperty) {
        VirtualCanvasPane parentVirtualCanvasPane = createParentVirtualCanvasPane(ganttLayout, canvas, parentDrawer, viewportBoundsProperty, vvalueProperty);
        parentVirtualCanvasPane.setMaxWidth(parentMaxWidth);
        return parentVirtualCanvasPane;
    }

    public static <P> void addParentsDrawing(GanttLayoutImpl<?, Temporal> ganttLayout, CanvasDrawer childrenDrawer, ChildDrawer<P> parentDrawer, double parentMaxWidth) {
        addParentAndGrandParentsDrawing(ganttLayout, childrenDrawer, parentDrawer, parentMaxWidth, null);
    }

    public static <P, G> void addParentAndGrandParentsDrawing(GanttLayoutImpl<?, ? extends Temporal> ganttLayout, CanvasDrawer childrenDrawer, ChildDrawer<P> parentDrawer, double parentMaxWidth, ChildDrawer<G> grandParentDrawer) {
        new ParentsCanvasDrawer(ganttLayout, childrenDrawer, parentDrawer, grandParentDrawer)
                .setParentWidth(parentMaxWidth);
    }

}
