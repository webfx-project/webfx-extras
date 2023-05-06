package dev.webfx.extras.time.layout.gantt.canvas;

import dev.webfx.extras.canvas.layer.ChildDrawer;
import dev.webfx.extras.canvas.pane.CanvasPane;
import dev.webfx.extras.canvas.pane.VirtualCanvasPane;
import dev.webfx.extras.canvas.CanvasDrawer;
import dev.webfx.extras.time.layout.gantt.GanttLayout;
import dev.webfx.kit.util.properties.FXProperties;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;

/**
 * @author Bruno Salmon
 */
public final class GanttCanvasUtil {

    public static CanvasPane createParentCanvasPane(Canvas canvas, GanttLayout<?, ?> ganttLayout, ChildDrawer<?> parentDrawer) {
        ParentsCanvasRefresher parentsCanvasDrawer = new ParentsCanvasRefresher(canvas, ganttLayout, parentDrawer);
        return new CanvasPane(canvas, parentsCanvasDrawer::refreshCanvas);
    }
    public static <P> CanvasPane createParentCanvasPane(Canvas canvas, GanttLayout<?, ?> ganttLayout, ChildDrawer<?> parentDrawer, double parentMaxWidth) {
        CanvasPane parentCanvasPane = createParentCanvasPane(canvas, ganttLayout, parentDrawer);
        parentCanvasPane.setMaxWidth(parentMaxWidth);
        return parentCanvasPane;
    }

    public static VirtualCanvasPane createParentVirtualCanvasPane(Canvas canvas, GanttLayout<?, ?> ganttLayout, ChildDrawer<?> parentDrawer, ObservableObjectValue<Bounds> viewportBoundsProperty, ObservableDoubleValue vvalueProperty) {
        ParentsCanvasRefresher parentsCanvasDrawer = new ParentsCanvasRefresher(canvas, ganttLayout, parentDrawer);
        VirtualCanvasPane parentVirtualCanvasPane = new VirtualCanvasPane(canvas, parentsCanvasDrawer::refreshCanvas);
        parentVirtualCanvasPane.activateVirtualCanvasMode(viewportBoundsProperty, vvalueProperty);
        FXProperties.runOnPropertiesChange(() -> parentVirtualCanvasPane.setRequestedCanvasHeight(ganttLayout.getHeight()), ganttLayout.heightProperty());
        return parentVirtualCanvasPane;
    }

    public static VirtualCanvasPane createParentVirtualCanvasPane(Canvas canvas, GanttLayout<?, ?> ganttLayout, ChildDrawer<?> parentDrawer, double parentMaxWidth, ObservableObjectValue<Bounds> viewportBoundsProperty, ObservableDoubleValue vvalueProperty) {
        VirtualCanvasPane parentVirtualCanvasPane = createParentVirtualCanvasPane(canvas, ganttLayout, parentDrawer, viewportBoundsProperty, vvalueProperty);
        parentVirtualCanvasPane.setMaxWidth(parentMaxWidth);
        return parentVirtualCanvasPane;
    }

    public static <P> void addParentsDrawing(GanttLayout<?, ?> ganttLayout, CanvasDrawer childrenDrawer, ChildDrawer<P> parentDrawer, double parentMaxWidth) {
        ParentsCanvasRefresher parentsCanvasDrawer = new ParentsCanvasRefresher(childrenDrawer.getCanvas(), ganttLayout, parentDrawer, false);
        childrenDrawer.addOnAfterDraw(() -> {
            parentsCanvasDrawer.refreshCanvas(parentMaxWidth, childrenDrawer.getCanvas().getHeight(), childrenDrawer.getLayoutOriginY(), false);
        });
    }

    public static <P, G> void addParentAndGrandParentsDrawing(GanttLayout<?, ?> ganttLayout, CanvasDrawer childrenDrawer, ChildDrawer<P> parentDrawer, double parentMaxWidth, ChildDrawer<G> grandParentDrawer) {
        ParentsCanvasRefresher parentsCanvasDrawer = new ParentsCanvasRefresher(childrenDrawer.getCanvas(), ganttLayout, parentDrawer, grandParentDrawer, false);
        childrenDrawer.addOnAfterDraw(() -> {
            parentsCanvasDrawer.refreshCanvas(parentMaxWidth, childrenDrawer.getCanvas().getHeight(), childrenDrawer.getLayoutOriginY(), false);
        });
    }

}
