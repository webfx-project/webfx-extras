package dev.webfx.extras.cell.renderer;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * @author Bruno Salmon
 */
final class RenderingContextApplier {

    static Text applyRenderingContextToText(Text text, ValueRenderingContext context) {
        TextAlignment textAlignment = toTextAlignment(context.getTextAlign());
        if (textAlignment != null) {
            text.setTextAlignment(textAlignment);
        }
        return text;
    }

    static TextField applyRenderingContextToTextField(TextField textField, ValueRenderingContext context) {
        Pos alignment = toAlignment(context.getTextAlign());
        if (alignment != null) {
            textField.setAlignment(alignment);
        }
        return textField;
    }

    static Label applyRenderingContextToLabeled(Label labeled, ValueRenderingContext context) {
        TextAlignment textAlignment = toTextAlignment(context.getTextAlign());
        if (textAlignment != null) {
            labeled.setTextAlignment(textAlignment);
        }
        return labeled;
    }

    private static TextAlignment toTextAlignment(String textAlign) {
        if (textAlign != null) {
            switch (textAlign) {
                case "left":   return TextAlignment.LEFT;
                case "center": return TextAlignment.CENTER;
                case "right":  return TextAlignment.RIGHT;
            }
        }
        return null;
    }

    private static Pos toAlignment(String textAlign) {
        if (textAlign != null) {
            switch (textAlign) {
                case "left":   return Pos.BASELINE_LEFT;
                case "center": return Pos.BASELINE_CENTER;
                case "right":  return Pos.BASELINE_RIGHT;
            }
        }
        return null;
    }

}
