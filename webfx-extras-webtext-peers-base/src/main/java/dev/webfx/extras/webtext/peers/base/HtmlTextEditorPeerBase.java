package dev.webfx.extras.webtext.peers.base;

import dev.webfx.extras.webtext.HtmlTextEditor;

/**
 * @author Bruno Salmon
 */
public final class HtmlTextEditorPeerBase
        <N extends HtmlTextEditor, NB extends HtmlTextEditorPeerBase<N, NB, NM>, NM extends HtmlTextEditorPeerMixin<N, NB, NM>>

        extends HtmlTextPeerBase<N, NB, NM> {

}
