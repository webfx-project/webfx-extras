package dev.webfx.extras.visual.controls.grid.peers.base;

import javafx.scene.Node;
import dev.webfx.extras.visual.controls.grid.VisualGrid;
import dev.webfx.extras.visual.controls.peers.base.SelectableVisualResultControlPeerMixin;
import dev.webfx.extras.visual.VisualColumn;

/**
 * @author Bruno Salmon
 */
public interface VisualGridPeerMixin
        <C, N extends VisualGrid, NB extends VisualGridPeerBase<C, N, NB, NM>, NM extends VisualGridPeerMixin<C, N, NB, NM>>

        extends SelectableVisualResultControlPeerMixin<C, N, NB, NM> {

    void updateHeaderVisible(boolean headerVisible);

    void updateFullHeight(boolean fullHeight);

    void setUpGridColumn(int gridColumnIndex, int rsColumnIndex, VisualColumn visualColumn);

    void setCellContent(C cell, Node content, VisualColumn visualColumn);

}
