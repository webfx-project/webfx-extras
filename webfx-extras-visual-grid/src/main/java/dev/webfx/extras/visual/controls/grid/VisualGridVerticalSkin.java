package dev.webfx.extras.visual.controls.grid;

import dev.webfx.extras.panes.MonoPane;
import dev.webfx.extras.visual.VisualColumn;
import dev.webfx.extras.visual.VisualStyle;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
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
    protected Pane getOrAddHeadCell(int gridColumnIndex) {
        return null; // No header in this skin
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
