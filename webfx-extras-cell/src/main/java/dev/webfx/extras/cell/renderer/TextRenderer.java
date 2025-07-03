package dev.webfx.extras.cell.renderer;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import dev.webfx.platform.util.Strings;

import java.util.function.BiFunction;

/**
 * @author Bruno Salmon
 */
public final class TextRenderer implements ValueRenderer {

    public final static TextRenderer SINGLETON = new TextRenderer();

    private BiFunction<Object, Object, TextField> textFieldFactory = (labelKey, placeholderKey) -> new TextField();

    private TextRenderer() {}

    public void setTextFieldFactory(BiFunction<Object, Object, TextField> textFieldFactory) {
        this.textFieldFactory = textFieldFactory;
    }

    @Override
    public Node renderValue(Object value, ValueRenderingContext context) {
        if (context.isReadOnly()) {
            Text text = new Text();
            ValueApplier.applyTextValue(value, text.textProperty());
            return RenderingContextApplier.applyRenderingContextToText(text, context);
        }
        TextField textField = RenderingContextApplier.applyRenderingContextToTextField(textFieldFactory.apply(context.getLabelKey(), context.getPlaceholderKey()), context);
        String stringValue = Strings.toSafeString(value);
        context.bindEditedValuePropertyTo(textField.textProperty(), stringValue);
        return textField;
    }

}
