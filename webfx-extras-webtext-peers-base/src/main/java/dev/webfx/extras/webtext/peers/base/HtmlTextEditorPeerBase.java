package dev.webfx.extras.webtext.peers.base;

import dev.webfx.extras.webtext.HtmlTextEditor;
import dev.webfx.kit.mapper.peers.javafxgraphics.SceneRequester;
import javafx.beans.value.ObservableValue;

/**
 * @author Bruno Salmon
 */
public final class HtmlTextEditorPeerBase
        <N extends HtmlTextEditor, NB extends HtmlTextEditorPeerBase<N, NB, NM>, NM extends HtmlTextEditorPeerMixin<N, NB, NM>>

        extends HtmlTextPeerBase<N, NB, NM> {

    @Override
    public void bind(N t, SceneRequester sceneRequester) {
        super.bind(t, sceneRequester);
        requestUpdateOnPropertiesChange(sceneRequester
                , t.modeProperty()
        );
    }

    @Override
    public boolean updateProperty(ObservableValue changedProperty) {
        N n = node;
        return super.updateProperty(changedProperty)
               || updateProperty(n.modeProperty(), changedProperty, mixin::updateMode)
                ;
    }

}
