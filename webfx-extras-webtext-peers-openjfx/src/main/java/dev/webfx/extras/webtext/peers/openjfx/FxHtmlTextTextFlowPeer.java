package dev.webfx.extras.webtext.peers.openjfx;

import dev.webfx.extras.webtext.HtmlText;
import dev.webfx.extras.webtext.peers.base.HtmlTextPeerBase;
import dev.webfx.extras.webtext.peers.base.HtmlTextPeerMixin;
import dev.webfx.kit.mapper.peers.javafxgraphics.SceneRequester;
import dev.webfx.kit.mapper.peers.javafxgraphics.openjfx.FxLayoutMeasurable;
import dev.webfx.kit.mapper.peers.javafxgraphics.openjfx.FxNodePeer;
import dev.webfx.platform.util.Numbers;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.*;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Bruno Salmon
 */
public class FxHtmlTextTextFlowPeer
        <FxN extends TextFlow, N extends HtmlText, NB extends HtmlTextPeerBase<N, NB, NM>, NM extends HtmlTextPeerMixin<N, NB, NM>>
        extends FxNodePeer<FxN, N, NB, NM>
        implements HtmlTextPeerMixin<N, NB, NM>, FxLayoutMeasurable {

    protected final TextFlow textFlow = new TextFlow();
    private String text;
    private Font font;
    private Paint fill;

    public FxHtmlTextTextFlowPeer() {
        this((NB) new HtmlTextPeerBase());
    }

    FxHtmlTextTextFlowPeer(NB base) {
        super(base);
        updateText(null);
    }

    @Override
    protected FxN createFxNode() {
        return (FxN) textFlow;
    }

    @Override
    public void bind(N node, SceneRequester sceneRequester) {
        super.bind(node, sceneRequester);
        node.computeMinWidthFunction   = this::minWidth;
        node.computeMinHeightFunction  = this::minHeight;
        node.computePrefWidthFunction  = this::prefWidth;
        node.computePrefHeightFunction = this::prefHeight;
        node.computeMaxWidthFunction   = this::maxWidth;
        node.computeMaxHeightFunction  = this::maxHeight;
    }

    @Override
    public void updateText(String text) {
        if (text != this.text) {
            this.text = text;
            updateTextFlow();
        }
    }

    private void updateTextFlow() {
        renderHtmlInTextFlow(text, font, fill, textFlow);
    }

    @Override
    public void updateFont(Font font) {
        if (font != this.font) {
            this.font = font;
            updateTextFlow();
        }
    }

    @Override
    public void updateFill(Paint fill) {
        if (fill != this.fill) {
            this.fill = fill;
            updateTextFlow();
        }
    }

    @Override
    public void updateWidth(Number width) {
        double w = Numbers.doubleValue(width);
        textFlow.resize(w, getNode().getHeight());
    }

    @Override
    public void updateHeight(Number height) {
        double h = Numbers.doubleValue(height);
        textFlow.resize(getNode().getWidth(), h);
    }

    @Override
    public void updateBackground(Background background) {
        textFlow.setBackground(background);
    }

    @Override
    public void updateBorder(Border border) {
        textFlow.setBorder(border);
    }

    @Override
    public void updatePadding(Insets padding) {
        textFlow.setPadding(padding);
    }

    @Override
    public double minWidth(double height) {
        return textFlow.minWidth(height);
    }

    @Override
    public double minHeight(double width) {
        return textFlow.minHeight(width);
    }

    @Override
    public double prefWidth(double height) {
        return textFlow.prefWidth(height);
    }

    @Override
    public double prefHeight(double width) {
        return textFlow.prefHeight(width);
    }

    @Override
    public double maxWidth(double height) {
        return textFlow.maxWidth(height);
    }

    @Override
    public double maxHeight(double width) {
        return textFlow.maxHeight(width);
    }

    /****** Static methods ******/

    public static void renderHtmlInTextFlow(String html, Font font, Paint fill, TextFlow textFlow) {
        List<Pair<String, HtmlStyle>> pairs = chopHtml(html, font, fill);
        textFlow.getChildren().setAll(
                pairs.stream()
                        .map(pair -> createStyledText(pair.getKey(), pair.getValue()))
                        .collect(Collectors.toList()));
        if (textFlow.getChildren().size() >= 1)
            textFlow.setTextAlignment(((Text)textFlow.getChildren().get(0)).getTextAlignment());
        //textFlow.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
    }

    private static List<Pair<String, HtmlStyle>> chopHtml(String html, Font font, Paint fill) {
        return chopHtml(html, new HtmlStyle(font, fill));
    }

    private static List<Pair<String, HtmlStyle>> chopHtml(String html, HtmlStyle parentStyle) {
        List<Pair<String, HtmlStyle>> list = new ArrayList<>();
        if (html == null)
            return list;
        html = html.replaceAll("\n", ""); // Ignoring \n in text (because html ignores them as well, line breaks depend on tag such as p, br, etc...)
        html = html.replaceAll("\\s+", " ");
        html = html.replaceAll("&nbsp;", " ");
        html = html.replaceAll("&rdquo;", "”");
        html = html.replaceAll("&ldquo;", "“");
        if (html.isEmpty())
            return list;
        int pos = 0;
        while (true) {
            int openingPos = html.indexOf("<", pos);
            if (openingPos == -1) {
                // Adding the remaining text at the end with the style unchanged
                if (pos < html.length())
                    list.add(new Pair<>(html.substring(pos), parentStyle));
                break;
            }
            // Adding the text before the tag with the style unchanged
            if (openingPos > pos)
                list.add(new Pair<>(html.substring(pos, openingPos), parentStyle));
            // Capturing the html tag
            pos = openingPos + 1;
            while (Character.isLetterOrDigit(html.charAt(pos)))
                pos++;
            String tag = html.substring(openingPos + 1, pos);
            // Searching the end of tag
            pos = Math.max(pos, html.indexOf('>', pos));
            String closingTag = "</" + tag + ">";
            int closingPos;
            if (html.charAt(pos - 1) == '/') {
                closingPos = pos + 1;
                closingTag = "";
            } else {
                closingPos = html.indexOf(closingTag, pos);
                if (closingPos == -1) {
                    closingPos = pos + 1; // html.length();
                    closingTag = "";
                }
            }
            // Deriving the style in dependence of the tag
            HtmlStyle derivedStyle = new HtmlStyle(parentStyle);
            String tagWithAttributes = html.substring(openingPos, pos);
            // Capturing css class
            String cssClass = captureAttribute("class", tagWithAttributes);
            // Marking the derived style in order to render the expected visual effect of the HTML tag
            switch (tag.toLowerCase()) {
                case "a":
                    derivedStyle.setHref(captureAttribute("href", tagWithAttributes));
                    //derivedStyle.setFill(Color.BLUE); // Commented as this is not always wanted (ex: FX2048 About).
                    // Instead, we just apply the 'html-link' class, and this is the responsibility of the developer to define an associated CSS rule
                    cssClass = "html-link" + (cssClass == null ? "" : " " + cssClass);
                    break;
                case "u":
                    derivedStyle.setUnderlined();
                    break;
                case "b":
                case "strong":
                    derivedStyle.setBold();
                    break;
                case "i":
                    derivedStyle.setItalic();
                    break;
                case "center":
                    derivedStyle.setTextAlignment(TextAlignment.CENTER);
                    break;
                case "br":
                case "p":
                case "div":
                    derivedStyle.setLineBreak();
                    break;
            }
            // Capturing color attribute
            String color = captureAttribute("color", tagWithAttributes);
            // Capturing style
            String style = captureAttribute("style", tagWithAttributes);
            if (style != null) {
                color = captureAttribute("color", style, ':', ';');
                String fontWeight = captureAttribute("font-weight", style, ':', ';');
                if (fontWeight != null && fontWeight.equals("bold"))
                    derivedStyle.setBold();
                String fontSize = captureAttribute("font-size", style, ':', ';');
                if (fontSize != null && fontSize.endsWith("px"))
                    derivedStyle.setFontSize(Double.parseDouble(fontSize.substring(0, fontSize.length() - 2)));
                String fontStyle = captureAttribute("font-style", style, ':', ';');
                if (fontStyle != null && (fontStyle.equals("italic") || fontStyle.equals("oblique")))
                    derivedStyle.setItalic();
            }
            if (color != null)
                derivedStyle.setFill(color.equalsIgnoreCase("inherit") ? null : Color.web(color));
            derivedStyle.setCssClass(cssClass);
            // Chopping the html text inside the tags (between the opening and closing tag) with the derived style
            String insideTagText = html.substring(pos + 1, closingPos);
            if (!insideTagText.isEmpty())
                list.addAll(chopHtml(insideTagText, derivedStyle));
            if (derivedStyle.hasLineBreak())
                list.add(new Pair<>("\n", derivedStyle));
            // Moving forward after the closing tag for next iteration
            pos = closingPos + closingTag.length();
        }
        return list;
    }

    private static String captureAttribute(String attribute, String html) {
        return captureAttribute(attribute, html, '=', null);
    }

    private static String captureAttribute(String attribute, String html, Character equalMark, Character endMark) {
        String token = attribute + equalMark;
        int namePos = -1;
        while (true) {
            namePos = html.indexOf(token, namePos);
            if (namePos == -1)
                return null;
            if (namePos == 0)
                break;
            char previousChar = html.charAt(namePos - 1);
            if (previousChar == ';' || Character.isWhitespace(previousChar))
                break;
            namePos++;
        }
        int valuePos = namePos + token.length();
        if (endMark == null)
            endMark = html.charAt(valuePos++); // assuming quote
        int endPos = html.indexOf(endMark, valuePos);
        if (endPos == -1)
            endPos = html.length();
        return html.substring(valuePos, endPos).trim();
    }

    private static class NoCssText extends Text {}

    private static Text createStyledText(String content, HtmlStyle style) {
        Text text = new NoCssText();
        text.setText(content);
        Font font = style.getFont();
        if (font != null)
            text.setFont(font);
        text.setUnderline(style.isUnderlined());
        Paint fill = style.getFill();
        if (fill != null)
            text.setFill(fill);
        text.setTextAlignment(style.getTextAlignment());
        String href = style.getHref();
        if (href != null)
            text.setCursor(Cursor.HAND);
        String cssClass = style.getCssClass();
        if (cssClass != null)
            text.getStyleClass().setAll(cssClass.split(" "));
        return text;
    }

    private static class HtmlStyle {
        private final HtmlStyle parent;
        private Font font;
        private FontWeight fontWeight;
        private FontPosture fontPosture;
        private double fontSize;
        private Boolean underlined;
        private Paint fill;
        private TextAlignment textAlignment;
        private boolean lineBreak;
        private String href;
        private String cssClass;

        HtmlStyle(HtmlStyle parent) {
            this(parent, null, null);
        }

        HtmlStyle(Font font, Paint fill) {
            this(null, font, fill);
        }

        public HtmlStyle(HtmlStyle parent, Font font, Paint fill) {
            this.parent = parent;
            setFont(font);
            setFill(fill);
        }

        void setFont(Font font) {
            this.font = font;
            if (fontSize == 0 && font != null)
                fontSize = font.getSize();
        }

        void resetFont() {
            Font font = getFont();
            if (font != null)
                setFont(Font.font(font.getFamily(), getFontWeight(), getFontPosture(), getFontSize()));
        }

        Font getFont() {
            return font != null || parent == null ? font : parent.getFont();
        }

        FontWeight getFontWeight() {
            return fontWeight != null || parent == null ? fontWeight : parent.getFontWeight();
        }

        FontPosture getFontPosture() {
            return fontPosture != null || parent == null ? fontPosture : parent.getFontPosture();
        }

        double getFontSize() {
            return fontSize != 0 || parent == null ? fontSize : parent.getFontSize();
        }

        void setBold() {
            fontWeight = FontWeight.BOLD;
            resetFont();
        }

        void setItalic() {
            fontPosture = FontPosture.ITALIC;
            resetFont();
        }

        void setFontSize(double fontSize) {
            this.fontSize = fontSize;
            resetFont();
        }

        void setUnderlined() {
            this.underlined = true;
        }

        boolean isUnderlined() {
            return underlined != null || parent == null ? underlined == Boolean.TRUE : parent.isUnderlined();
        }

        void setFill(Paint fill) {
            this.fill = fill;
        }

        Paint getFill() {
            return fill != null || parent == null ? fill : parent.getFill();
        }

        public void setTextAlignment(TextAlignment textAlignment) {
            this.textAlignment = textAlignment;
        }

        public TextAlignment getTextAlignment() {
            return textAlignment != null || parent == null ? textAlignment : parent.getTextAlignment();
        }

        boolean hasLineBreak() {
            return lineBreak;
        }

        void setLineBreak() {
            this.lineBreak = true;
        }

        void setHref(String href) {
            this.href = href;
        }

        String getHref() {
            return href;
        }

        public String getCssClass() {
            return cssClass;
        }

        public void setCssClass(String cssClass) {
            this.cssClass = cssClass;
        }

        @Override
        public String toString() {
            return "HtmlStyle{" +
                    "font=" + font +
                    (underlined == null ? "" : ", underlined=" + underlined) +
                    (fill == null ? "" : ", fill=" + fill) +
                    (!lineBreak ? "" : ", lineBreak=true") +
                    (href == null ? "": ", href='" + href + '\'') +
                    '}';
        }
    }
}