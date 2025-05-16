package dev.webfx.extras.visual.controls.grid;

import dev.webfx.extras.panes.MonoPane;
import dev.webfx.extras.visual.VisualColumn;
import dev.webfx.extras.visual.VisualResult;
import dev.webfx.extras.visual.VisualStyle;
import dev.webfx.platform.scheduler.Scheduled;
import dev.webfx.platform.uischeduler.UiScheduler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Bruno Salmon
 */
final class VisualGridVerticalSkin extends VisualGridSkinBase<Pane, Pane> {

    private static final long INITIAL_BUILD_TIME_MAX_MILLIS = 50;
    private static final long ANIMATION_FRAME_BUILD_TIME_MAX_MILLIS = 10;

    static class HorizontalBiasVBox extends VBox {
        @Override
        public Orientation getContentBias() {
            return Orientation.HORIZONTAL;
        }
    }

    private long initialBuildTimeMillis;
    private final VBox container = new HorizontalBiasVBox();
    // List containing all data rows (index = index of the data in the visual result)
    private final List<Pane> bodyDataRows = new ArrayList<>();

    VisualGridVerticalSkin(VisualGrid control) {
        super(control);
    }

    @Override
    public void applyResponsiveLayout() {
        // Because of a bug in OpenJFX, we can't reuse the same skin again, so we need to create a new instance
        if (visualControl.getSkin() != this)
            visualControl.setSkin(new VisualGridVerticalSkin(visualControl));
    }

    @Override
    public void install() {
        super.install();
        getChildren().setAll(container);
    }

    @Override
    protected void startBuildingGrid() {
        initialBuildTimeMillis = System.currentTimeMillis();
        container.getChildren().clear();
        bodyDataRows.clear();
        super.startBuildingGrid();
    }

    private int builtRowIndex = -1;

    @Override
    protected void buildRowCells(Pane bodyRow, int rowIndex) {
        // Skipping rows after INITIAL_BUILD_TIME_MAX_MILLIS (remaining rows will be built later in next animation frames)
        if (System.currentTimeMillis() <= initialBuildTimeMillis + INITIAL_BUILD_TIME_MAX_MILLIS) {
            super.buildRowCells(bodyRow, rowIndex);
            builtRowIndex = rowIndex;
        }
    }

    private int getBuiltRowCount() {
        return builtRowIndex + 1;
    }

    private boolean endBuildingGridIfAllRowsAreBuilt() {
        if (getBuiltRowCount() < getRowCount())
            return false;
        return true;
    }

    @Override
    protected void endBuildingGrid() {
        if (!endBuildingGridIfAllRowsAreBuilt()) {
            UiScheduler.schedulePeriodicInAnimationFrame(new Consumer<>() {
                final VisualResult rs = getRs(); // Capturing VisualResult to check if it hasn't changed

                @Override
                public void accept(Scheduled scheduled) {
                    if (rs != getRs())
                        scheduled.cancel();
                    else {
                        long animationFrameBuildEndTimeMillis = System.currentTimeMillis() + ANIMATION_FRAME_BUILD_TIME_MAX_MILLIS;
                        while (System.currentTimeMillis() <= animationFrameBuildEndTimeMillis) {
                            if (endBuildingGridIfAllRowsAreBuilt()) {
                                scheduled.cancel();
                                break;
                            } else {
                                builtRowIndex++;
                                VisualGridVerticalSkin.super.buildRowCells(getOrAddBodyRow(builtRowIndex), builtRowIndex);
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    protected Pane getOrAddHeadCell(int gridColumnIndex) {
        return null; // No header in this skin
    }

    @Override
    protected Pane createBodyGroupCell(int rowIndex, VisualColumn groupColumn) {
        MonoPane groupCell = new MonoPane();
        groupCell.setMaxWidth(Double.MAX_VALUE);
        groupCell.paddingProperty().bind(visualControl.cellMarginProperty());
        groupCell.minHeightProperty().bind(visualControl.minRowHeightProperty());
        groupCell.maxHeightProperty().bind(visualControl.maxRowHeightProperty());
        groupCell.getStyleClass().add("grid-group");
        container.getChildren().add(groupCell);
        return groupCell;
    }

    @Override
    protected Pane getOrAddBodyRow(int rowIndex) {
        if (bodyDataRows.size() > rowIndex)
            return bodyDataRows.get(rowIndex);
        VBox bodyRow = new HorizontalBiasVBox();
        bodyRow.paddingProperty().bind(visualControl.cellMarginProperty());
        bodyRow.minHeightProperty().bind(visualControl.minRowHeightProperty());
        bodyRow.maxHeightProperty().bind(visualControl.maxRowHeightProperty());
        bodyRow.getStyleClass().add("grid-row");
        container.getChildren().add(bodyRow);
        bodyDataRows.add(bodyRow);
        return bodyRow;
    }

    @Override
    protected Pane createBodyRowCell(Pane bodyRow, int rowIndex, int gridColumnIndex) {
        return bodyRow; // Note: bodyRow is also the cell
    }

    @Override
    protected void setCellContent(Pane bodyRowCell, Node content, VisualColumn visualColumn) {
        if (content != null) {
            VisualStyle style = visualColumn.getStyle();
            if (style != null) {
                String styleClass = style.getStyleClass();
                if (styleClass != null)
                    content.getStyleClass().addAll(styleClass.split(" "));
                String textAlign = style.getTextAlign();
                if (textAlign != null) {
                    // If an alignment is specified, we wrap the content in a mono pane to consider it, because VBox
                    // alone doesn't support alignment (there is no VBox.setAlignment() method)
                    Pos alignment;
                    switch (textAlign) {
                        case "center": alignment = Pos.CENTER;       break;
                        case "right":  alignment = Pos.CENTER_RIGHT; break;
                        case "left":
                        default:       alignment = Pos.CENTER_LEFT;  break;
                    }
                    MonoPane monoPane = new MonoPane(content);
                    monoPane.setAlignment(alignment);
                    monoPane.setMaxWidth(Double.MAX_VALUE); // Filling the VBox width so the alignment is correctly applied
                    content = monoPane;
                }
            }
            VBox.setMargin(content, visualControl.getCellMargin());
            bodyRowCell.getChildren().add(content);
        }
    }

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return 0;
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return 0;
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return leftInset + rightInset + container.prefWidth(height);
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return topInset + bottomInset + container.prefHeight(width);
    }

    @Override
    protected double computeBaselineOffset(double topInset, double rightInset, double bottomInset, double leftInset) {
        return -1;
    }

}
