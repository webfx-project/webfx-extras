package dev.webfx.extras.webtext.peers.gwt.html;

import com.google.gwt.core.client.JavaScriptObject;
import dev.webfx.extras.webtext.HtmlTextEditor;
import dev.webfx.extras.webtext.peers.base.HtmlTextEditorPeerBase;
import dev.webfx.extras.webtext.peers.base.HtmlTextEditorPeerMixin;
import dev.webfx.kit.mapper.peers.javafxgraphics.HasNoChildrenPeers;
import dev.webfx.kit.mapper.peers.javafxgraphics.SceneRequester;
import dev.webfx.kit.mapper.peers.javafxgraphics.gwt.html.HtmlRegionPeer;
import dev.webfx.kit.mapper.peers.javafxgraphics.gwt.html.layoutmeasurable.HtmlLayoutMeasurable;
import dev.webfx.kit.mapper.peers.javafxgraphics.gwt.util.HtmlUtil;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.platform.console.Console;
import dev.webfx.platform.util.Objects;
import dev.webfx.platform.util.Strings;
import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

/**
 * @author Bruno Salmon
 */
public final class HtmlHtmlTextEditorPeer
        <N extends HtmlTextEditor, NB extends HtmlTextEditorPeerBase<N, NB, NM>, NM extends HtmlTextEditorPeerMixin<N, NB, NM>>
        extends HtmlRegionPeer<N, NB, NM>
        implements HtmlTextEditorPeerMixin<N, NB, NM>, HtmlLayoutMeasurable, HasNoChildrenPeers {

    private static final String ckEditorUrl = "https://cdn.ckeditor.com/4.22.1/full/ckeditor.js";

    private final HTMLDivElement div = HtmlUtil.createDivElement();
    private JavaScriptObject ckEditor;
    private boolean instanceReady;

    public HtmlHtmlTextEditorPeer() {
        this((NB) new HtmlTextEditorPeerBase());
    }

    HtmlHtmlTextEditorPeer(NB base) {
        super(base, HtmlUtil.createDivElement());
        HtmlUtil.setChild(getElement(), div);
    }

    @Override
    public void bind(N node, SceneRequester sceneRequester) {
        super.bind(node, sceneRequester);
        HtmlUtil.loadScript(ckEditorUrl, this::recreateCKEditorIfRequired);

        FXProperties.runOnPropertiesChange(() -> {
            if (node.getScene() == null) {
                if (ckEditor != null) {
                    callCKEditorDestroy(ckEditor);
                    ckEditor = null;
                }
            } else { // Going back to the page
                recreateCKEditorIfRequired();
            }
        }, node.sceneProperty());
    }

    @Override
    public void updateText(String text) {
        if (ckEditor != null && instanceReady && !Objects.areEquals(text, callCKEditorGetData(ckEditor)))
            callCKEditorSetData(ckEditor, Strings.toSafeString(text));
    }

    @Override
    public void updateFont(Font font) {
        // TODO
    }

    @Override
    public void updateFill(Paint fill) {
        // TODO
    }

    @Override
    public void updateWidth(Number width) {
        super.updateWidth(width);
        if (ckEditor != null && instanceReady)
            callCKEditorResize(ckEditor, width.doubleValue(), getNode().getHeight());
    }

    @Override
    public void updateHeight(Number height) {
        super.updateHeight(height);
        if (ckEditor != null && instanceReady)
            callCKEditorResize(ckEditor, getNode().getWidth(), height.doubleValue());
    }

    private boolean recreateCKEditorIfRequired() {
        if (ckEditor != null && !Strings.isEmpty(getCKEditorInnerHTML(ckEditor)))
            return false;
        Console.log("Recreating CKEditor");
        if (ckEditor != null)
            callCKEditorDestroy(ckEditor);
        instanceReady = false;
        N node = getNode();
        ckEditor = callCKEditorReplace(div, node.getWidth(), node.getHeight(), this);
        resyncEditorFromNodeText(false);
        return true;
    }

    private static native String getCKEditorInnerHTML(JavaScriptObject ckEditor) /*-{
        var container = ckEditor.container;
        if (container) {
            var contentDocument = container.getElementsByTag('iframe').$[0].contentDocument;
            if (contentDocument)
                return contentDocument.body.innerHTML;
        }
        return null;
    }-*/;

    private static native JavaScriptObject callCKEditorReplace(Element textArea, double width, double height, HtmlHtmlTextEditorPeer javaPeer) /*-{
        return $wnd.CKEDITOR.replace(textArea, {resize_enabled: false, on: {'instanceReady': function(e) {e.editor.resize(width, height); javaPeer.@HtmlHtmlTextEditorPeer::resyncEditorFromNodeText(Z)(true); e.editor.on('change', javaPeer.@HtmlHtmlTextEditorPeer::resyncNodeTextFromEditor().bind(javaPeer));}}});
    }-*/;

    private static native void callCKEditorSetData(JavaScriptObject ckEditor, String data) /*-{
        //$wnd.console.log('Calling setData() with data = ' + data);
        ckEditor.setData(data);
    }-*/;

    private static native String callCKEditorGetData(JavaScriptObject ckEditor) /*-{
        return ckEditor.getData();
    }-*/;

    private static native void callCKEditorResize(JavaScriptObject ckEditor, double width, double height) /*-{
        //$wnd.console.log('Calling resize() with width = ' + width + ', height = ' + height);
        ckEditor.resize(width, height);
    }-*/;

    private static native void callCKEditorDestroy(JavaScriptObject ckEditor) /*-{
        ckEditor.destroy();
    }-*/;

    private void resyncNodeTextFromEditor() { // Called back from JS - see callCKEditorReplace()
        getNode().setText(callCKEditorGetData(ckEditor));
    }

    private void resyncEditorFromNodeText(boolean js) {
        if (js)
            instanceReady = true;
        callCKEditorSetData(ckEditor, getNode().getText());
    }

}