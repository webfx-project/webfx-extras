package dev.webfx.extras.webtext;

import dev.webfx.extras.webtext.registry.WebTextRegistry;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Orientation;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.util.function.Function;

/**
 * @author Bruno Salmon
 */
public class HtmlText extends Control {

    public HtmlText() {
        setMaxHeight(USE_PREF_SIZE);
    }

    public HtmlText(String text) {
        setText(text);
    }

    private final StringProperty textProperty = new SimpleStringProperty();
    public StringProperty textProperty() {
        return textProperty;
    }
    public void setText(String text) {
        textProperty.setValue(text);
    }
    public String getText() {
        return textProperty.getValue();
    }

    private final Property<Font> fontProperty = new SimpleObjectProperty<>();
    public Property<Font> fontProperty() {
        return fontProperty;
    }
    public void setFont(Font font) {
        fontProperty.setValue(font);
    }
    public Font getFont() {
        return fontProperty.getValue();
    }

    private final Property<Paint> fillProperty = new SimpleObjectProperty<>();
    public Property<Paint> fillProperty() {
        return fillProperty;
    }
    public void setFill(Paint fill) {
        fillProperty.setValue(fill);
    }
    public Paint getFill() {
        return fillProperty.getValue();
    }

    // Size computing functions set by FxHtmlTextTextFlowPeer to redirect the computation to the TextFlow component
    public Function<Double, Double> computeMinWidthFunction, computeMinHeightFunction, computePrefWidthFunction, computePrefHeightFunction, computeMaxWidthFunction, computeMaxHeightFunction;

    private void checkPeer() {
        if (DEFAULT_SKIN_FACTORY != null && getSkin() == null)
            setSkin(DEFAULT_SKIN_FACTORY.apply(this));
    }

    @Override
    protected double computeMinWidth(double height) {
        checkPeer();
        if (computeMinWidthFunction != null)
            return computeMinWidthFunction.apply(height);
        return super.computeMinWidth(height);
    }

    @Override
    protected double computeMinHeight(double width) {
        checkPeer();
        if (computeMinHeightFunction != null)
            return computeMinHeightFunction.apply(width);
        return super.computeMinHeight(width);
    }

    @Override
    protected double computePrefWidth(double height) {
        checkPeer();
        if (computePrefWidthFunction != null)
            return computePrefWidthFunction.apply(height);
        return 10000;
    }

    @Override
    protected double computePrefHeight(double width) {
        checkPeer();
        if (computePrefHeightFunction != null)
            return computePrefHeightFunction.apply(width);
        return super.computePrefHeight(width);
    }

    @Override
    protected double computeMaxWidth(double height) {
        checkPeer();
        if (computeMaxWidthFunction != null)
            return computeMaxWidthFunction.apply(height);
        return 10000;
    }

    @Override
    protected double computeMaxHeight(double width) {
        checkPeer();
        if (computeMaxHeightFunction != null)
            return computeMaxHeightFunction.apply(width);
        return super.computeMaxHeight(width);
    }

    private static Function<Control, Skin<?>> DEFAULT_SKIN_FACTORY;

    public static void setDefaultSkinFactory(Function<Control, Skin<?>> defaultSkinFactory) {
        DEFAULT_SKIN_FACTORY = defaultSkinFactory;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return DEFAULT_SKIN_FACTORY == null ? null : DEFAULT_SKIN_FACTORY.apply(this);
    }

    @Override
    public Orientation getContentBias() {
        return Orientation.HORIZONTAL; // To say that the min/pref/max height depends on the width (like TextFlow)
    }

    static {
        WebTextRegistry.registerHtmlText();
    }
}
