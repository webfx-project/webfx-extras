package dev.webfx.extras.time.pickers;

import dev.webfx.extras.panes.MonoPane;
import dev.webfx.extras.util.scene.SceneUtil;
import dev.webfx.kit.util.properties.FXProperties;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author Bruno Salmon
 */
public class DateField {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String CALENDAR_SVG_PATH = "M 32 4 h -2 V 2 a 2 2 90 0 0 -4 0 v 2 H 10 V 2 a 2 2 90 0 0 -4 0 v 2 H 4 c -2.2 0 -4 1.8 -4 4 v 28 c 0 2.2 1.8 4 4 4 h 28 c 2.2 0 4 -1.8 4 -4 V 8 c 0 -2.2 -1.8 -4 -4 -4 z m 0 32 H 4 V 14 h 28 v 22 z M 4 10 V 8 h 28 v 2 H 4 z";

    private final Pane overlayPane;
    private final ObjectProperty<LocalDate> dateProperty = FXProperties.newObjectProperty(this::onDateChanged);

    private final TextField textField = new TextField();
    private final DatePicker datePicker = new DatePicker(new DatePickerOptions().setApplyBorderStyle(false).setApplyMaxSize(false));
    private final HBox container;

    public DateField(Pane overlayPane) {
        this.overlayPane = overlayPane;
        datePicker.selectedDateProperty().bindBidirectional(dateProperty);
        SVGPath calendarIcon = new SVGPath();
        calendarIcon.setContent(CALENDAR_SVG_PATH);
        MonoPane calendarIconPane = new MonoPane(calendarIcon);
        container = new HBox(10, textField, calendarIconPane);
        HBox.setHgrow(textField, Priority.ALWAYS);
        container.setAlignment(Pos.BOTTOM_LEFT);
        ObservableList<Node> overlayChildren = overlayPane.getChildren();
        Node datePickerView = datePicker.getView();
        calendarIconPane.setOnMouseClicked(e -> {
            if (overlayChildren.contains(datePickerView)) {
                overlayChildren.remove(datePickerView);
                return;
            }
            datePickerView.setManaged(false);
            overlayChildren.add(datePickerView);
            relocateDatePicker();
            SceneUtil.runOnceFocusIsOutside(datePickerView, false, () -> overlayChildren.remove(datePickerView));
        });
        // Keeping the position of the date picker up-to-date
        FXProperties.runOnPropertiesChange(this::relocateDatePicker,
            container.layoutXProperty(), container.layoutYProperty(), overlayPane.widthProperty(), overlayPane.heightProperty());
        // Removing the date picker when leaving the scene (ex: on dialog close if we were in a dialog)
        FXProperties.runOnPropertyChange(scene -> {
            if (scene == null) {
                // The reason for running later is to prevent an exception (this listener being called already during overlayChildren modification)
                Platform.runLater(() -> overlayChildren.remove(datePickerView));
            }
        }, container.sceneProperty());
    }

    private void relocateDatePicker() {
        Point2D scenePoint = container.localToScene(0, container.getHeight());
        double sceneX = scenePoint.getX();
        double sceneY = scenePoint.getY();

        // Convert the scene position to the root container's local coordinates
        Point2D localPoint = overlayPane.sceneToLocal(sceneX, sceneY);
        double localX = localPoint.getX();
        double localY = localPoint.getY();

        Node datePickerView = datePicker.getView();
        double width = textField.getWidth();
        double height = datePickerView.prefHeight(width);
        datePickerView.resizeRelocate(localX, localY, width, height);
    }

    public Region getView() {
        return container;
    }

    public TextField getTextField() {
        return textField;
    }

    public DatePicker getDatePicker() {
        return datePicker;
    }

    public LocalDate getDate() {
        return dateProperty.get();
    }

    public void setDate(LocalDate date) {
        dateProperty.set(date);
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return dateProperty;
    }

    private void onDateChanged() {
        textField.setText(DATE_TIME_FORMATTER.format(getDate()));
        // Note: there is no need to update the datePicker selectedDate because this is already done by the bidirectional
        // binding set in the constructor.
    }

}
