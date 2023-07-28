package dev.webfx.extras.theme;

import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/**
 * @author Bruno Salmon
 */
public class FontDef {
    private final String family;
    private final FontWeight weight;
    private final FontPosture posture;
    private final double size;


    public static FontDef font(String family, FontWeight weight, FontPosture posture, double size) {
        return new FontDef(family, weight, posture, size);
    }

    public static FontDef font(String family, FontWeight weight, double size) {
        return font(family, weight, null, size);
    }

    public static FontDef font(String family, FontPosture posture, double size) {
        return font(family, null, posture, size);
    }

    public static FontDef font(String family, double size) {
        return font(family, null, null, size);
    }

    public static FontDef font(String family) {
        return font(family, null, null, -1);
    }

    public static FontDef font(double size) {
        return font(null, null, null, size);
    }

    public static FontDef font(FontWeight weight, FontPosture posture, double size) {
        return font(null, weight, posture, size);
    }

    public static FontDef font(FontWeight weight, double size) {
        return font(null, weight, size);
    }

    public static FontDef font(FontPosture posture, double size) {
        return font((String) null, posture, size);
    }


    public FontDef(String family, FontWeight weight, FontPosture posture, double size) {
        this.family = family;
        this.weight = weight;
        this.posture = posture;
        this.size = size;
    }

    public String getFamily() {
        return family;
    }

    public FontWeight getWeight() {
        return weight;
    }

    public FontPosture getPosture() {
        return posture;
    }

    public double getSize() {
        return size;
    }
}
