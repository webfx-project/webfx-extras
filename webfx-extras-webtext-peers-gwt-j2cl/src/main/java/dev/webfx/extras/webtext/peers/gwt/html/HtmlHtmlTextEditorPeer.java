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

        FXProperties.runOnPropertiesChange(() -> {
            if (node.getScene() == null) {
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

    @Override
    public void updateText(String text) {
        if (ckEditor != null && instanceReady && !Objects.areEquals(text, ckEditor.getData())) {
            ckEditor.setData(Strings.toSafeString(text));
        }
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

    private void resyncNodeTextFromEditor() {
        getNode().setText(ckEditor.getData());
    }

    private void resyncEditorFromNodeText(boolean js) {
        if (js)
            instanceReady = true;
        ckEditor.setData(getNode().getText());
    }

}