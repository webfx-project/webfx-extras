package dev.webfx.extras.visual.controls.grid;

import dev.webfx.extras.responsive.ResponsiveDesign;
import dev.webfx.extras.visual.VisualResult;
import dev.webfx.extras.visual.controls.SelectableVisualResultControl;
import dev.webfx.extras.visual.controls.grid.registry.VisualGridRegistry;
import javafx.beans.property.*;
import javafx.geometry.Insets;

/**
 * @author Bruno Salmon
 */
public class VisualGrid extends SelectableVisualResultControl {

    private static final double DEFAULT_ROW_HEIGHT = 24;
    private static final Insets DEFAULT_CELL_MARGIN = new Insets(0, 0, 0, 5);

    private final DoubleProperty rowHeightProperty = new SimpleDoubleProperty(DEFAULT_ROW_HEIGHT);
    private final ObjectProperty<Insets> cellMarginProperty = new SimpleObjectProperty<>(DEFAULT_CELL_MARGIN);

    public VisualGrid() {
    }

    public VisualGrid(VisualResult rs) {
        setVisualResult(rs);
    }

    private final BooleanProperty headerVisibleProperty = new SimpleBooleanProperty(true);

    public BooleanProperty headerVisibleProperty() {
        return headerVisibleProperty;
    }

    public boolean isHeaderVisible() {
        return headerVisibleProperty.get();
    }

    public void setHeaderVisible(boolean headerVisible) {
        this.headerVisibleProperty.set(headerVisible);
    }

    private final BooleanProperty fullHeightProperty = new SimpleBooleanProperty(false);

    public BooleanProperty fullHeightProperty() {
        return fullHeightProperty;
    }

    public boolean isFullHeight() {
        return fullHeightProperty.get();
    }

    public void setFullHeight(boolean fullHeight) {
        this.fullHeightProperty.set(fullHeight);
    }

    public double getRowHeight() {
        return rowHeightProperty.get();
    }

    public DoubleProperty rowHeightProperty() {
        return rowHeightProperty;
    }

    public void setRowHeight(double rowHeight) {
        rowHeightProperty.set(rowHeight);
    }

    public Insets getCellMargin() {
        return cellMarginProperty.get();
    }

    public ObjectProperty<Insets> cellMarginProperty() {
        return cellMarginProperty;
    }

    public void setCellMargin(Insets cellMargin) {
        cellMarginProperty.set(cellMargin);
    }

    public static VisualGrid createVisualGridWithTableSkin() {
        return new SkinnedVisualGrid(VisualGridTableSkin::new);
    }

    public static VisualGrid createVisualGridWithVerticalSkin() {
        return new SkinnedVisualGrid(VisualGridVerticalSkin::new);
    }

    public static VisualGrid createVisualGridWithResponsiveSkin() {
        VisualGrid visualGrid = createVisualGridWithTableSkin();
        new ResponsiveDesign(visualGrid)
                .addResponsiveLayout((VisualGridTableSkin) visualGrid.getSkin())
                .addResponsiveLayout(new VisualGridVerticalSkin(visualGrid))
                .start();
        return visualGrid;
    }

    static {
        VisualGridRegistry.registerVisualGrid();
    }
}
