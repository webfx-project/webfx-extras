package dev.webfx.extras.webtext.peers.gwt.html;

import dev.webfx.extras.webtext.HtmlTextEditor;
import dev.webfx.extras.webtext.peers.base.HtmlTextEditorPeerBase;
import dev.webfx.extras.webtext.peers.base.HtmlTextEditorPeerMixin;
import dev.webfx.kit.mapper.peers.javafxgraphics.HasNoChildrenPeers;
import dev.webfx.kit.mapper.peers.javafxgraphics.SceneRequester;
import dev.webfx.kit.mapper.peers.javafxgraphics.gwtj2cl.html.HtmlRegionPeer;
import dev.webfx.kit.mapper.peers.javafxgraphics.gwtj2cl.html.layoutmeasurable.HtmlLayoutMeasurable;
import dev.webfx.kit.mapper.peers.javafxgraphics.gwtj2cl.util.HtmlUtil;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.platform.console.Console;
import dev.webfx.platform.util.Objects;
import dev.webfx.platform.util.Strings;
import elemental2.dom.Document;
import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLIFrameElement;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import jsinterop.base.JsPropertyMap;

/**
 * @author Bruno Salmon
 */
public final class HtmlHtmlTextEditorPeer
        <N extends HtmlTextEditor, NB extends HtmlTextEditorPeerBase<N, NB, NM>, NM extends HtmlTextEditorPeerMixin<N, NB, NM>>
        extends HtmlRegionPeer<N, NB, NM>
        implements HtmlTextEditorPeerMixin<N, NB, NM>, HtmlLayoutMeasurable, HasNoChildrenPeers {

    private static final String CK_EDITOR_URL_TEMPLATE = "https://cdn.ckeditor.com/4.22.1/${mode}/ckeditor.js";

    private final HTMLDivElement div = HtmlUtil.createDivElement();
    private CKEditor ckEditor;
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

        FXProperties.runOnPropertyChange(scene -> {
            if (scene == null) {
                if (ckEditor != null) {
                    ckEditor.destroy();
                    ckEditor = null;
                }
            } else { // Going back to the page
                recreateCKEditorIfRequired();
            }
        }, node.sceneProperty());
    }

    @Override
    public void updateMode(HtmlTextEditor.Mode mode) {
        String ckEditorUrl = CK_EDITOR_URL_TEMPLATE.replace("${mode}", mode.name().toLowerCase());
        HtmlUtil.loadScript(ckEditorUrl, this::recreateCKEditorIfRequired);
    }

    private String lastUpdateText; // last text passed to updateText() by application code
    private String lastUpdateTextEditorData; // editor data corresponding to last text (maybe reformatted by editor)

    @Override
    public void updateText(String text) {
        // We pass the text to the editor, unless this is the same text as last time and the editor data hasn't changed
        if (ckEditor != null) {
            boolean identical = Objects.areEquals(text, lastUpdateText) && Objects.areEquals(lastUpdateTextEditorData, getEditorData());
            if (!identical) {
                lastUpdateText = text;
                lastUpdateTextEditorData = null;
                ckEditor.setData(Strings.toSafeString(text));
            }
        }
    }

    public void onEditorDataChanged() {
        // We don't reset the node text on subsequent editor notification after updateText(), because the editor probably
        // reformatted the text, be this shouldn't be considered as a user change.
        boolean skipNodeText = lastUpdateTextEditorData == null;
        lastUpdateTextEditorData = getEditorData();
        if (!skipNodeText) { // Should be a user change, so we reset the node text in this case
            getNode().setText(lastUpdateText = lastUpdateTextEditorData);
        }
    }

    private String getEditorData() {
        return ckEditor.getData();
    }

    private void resyncNodeTextFromEditor() {
        onEditorDataChanged();
    }

    private void resyncEditorFromNodeText(boolean js) {
        if (js)
            instanceReady = true;
        updateText(getNode().getText());
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
        if (ckEditor != null && instanceReady) {
            ckEditor.resize(width.doubleValue(), getNode().getHeight());
        }
    }

    @Override
    public void updateHeight(Number height) {
        super.updateHeight(height);
        if (ckEditor != null && instanceReady) {
            ckEditor.resize(getNode().getWidth(), height.doubleValue());
        }
    }

    private boolean recreateCKEditorIfRequired() {
        if (ckEditor != null && !Strings.isEmpty(getCKEditorInnerHTML(ckEditor)))
            return false;
        Console.log("Recreating CKEditor");
        if (ckEditor != null)
            ckEditor.destroy();
        instanceReady = false;
        N node = getNode();
        ckEditor = CKEditor.replace(div, JsPropertyMap.of(
                "resize_enabled", false,
                "on", JsPropertyMap.of(
                        "instanceReady", (CKEditor.InstanceReadyFn) e -> {
                            e.editor.resize(node.getWidth(), node.getHeight());
                            resyncEditorFromNodeText(true);
                            e.editor.on("change", e2 -> resyncNodeTextFromEditor());
                        }
                )
        ));
        resyncEditorFromNodeText(false);
        // Note: this is the responsibility of this peer to actually set the JavaFX scene focus owner to this node when
        // it actually gains the focus.
        ckEditor.on("focus", e -> getNode().requestFocus());
        return true;
    }

    private static String getCKEditorInnerHTML(CKEditor ckEditor) {
        Element container = ckEditor.container;
        if (container != null) {
            HTMLIFrameElement iframe = (HTMLIFrameElement) container.getElementsByTagName("iframe").getAt(0);
            Document contentDocument = iframe.contentDocument;
            if (contentDocument != null)
                return contentDocument.getElementsByTagName("body").getAt(0).innerHTML;
        }
        return null;
    }

}