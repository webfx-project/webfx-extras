package dev.webfx.extras.styles.materialdesign.util;

import dev.webfx.extras.styles.materialdesign.textfield.MaterialTextField;
import dev.webfx.extras.styles.materialdesign.textfield.MaterialTextFieldSkin;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;

/**
 * @author Bruno Salmon
 */
public final class MaterialUtil {

    public static <T extends TextField> T makeMaterial(T textField) {
        return makeMaterial(textField, new MaterialTextFieldSkin(textField));
    }

/*
    public static <T extends Button> T makeMaterial(T button) {
        return makeMaterial(button, new MaterialButtonSkin(button));
    }

*/
    public static <T extends Control> T makeMaterial(T control, Skin<?> materialSkin) {
        if (materialSkin != null)
            control.setSkin(materialSkin);
        control.getStyleClass().add("material");
        return control;
    }

    public static MaterialTextField getMaterialTextField(Control control) {
        return getMaterialTextField(control.getSkin());
    }

    public static MaterialTextField getMaterialTextField(Skin<?> skin) {
        if (skin instanceof MaterialTextField)
            return ((MaterialTextField) skin);
        return null;
    }

}
