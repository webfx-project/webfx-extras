package dev.webfx.extras.timelayout.gantt.canvas;

import dev.webfx.extras.timelayout.canvas.ChildDrawer;
import dev.webfx.extras.timelayout.canvas.generic.CanvasPane;
import dev.webfx.extras.timelayout.canvas.generic.VirtualCanvasPane;
import dev.webfx.extras.timelayout.gantt.GanttLayout;
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

    public static <P> CanvasPane createParentCanvasPane(Canvas canvas, GanttLayout<?, ?> ganttLayout, ChildDrawer<P, Temporal> parentDrawer) {
        ParentsCanvasRefresher<P> parentsCanvasDrawer = new ParentsCanvasRefresher<>(canvas, ganttLayout, parentDrawer);
        return new CanvasPane(canvas, parentsCanvasDrawer::refreshCanvas);
    }
    public static <P> CanvasPane createParentCanvasPane(Canvas canvas, GanttLayout<?, ?> ganttLayout, ChildDrawer<P, Temporal> parentDrawer, double parentMaxWidth) {
        CanvasPane parentCanvasPane = createParentCanvasPane(canvas, ganttLayout, parentDrawer);
        parentCanvasPane.setMaxWidth(parentMaxWidth);
        return parentCanvasPane;
    }

    public static <P> VirtualCanvasPane createParentVirtualCanvasPane(Canvas canvas, GanttLayout<?, ?> ganttLayout, ChildDrawer<P, Temporal> parentDrawer, ObservableObjectValue<Bounds> viewportBoundsProperty, ObservableDoubleValue vvalueProperty) {
        ParentsCanvasRefresher<P> parentsCanvasDrawer = new ParentsCanvasRefresher<>(canvas, ganttLayout, parentDrawer);
        VirtualCanvasPane parentVirtualCanvasPane = new VirtualCanvasPane(canvas, parentsCanvasDrawer::refreshCanvas);
        parentVirtualCanvasPane.activateVirtualCanvasMode(viewportBoundsProperty, vvalueProperty);
        FXProperties.runOnPropertiesChange(() -> parentVirtualCanvasPane.setRequestedCanvasHeight(ganttLayout.getHeight()), ganttLayout.heightProperty());
        return parentVirtualCanvasPane;
    }

    public static <P> VirtualCanvasPane createParentVirtualCanvasPane(Canvas canvas, GanttLayout<?, ?> ganttLayout, ChildDrawer<P, Temporal> parentDrawer, double parentMaxWidth, ObservableObjectValue<Bounds> viewportBoundsProperty, ObservableDoubleValue vvalueProperty) {
        VirtualCanvasPane parentVirtualCanvasPane = createParentVirtualCanvasPane(canvas, ganttLayout, parentDrawer, viewportBoundsProperty, vvalueProperty);
        parentVirtualCanvasPane.setMaxWidth(parentMaxWidth);
        return parentVirtualCanvasPane;
    }

}
