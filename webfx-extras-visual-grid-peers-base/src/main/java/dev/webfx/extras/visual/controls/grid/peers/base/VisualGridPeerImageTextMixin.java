package dev.webfx.extras.visual.controls.grid.peers.base;

import dev.webfx.extras.visual.controls.grid.VisualGrid;
import dev.webfx.extras.visual.VisualColumn;
import javafx.scene.Node;

/**
 * @author Bruno Salmon
 */
public interface VisualGridPeerImageTextMixin
        <C, N extends VisualGrid, NB extends VisualGridPeerBase<C, N, NB, NM>, NM extends VisualGridPeerMixin<C, N, NB, NM>>

        extends VisualGridPeerMixin<C, N, NB, NM> {

    default void setCellContent(C cell, Node content, VisualColumn visualColumn) {
        setCellImageAndTextContent(cell, content, null, visualColumn);
    }

    default void setCellTextContent(C cell, Object /* String or StringProperty */ text, VisualColumn visualColumn) {
        setCellImageAndTextContent(cell, null, text, visualColumn);
    }

    void setCellImageAndTextContent(C cell, Node image, Object /* String or StringProperty */ text, VisualColumn visualColumn);

}
