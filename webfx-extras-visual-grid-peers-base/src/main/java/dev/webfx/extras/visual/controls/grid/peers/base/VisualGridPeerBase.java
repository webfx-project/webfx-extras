package dev.webfx.extras.visual.controls.grid.peers.base;

import dev.webfx.extras.visual.controls.VisualResultControlSkinBase;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.*;
import dev.webfx.extras.cell.renderer.ImageTextRenderer;
import dev.webfx.extras.cell.renderer.TextRenderer;
import dev.webfx.extras.cell.renderer.ValueRenderer;
import dev.webfx.extras.visual.controls.grid.VisualGrid;
import dev.webfx.extras.visual.controls.peers.base.SelectableVisualResultControlPeerBase;
import dev.webfx.extras.visual.VisualColumn;
import dev.webfx.extras.visual.VisualResult;
import dev.webfx.kit.mapper.peers.javafxgraphics.SceneRequester;
import dev.webfx.platform.util.Strings;

/**
 * @author Bruno Salmon
 */
public class VisualGridPeerBase
        <C, N extends VisualGrid, NB extends VisualGridPeerBase<C, N, NB, NM>, NM extends VisualGridPeerMixin<C, N, NB, NM>>

        extends SelectableVisualResultControlPeerBase<C, N, NB, NM> {

    private int rowStyleColumnIndex;
    private int rowBackgroundColumnIndex;
    private int gridColumnCount;
    private VisualResult rs;
    private VisualGridPeerImageTextMixin<C, N, NB, NM> imageTextMixin;

    @Override
    public void bind(N shape, SceneRequester sceneRequester) {
        super.bind(shape, sceneRequester);
        requestUpdateOnPropertiesChange(sceneRequester
                , node.headerVisibleProperty()
                , node.fullHeightProperty()
        );
    }

    @Override
    public boolean updateProperty(ObservableValue changedProperty) {
        return super.updateProperty(changedProperty)
                || updateProperty(node.headerVisibleProperty(), changedProperty, mixin::updateHeaderVisible)
                || updateProperty(node.fullHeightProperty(), changedProperty, mixin::updateFullHeight)
                ;
    }


    public int getGridColumnCount() {
        return gridColumnCount;
    }

    public VisualResult getRs() {
        return rs;
    }

    @Override
    public void setMixin(NM mixin) {
        super.setMixin(mixin);
        imageTextMixin = mixin instanceof VisualGridPeerImageTextMixin ? (VisualGridPeerImageTextMixin<C, N, NB, NM>) mixin : null;
    }

    public void initGrid(VisualResult rs) {
        this.rs = rs;
        fillGrid(true);
    }

    public void fillGrid(VisualResult rs) {
        this.rs = rs;
        fillGrid(false);
    }


    public void fillGrid(boolean init) {
        rowStyleColumnIndex = rowBackgroundColumnIndex = -1;
        gridColumnCount = 0;
        if (rs == null)
            return;
        int columnCount = rs.getColumnCount();
        int gridColumnIndex = 0;
        VisualColumn[] columns = rs.getColumns();
        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
            VisualColumn visualColumn = columns[columnIndex];
            String role = visualColumn.getRole();
            if (role == null) {
                if (!init)
                    mixin.setUpGridColumn(gridColumnIndex, columnIndex, visualColumn);
                gridColumnIndex++;
            } else if (role.equals("style"))
                rowStyleColumnIndex = columnIndex;
            else if (role.equals("background"))
                rowBackgroundColumnIndex = columnIndex;
        }
        gridColumnCount = gridColumnIndex;
    }

    public void fillCell(C cell, int rowIndex, int rsColumnIndex) {
        fillCell(cell, rowIndex, rsColumnIndex, rs.getColumns()[rsColumnIndex]);
    }

    public void fillCell(C cell, int rowIndex, int rsColumnIndex, VisualColumn visualColumn) {
        fillCell(cell, rs.getValue(rowIndex, rsColumnIndex), visualColumn);
    }

    public void fillCell(C cell, Object cellValue, VisualColumn visualColumn) {
        fillCell(cell, cellValue, visualColumn, visualColumn.getValueRenderer());
    }

    public void fillCell(C cell, Object cellValue, VisualColumn visualColumn, ValueRenderer valueRenderer) {
        if (imageTextMixin != null) {
            if (valueRenderer == TextRenderer.SINGLETON) {
                imageTextMixin.setCellTextContent(cell, Strings.toString(cellValue), visualColumn);
                return;
            }
            if (valueRenderer == ImageTextRenderer.SINGLETON) {
                ImageTextRenderer imageTextCellRenderer = ImageTextRenderer.SINGLETON;
                Object[] array = imageTextCellRenderer.getAndCheckArray(cellValue);
                imageTextMixin.setCellImageAndTextContent(cell, imageTextCellRenderer.getImage(array), imageTextCellRenderer.getText(array), visualColumn);
                return;
            }
        }
        mixin.setCellContent(cell, valueRenderer.renderValue(cellValue, visualColumn.getValueRenderingContext()), visualColumn);
    }

    public int getRowStyleColumnIndex() {
        return rowStyleColumnIndex;
    }

    public int getRowBackgroundColumnIndex() {
        return rowBackgroundColumnIndex;
    }

    public boolean isDataColumn(int columnIndex) {
        return columnIndex != rowStyleColumnIndex && columnIndex != rowBackgroundColumnIndex;
    }

    public int gridColumnIndexToResultColumnIndex(int gridColumnIndex, int rowStyleColumnIndex) {
        int rsColumnIndex = gridColumnIndex;
        if (rowStyleColumnIndex == 0 && gridColumnIndex >= rowStyleColumnIndex)
            rsColumnIndex++;
        return rsColumnIndex;
    }

    public Object getRowStyleResultValue(int rowIndex) {
        return getSafeResultValue(rowIndex, rowStyleColumnIndex);
    }

    public Object getRowBackgroundResultValue(int rowIndex) {
        return getSafeResultValue(rowIndex, rowBackgroundColumnIndex);
    }

    public Paint getRowBackground(Object value) {
        return VisualResultControlSkinBase.getRowBackground(value);
    }

    public Paint getRowBackground(int rowIndex) {
        return getRowBackground(getRowBackgroundResultValue(rowIndex));
    }

    private Object getSafeResultValue(int rowIndex, int columnIndex) {
        if (rs == null || rowIndex < 0 || columnIndex < 0 || rowIndex >= rs.getRowCount() || columnIndex >= rs.getColumnCount())
            return null;
        return rs.getValue(rowIndex, columnIndex);
    }

    public Object[] getRowStyleClasses(int rowIndex) {
        return getRowStyleClasses(getRowStyleResultValue(rowIndex));
    }

    public Object[] getRowStyleClasses(Object value) {
        if (!(value instanceof Object[]))
            return null;
        return (Object[]) value;
    }

    public String getRowStyle(int rowIndex) {
        return VisualResultControlSkinBase.getRowStyle(getRowStyleClasses(rowIndex));
    }

}
