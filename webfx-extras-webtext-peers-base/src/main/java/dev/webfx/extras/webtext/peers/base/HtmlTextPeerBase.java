package dev.webfx.extras.webtext.peers.base;

import javafx.beans.value.ObservableValue;
import dev.webfx.extras.webtext.HtmlText;
import dev.webfx.kit.mapper.peers.javafxcontrols.base.ControlPeerBase;
import dev.webfx.kit.mapper.peers.javafxgraphics.SceneRequester;

/**
 * @author Bruno Salmon
 */
public class HtmlTextPeerBase
        <N extends HtmlText, NB extends HtmlTextPeerBase<N, NB, NM>, NM extends HtmlTextPeerMixin<N, NB, NM>>

        extends ControlPeerBase<N, NB, NM> {

    @Override
    public void bind(N t, SceneRequester sceneRequester) {
        super.bind(t, sceneRequester);
        requestUpdateOnPropertiesChange(sceneRequester
                , t.textProperty()
                , t.fontProperty()
                , t.fillProperty()
        );
    }

    @Override
    public boolean updateProperty(ObservableValue changedProperty) {
        N n = node;
        return super.updateProperty(changedProperty)
                || updateProperty(n.textProperty(), changedProperty, mixin::updateText)
                || updateProperty(n.fontProperty(), changedProperty, mixin::updateFont)
                || updateProperty(n.fillProperty(), changedProperty, mixin::updateFill)
                ;
    }
}
