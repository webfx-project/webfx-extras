package dev.webfx.extras.flextable;

import dev.webfx.extras.panes.LayoutPane;
import dev.webfx.kit.util.properties.ObservableLists;
import javafx.collections.ObservableList;
import javafx.scene.Node;

/**
 * @author Bruno Salmon
 */
public final class FlexTable extends LayoutPane {

    private final ObservableList<FlexColumn> flexColumns = ObservableLists.newObservableList(this::onRowsOrColumnsChanged);
    private final ObservableList<FlexRow> flexRows = ObservableLists.newObservableList(this::onRowsOrColumnsChanged);

    public FlexTable() {
    }

    @Override
    protected void layoutChildren(double paddingLeft, double paddingTop, double innerWidth, double innerHeight) {
        double x = paddingLeft;
        for (FlexColumn flexColumn : flexColumns) {
            double y = paddingTop;
            double columnWidth = flexColumn.getWidth();
            for (FlexRow flexRow : flexRows) {
                Node cellNode = flexRow.getCellNode(flexColumn);
                if (cellNode != null) {
                    double cellPrefHeight = cellNode.prefHeight(columnWidth);
                    layoutInArea(cellNode, x, y, columnWidth, cellPrefHeight);
                    y += cellPrefHeight;
                }
            }
            x += columnWidth;
        }
    }

    public ObservableList<FlexColumn> getFlexColumns() {
        return flexColumns;
    }

    public ObservableList<FlexRow> getFlexRows() {
        return flexRows;
    }

    private void onRowsOrColumnsChanged() {
        getChildren().clear();
        for (FlexColumn flexColumn : flexColumns) {
            double maxCellWidth = 0;
            for (FlexRow flexRow : flexRows) {
                Node cellNode = flexRow.getCellNode(flexColumn);
                if (cellNode != null) {
                    double cellPrefWidth = cellNode.prefWidth(-1);
                    if (cellPrefWidth > maxCellWidth)
                        maxCellWidth = cellPrefWidth;
                    getChildren().add(cellNode);
                }
            }
            double columnWidth = LayoutPane.boundedSize(flexColumn.getMinWidth(), maxCellWidth, flexColumn.getMaxWidth());
            flexColumn.setWidth(columnWidth);
        }
    }
}
