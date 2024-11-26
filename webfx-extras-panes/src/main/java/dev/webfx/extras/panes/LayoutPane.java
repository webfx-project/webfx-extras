package dev.webfx.extras.panes;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 * @author Bruno Salmon
 */
public abstract class LayoutPane extends Pane {

    public LayoutPane() {
        super();
    }

    public LayoutPane(Node... children) {
        super(children);
    }

    @Override
    protected void layoutChildren() {
        layoutChildren(getLayoutWidth(), getLayoutHeight());
    }

    protected double getLayoutWidth() {
        return getWidth();
    }

    protected double getLayoutHeight() {
        return getHeight();
    }

    protected double insetsWidth() {
        Insets insets = getInsets();
        return insets.getLeft() + insets.getRight();
    }

    protected double insetsHeight() {
        Insets insets = getInsets();
        return insets.getTop() + insets.getBottom();
    }

    protected void layoutChildren(double width, double height) {
        super.layoutChildren();
    }

    protected void layoutInArea(Node child, double areaX, double areaY,
                                double areaWidth, double areaHeight) {
        layoutInArea(child, areaX, areaY, areaWidth, areaHeight, Pos.TOP_LEFT); // most efficient if POS is not necessary
    }
    protected void layoutInArea(Node child, double areaX, double areaY,
                                double areaWidth, double areaHeight,
                                Pos pos) {
        layoutInArea(child, areaX, areaY, areaWidth, areaHeight, pos.getHpos(), pos.getVpos());
    }

    protected void layoutInArea(Node child, double areaX, double areaY,
                                double areaWidth, double areaHeight,
                                HPos halignment, VPos valignment) {
        layoutInArea(child, areaX, areaY, areaWidth, areaHeight, 0, null ,halignment, valignment);
    }

    }
