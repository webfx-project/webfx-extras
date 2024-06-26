package dev.webfx.extras.webtext.peers.base;

import dev.webfx.extras.webtext.HtmlTextEditor;

/**
 * @author Bruno Salmon
 */
public interface HtmlTextEditorPeerMixin
        <N extends HtmlTextEditor, NB extends HtmlTextEditorPeerBase<N, NB, NM>, NM extends HtmlTextEditorPeerMixin<N, NB, NM>>

        extends HtmlTextPeerMixin<N, NB, NM> {

    void updateMode(HtmlTextEditor.Mode mode);

}
