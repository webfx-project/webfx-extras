package dev.webfx.extras.webtext.peers.openjfx;

import dev.webfx.extras.webtext.peers.base.HtmlTextEditorPeerBase;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import dev.webfx.extras.webtext.HtmlTextEditor;
import dev.webfx.extras.webtext.peers.base.HtmlTextEditorPeerMixin;
import dev.webfx.platform.util.Objects;
import netscape.javascript.JSObject;

/**
 * @author Bruno Salmon
 */
public final class FxHtmlTextEditorPeer
        <FxN extends WebView, N extends HtmlTextEditor, NB extends HtmlTextEditorPeerBase<N, NB, NM>, NM extends HtmlTextEditorPeerMixin<N, NB, NM>>
        extends FxHtmlTextWebViewPeer<FxN, N, NB, NM>
        implements HtmlTextEditorPeerMixin<N, NB, NM> {

    private static final String ckEditorUrl = "http://cdn.ckeditor.com/4.7.2/full/ckeditor.js";
    private JSObject ckEditor;

    public FxHtmlTextEditorPeer() {
        this((NB) new HtmlTextEditorPeerBase());
    }

    public FxHtmlTextEditorPeer(NB base) {
        super(base);
        WebEngine webEngine = webView.getEngine();
        webEngine.loadContent("<html><head><script src='" + ckEditorUrl + "'></script></head><body><div id='ckEditorDiv'></div></body></html>");
        webEngine.getLoadWorker().stateProperty().addListener(
                (ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) -> {
                    if (newValue == Worker.State.SUCCEEDED && ckEditor == null) {
                        N node = getNode();
                        JSObject window = (JSObject) executeScript("window");
                        if (window != null) {
                            window.setMember("javaThis", this);
                            ckEditor = (JSObject) executeScript("CKEDITOR.replace('ckEditorDiv', {resize_enabled: false, on: {'instanceReady': function(e) {e.editor.execCommand('maximize'); e.editor.on('change', function() {javaThis.onEditorDataChanged();});}}});");
                            updateText(node.getText());
                        }
                    }
                } );
    }

/*
    @Override
    protected void resizeWebView(double width, double height) {
        super.resizeWebView(width, height);
        if (ckEditor != null)
            ckEditor.call("resize", width - 20, height - 20);
    }
*/

    private String lastUpdateText; // last text passed to updateText() by application code
    private String lastUpdateTextEditorData; // editor data corresponding to last text (may be reformatted by editor)

    @Override
    public void updateText(String text) {
        // We pass the text to the editor, unless this is the same text as last time and the editor data hasn't changed
        if (ckEditor != null) {
            boolean identical = Objects.areEquals(text, lastUpdateText) && Objects.areEquals(lastUpdateTextEditorData, getEditorData());
            if (!identical) {
                lastUpdateText = text;
                lastUpdateTextEditorData = null;
                ckEditor.call("setData", text);
            }
        }
    }

    public void onEditorDataChanged() {
        // We don't reset the node text on subsequent editor notification after updateText(), because the editor probably
        // reformatted the text, be this shouldn't be considered as a user change.
        if (lastUpdateTextEditorData == null)
            lastUpdateTextEditorData = getEditorData();
        else // Should be a user change, so we reset the node text in this case
            getNode().setText(getEditorData());
    }

    private String getEditorData() {
        return ckEditor.call("getData").toString();
    }
}