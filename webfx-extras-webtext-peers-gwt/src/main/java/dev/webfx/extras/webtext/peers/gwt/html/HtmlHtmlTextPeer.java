package dev.webfx.extras.webtext.peers.gwt.html;

import dev.webfx.extras.webtext.HtmlText;
import dev.webfx.extras.webtext.peers.base.HtmlTextPeerBase;
import dev.webfx.extras.webtext.peers.base.HtmlTextPeerMixin;
import dev.webfx.extras.webtext.util.WebTextUtil;
import dev.webfx.kit.mapper.peers.javafxcontrols.gwt.html.HtmlControlPeer;
import dev.webfx.kit.mapper.peers.javafxgraphics.HasNoChildrenPeers;
import dev.webfx.kit.mapper.peers.javafxgraphics.gwt.html.NormalWhiteSpacePeer;
import dev.webfx.kit.mapper.peers.javafxgraphics.gwt.html.layoutmeasurable.HtmlLayoutMeasurable;
import dev.webfx.kit.mapper.peers.javafxgraphics.gwt.util.HtmlPaints;
import dev.webfx.kit.mapper.peers.javafxgraphics.gwt.util.HtmlUtil;
import dev.webfx.platform.util.Strings;
import elemental2.dom.Element;
import elemental2.dom.HTMLScriptElement;
import elemental2.dom.Node;
import elemental2.dom.NodeList;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

/**
 * @author Bruno Salmon
 */
public final class HtmlHtmlTextPeer
        <N extends HtmlText, NB extends HtmlTextPeerBase<N, NB, NM>, NM extends HtmlTextPeerMixin<N, NB, NM>>
        extends HtmlControlPeer<N, NB, NM>
        implements HtmlTextPeerMixin<N, NB, NM>, HtmlLayoutMeasurable, NormalWhiteSpacePeer, HasNoChildrenPeers {

    public HtmlHtmlTextPeer() {
        this((NB) new HtmlTextPeerBase());
    }

    HtmlHtmlTextPeer(NB base) {
        super(base, HtmlUtil.createElement("fx-htmltext"));
    }

    @Override
    public void updateText(String text) {
        String html = Strings.toSafeString(text);
        html = WebTextUtil.unescapeEntities(html);
        getElement().innerHTML = html;
        if (text.contains("<script"))
            executeScripts(getElement());
        clearCache();
    }

    @Override
    public void updateFont(Font font) {
        setFontAttributes(font);
        clearCache();
    }

    @Override
    public void updateFill(Paint fill) {
        setElementStyleAttribute("color", HtmlPaints.toHtmlCssPaint(fill));
    }

    private void executeScripts(Node node) {
        if (node instanceof Element) {
            Element element = (Element) node;
            if ("SCRIPT".equalsIgnoreCase(element.tagName)) {
                HTMLScriptElement script = HtmlUtil.createElement("script");
                script.text = element.innerHTML;
                for (int i = 0; i < element.attributes.length; i++)
                    script.setAttribute(element.attributes.getAt(i).name, element.attributes.getAt(i).value);
                element.parentNode.replaceChild(script, element);
                return;
            }
        }
        NodeList<Node> children = node.childNodes;
        for (int i = 0; i < children.length; i++)
            executeScripts(children.item(i));
    }

}