package dev.webfx.extras.theme;

import dev.webfx.kit.util.properties.FXProperties;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.Map;

import static dev.webfx.extras.theme.FacetNodeType.*;
import static dev.webfx.extras.theme.FacetState.*;

/**
 * @author Bruno Salmon
 */
public class Facet {

    public static final Text GENERIC_TEXT_NODE = new Text();
    public static final Facet GENERIC_FACET = new Facet(null, new Region()).setTextNode(GENERIC_TEXT_NODE);
    private final Object facetCategory;
    private final Map<Object /* presumably FacetNodeType */, Node> facetNodes = new HashMap<>();
    private final Map<Object, ObservableValue<?>> facetStates = new HashMap<>();
    private final Map<Object, Object> facetValues = new HashMap<>();
    private boolean registered;


    public Facet(Object facetCategory) {
        this(facetCategory, null);
    }

    public Facet(Object facetCategory, Node containerNode) {
        this.facetCategory = facetCategory;
        setContainerNode(containerNode);
        setBackgroundNode(containerNode);
    }

    public Object getFacetCategory() {
        return facetCategory;
    }

    public <T> Facet setLogicValue(T logicValue) {
        return setFacetValue("LOGIC_VALUE", logicValue);
    }

    public <T> T getLogicValue() {
        return getFacetValue("LOGIC_VALUE");
    }

    public <T> Facet setFacetValue(Object facetValueKey, T facetValue) {
        facetValues.put(facetValueKey, facetValue);
        return this;
    }

    public <T> T getFacetValue(Object facetValueKey) {
        return (T) facetValues.get(facetValueKey);
    }

    public <T extends Node> Facet setFacetNode(Object facetNodeKey, T facetNode) {
        facetNodes.put(facetNodeKey, facetNode);
        if (registered)
            style();
        return this;
    }

    public <T extends Node> T getFacetNode(Object facetNodeKey) {
        return (T) facetNodes.get(facetNodeKey);
    }

    public Facet setFacetStateProperty(Object facetStateKey, ObservableValue<?> facetStateProperty) {
        FXProperties.runOnPropertyChange(this::style, facetStateProperty);
        facetStates.put(facetStateKey, facetStateProperty);
        return this;
    }

    public <T> ObservableValue<T> getFacetStateProperty(Object facetStateKey) {
        return (ObservableValue<T>) facetStates.get(facetStateKey);
    }

    public <T> T getFacetState(Object facetStateKey) {
        ObservableValue<T> facetStateProperty = getFacetStateProperty(facetStateKey);
        return facetStateProperty == null ? null : facetStateProperty.getValue();
    }

    public ObservableValue<Boolean> getOrCreateBooleanFacetStateProperty(Object facetStateKey) {
        ObservableValue<Boolean> facetStateProperty = getFacetStateProperty(facetStateKey);
        if (facetStateProperty == null)
            setFacetStateProperty(facetStateKey, facetStateProperty = new SimpleBooleanProperty());
        return facetStateProperty;
    }

    public ObservableValue<?> getOrCreateObjectFacetStateProperty(Object facetStateKey) {
        ObservableValue<?> facetStateProperty = getFacetStateProperty(facetStateKey);
        if (facetStateProperty == null)
            setFacetStateProperty(facetStateKey, facetStateProperty = new SimpleObjectProperty());
        return facetStateProperty;
    }

    public Facet setBooleanFacetState(Object facetStateKey, boolean facetStateValue) {
        ObservableValue<Boolean> facetStateProperty = getOrCreateBooleanFacetStateProperty(facetStateKey);
        if (facetStateProperty instanceof Property)
            ((Property<Boolean>) facetStateProperty).setValue(facetStateValue);
        return this;
    }

    public boolean getBooleanFacetState(Object facetStateKey) {
        return Boolean.TRUE.equals(getFacetState(facetStateKey));
    }

    public Facet style() {
        if (!registered) {
            registered = true;
            ThemeRegistry.styleFacet(this);
        } else
            ThemeRegistry.styleFacetNow(this);
        return this;
    }

    public Facet setOnMouseClicked(EventHandler<? super MouseEvent> eventHandler) {
        Node containerNode = getContainerNode();
        containerNode.setCursor(Cursor.HAND);
        containerNode.setOnMouseClicked(eventHandler);
        return this;
    }

    // Shortcut methods

    public <T extends Node> Facet setContainerNode(T containerNode) {
        return setFacetNode(CONTAINER_NODE, containerNode);
    }

    public <T extends Node> T getContainerNode() {
        if (!registered)
            style();
        return getFacetNode(CONTAINER_NODE);
    }

    public <T extends Node> Facet setTextNode(T textNode) {
        return setFacetNode(TEXT_NODE, textNode);
    }

    public <T extends Node> T getTextNode() {
        return getFacetNode(TEXT_NODE);
    }

    public <T extends Node> Facet setGraphicNode(T primaryGraphicNode) {
        return setFacetNode(GRAPHIC_NODE, primaryGraphicNode);
    }

    public <T extends Node> T getGraphicNode() {
        return getFacetNode(GRAPHIC_NODE);
    }

    public <T extends Node> Facet setBackgroundNode(T textNode) {
        return setFacetNode(BACKGROUND_NODE, textNode);
    }

    public <T extends Node> T getBackgroundNode() {
        return getFacetNode(BACKGROUND_NODE);
    }

    public Facet setSelectedProperty(ObservableValue<Boolean> selectedProperty) {
        return setFacetStateProperty(SELECTED, selectedProperty);
    }

    public Facet setSelected(boolean selected) {
        return setBooleanFacetState(SELECTED, selected);
    }

    public boolean isSelected() {
        return getBooleanFacetState(SELECTED);
    }

    public Facet setShadowedProperty(ObservableValue<Boolean> shadowedProperty) {
        return setFacetStateProperty(SHADOWED, shadowedProperty);
    }

    public Facet setShadowed(boolean shadowed) {
        return setBooleanFacetState(SHADOWED, shadowed);
    }

    public boolean isShadowed() {
        return getBooleanFacetState(SHADOWED);
    }

    public Facet setRoundedProperty(ObservableValue<Boolean> roundedProperty) {
        return setFacetStateProperty(ROUNDED, roundedProperty);
    }

    public Facet setRounded(boolean rounded) {
        return setBooleanFacetState(ROUNDED, rounded);
    }

    public boolean isRounded() {
        return getBooleanFacetState(ROUNDED);
    }

    public Facet setBorderedProperty(ObservableValue<Boolean> borderedProperty) {
        return setFacetStateProperty(BORDERED, borderedProperty);
    }

    public Facet setBordered(boolean bordered) {
        return setBooleanFacetState(BORDERED, bordered);
    }

    public boolean isBordered() {
        return getBooleanFacetState(BORDERED);
    }

    public Facet setInvertedProperty(ObservableValue<Boolean> invertedProperty) {
        return setFacetStateProperty(INVERTED, invertedProperty);
    }

    public Facet setInverted(boolean inverted) {
        return setBooleanFacetState(INVERTED, inverted);
    }

    public boolean isInverted() {
        return getBooleanFacetState(INVERTED);
    }

    public Facet setDisabledProperty(ObservableValue<Boolean> disabledProperty) {
        return setFacetStateProperty(DISABLED, disabledProperty);
    }

    public Facet setDisabled(boolean disabled) {
        return setBooleanFacetState(DISABLED, disabled);
    }

    public boolean isDisabled() {
        return getBooleanFacetState(DISABLED);
    }

    public Facet setFillProperty(Property<Paint> fillProperty) {
        return setFacetStateProperty(FILL_PROPERTY, fillProperty);
    }

    public Property<Paint> getFillProperty() {
        ObservableValue<Paint> facetStateProperty = getFacetStateProperty(FILL_PROPERTY);
        return (Property<Paint>) facetStateProperty;
    }

    public Facet setFontProperty(Property<Font> fontProperty) {
        return setFacetStateProperty(FONT_PROPERTY, fontProperty);
    }

    public Property<Font> getFontProperty() {
        ObservableValue<Font> facetStateProperty = getFacetStateProperty(FONT_PROPERTY);
        return (Property<Font>) facetStateProperty;
    }

    public Property<FontDef> getRequestFontProperty() {
        ObservableValue<FontDef> facetStateProperty = getFacetStateProperty(REQUESTED_FONT);
        return (Property<FontDef>) facetStateProperty;
    }

    public Facet requestedFont(FontDef font) {
        return setRequestedFont(font);
    }

    public Facet setRequestedFont(FontDef font) {
        getOrCreateObjectFacetStateProperty(REQUESTED_FONT);
        getRequestFontProperty().setValue(font);
        return this;
    }

    public FontDef getRequestedFont() {
        return getFacetState(REQUESTED_FONT);
    }

}
