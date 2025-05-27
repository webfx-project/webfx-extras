package dev.webfx.extras.switches;

import dev.webfx.kit.launcher.WebFxKitLauncher;
import dev.webfx.kit.launcher.aria.AriaRole;
import dev.webfx.kit.util.properties.FXProperties;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.WritableValue;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * @author Bruno Salmon
 */
public class Switch extends Pane {

    private final static double RADIUS = 10, WIDTH = 45;
    private final Circle switchKnob;

    private final BooleanProperty selectedProperty = FXProperties.newBooleanProperty(() -> updateSwitchUi(true));

    public Switch() {
        super(new Circle(RADIUS - 3));
        switchKnob = (Circle) getChildren().get(0);
        switchKnob.setLayoutY(RADIUS);
        getStyleClass().add("webfx-switch");
        WebFxKitLauncher.setAriaRole(this, AriaRole.SWITCH);
        switchKnob.getStyleClass().add("knob");
        setMinSize(WIDTH, 2 * RADIUS);
        setMaxSize(WIDTH, 2 * RADIUS);
        setOnMouseClicked(e -> setSelected(!isSelected()));
        updateSwitchUi(false);
    }

    public boolean isSelected() {
        return selectedProperty.get();
    }

    public BooleanProperty selectedProperty() {
        return selectedProperty;
    }

    public void setSelected(boolean selected) {
        this.selectedProperty.set(selected);
    }

    private void updateSwitchUi(boolean animate) {
        boolean selected = isSelected();
        double layoutX = selected ? WIDTH - RADIUS : RADIUS;
        getStyleClass().remove(selected ? "unselected" : "selected");
        getStyleClass().add(selected ? "selected" : "unselected");
        if (!animate) {
            switchKnob.setLayoutX(layoutX);
        } else {
            animateProperty(200, switchKnob.layoutXProperty(), layoutX);
        }
        WebFxKitLauncher.setAriaSelected(this, selected);
    }

    static <T> Timeline animateProperty(int durationMillis, WritableValue<T> target, T endValue) {
        Timeline timeline = new Timeline(new KeyFrame(new Duration(durationMillis), new KeyValue(target, endValue, Interpolator.EASE_BOTH)));
        timeline.play();
        return timeline;
    }

}
