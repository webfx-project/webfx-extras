package dev.webfx.extras.visual.controls.grid;

import dev.webfx.extras.visual.VisualColumn;
import dev.webfx.kit.util.properties.FXProperties;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * @author Bruno Salmon
 */
final class VisualGridVerticalSkin extends VisualGridSkinBase<Pane, Pane> {

    static class HorizontalBiasVBox extends VBox {
        @Override
        public Orientation getContentBias() {
            return Orientation.HORIZONTAL;
        }
    }

    private final VBox container = new HorizontalBiasVBox();

    VisualGridVerticalSkin(VisualGrid control) {
        super(control);
    }


    @Override
    public void install() {
        super.install();
        getChildren().setAll(container);
    }

    @Override
    protected Pane getOrAddHeadCell(int gridColumnIndex) {
        return null;
    }

    @Override
    protected Pane getOrAddBodyRow(int rowIndex) {
        ObservableList<Node> bodyRows = container.getChildren();
        VBox bodyRow;
        if (rowIndex < bodyRows.size())
            bodyRow = (VBox) bodyRows.get(rowIndex);
        else {
            bodyRow = new HorizontalBiasVBox();
            bodyRow.setPadding(new Insets(10, 0, 10, 0));
            bodyRow.getStyleClass().add("grid-row");
            bodyRows.add(bodyRow);
        }
        return bodyRow;
    }

    @Override
    protected Pane getOrAddBodyRowCell(Pane bodyRow, int rowIndex, int gridColumnIndex) {
        return bodyRow;
    }

    @Override
    protected void setCellContent(Pane pane, Node content, VisualColumn visualColumn) {
        if (content != null) {
            VBox.setMargin(content, getSkinnable().getCellMargin());
            pane.getChildren().add(content);
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
