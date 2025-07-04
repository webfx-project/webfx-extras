package dev.webfx.extras.visual.controls.grid;

import dev.webfx.extras.responsive.ResponsiveDesign;
import dev.webfx.extras.responsive.ResponsiveLayout;
import dev.webfx.extras.visual.VisualResult;
import dev.webfx.extras.visual.controls.SelectableVisualResultControl;
import dev.webfx.extras.visual.controls.grid.registry.VisualGridRegistry;
import dev.webfx.extras.visual.controls.grid.skin.VisualGridSkin;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Skin;

/**
 * @author Bruno Salmon
 */
public class VisualGrid extends SelectableVisualResultControl {

    private static final double DEFAULT_ROW_HEIGHT = 24;
    private static final Insets DEFAULT_CELL_MARGIN = new Insets(0, 0, 0, 5);

    private final DoubleProperty minRowHeightProperty = new SimpleDoubleProperty(USE_PREF_SIZE);
    private final DoubleProperty prefRowHeightProperty = new SimpleDoubleProperty(DEFAULT_ROW_HEIGHT);
    private final DoubleProperty maxRowHeightProperty = new SimpleDoubleProperty(USE_PREF_SIZE);
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

    public double getMinRowHeight() {
        return minRowHeightProperty.get();
    }

    public DoubleProperty minRowHeightProperty() {
        return minRowHeightProperty;
    }

    public void setMinRowHeight(double rowHeight) {
        minRowHeightProperty.set(rowHeight);
    }

    public double getPrefRowHeight() {
        return prefRowHeightProperty.get();
    }

    public DoubleProperty prefRowHeightProperty() {
        return prefRowHeightProperty;
    }

    public void setPrefRowHeight(double rowHeight) {
        prefRowHeightProperty.set(rowHeight);
    }

    public double getMaxRowHeight() {
        return maxRowHeightProperty.get();
    }

    public DoubleProperty maxRowHeightProperty() {
        return maxRowHeightProperty;
    }

    public void setMaxRowHeight(double rowHeight) {
        maxRowHeightProperty.set(rowHeight);
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

    public static VisualGrid createVisualGridWithTableLayoutSkin() {
        return new SkinnedVisualGrid(VisualGridSkin::new);
    }

    public static VisualGrid createVisualGridWithMonoColumnLayoutSkin() {
        VisualGrid visualGrid = createVisualGridWithTableLayoutSkin();
        VisualGridSkin skin = (VisualGridSkin) visualGrid.getSkin();
        skin.setMonoColumnLayout();
        return visualGrid;
    }

    public static VisualGrid createVisualGridWithResponsiveSkin() {
        VisualGrid visualGrid = createVisualGridWithTableLayoutSkin();
        VisualGridSkin skin = (VisualGridSkin) visualGrid.getSkin();
        new ResponsiveDesign(visualGrid)
                .addResponsiveLayout(new ResponsiveLayout() {
                    @Override
                    public boolean testResponsiveLayoutApplicability(double width) {
                        return width >= skin.getTableLayoutMinWidth();
                    }

                    @Override
                    public ObservableValue<?>[] getResponsiveTestDependencies() {
                        return new ObservableValue[]{ skin.tableLayoutMinWidthProperty() };
                    }

                    @Override
                    public void applyResponsiveLayout() {
                        skin.setTableLayout();
                    }
                })
                .addResponsiveLayout(skin::setMonoColumnLayout)
                .start();
        return visualGrid;
    }

    public static boolean isMonoColumnLayout(VisualGrid visualGrid) {
        Skin<?> skin = visualGrid.getSkin();
        return skin instanceof VisualGridSkin s && s.isMonoColumnLayout();
    }

    public static boolean isTableLayout(VisualGrid visualGrid) {
        return !isMonoColumnLayout(visualGrid);
    }

    static {
        VisualGridRegistry.registerVisualGrid();
    }
}
