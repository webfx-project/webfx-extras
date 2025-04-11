package dev.webfx.extras.time.pickers;

import dev.webfx.extras.panes.MonoPane;
import dev.webfx.extras.time.format.LocalizedTime;
import dev.webfx.extras.util.scene.SceneUtil;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.kit.util.properties.Unregisterable;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * @author Bruno Salmon
 */
public final class DateField {

    private static final String CALENDAR_SVG_PATH = "M 32 4 h -2 V 2 a 2 2 90 0 0 -4 0 v 2 H 10 V 2 a 2 2 90 0 0 -4 0 v 2 H 4 c -2.2 0 -4 1.8 -4 4 v 28 c 0 2.2 1.8 4 4 4 h 28 c 2.2 0 4 -1.8 4 -4 V 8 c 0 -2.2 -1.8 -4 -4 -4 z m 0 32 H 4 V 14 h 28 v 22 z M 4 10 V 8 h 28 v 2 H 4 z";

    private final Pane overlayPane;

    private final ObjectProperty<LocalDate> dateProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<DateTimeFormatter> dateTimeFormatterProperty = new SimpleObjectProperty<>();

    private final TextField textField = new TextField();
    private final DatePicker datePicker = new DatePicker(new DatePickerOptions().setApplyBorderStyle(true).setApplyMaxSize(false));
    private final HBox container;

    private Unregisterable datePickerRelocator, datePickerAutoHiderOnLeavingScene, datePickerAutoHiderOnFocusLoss;
    private final Unregisterable datePickerAutoHiderOnOutsideClick;

    public DateField(Pane overlayPane) {
        this.overlayPane = overlayPane;
        dateTimeFormatterProperty.bind(LocalizedTime.dateFormatterProperty(FormatStyle.SHORT)); // binding by default - can be overridden by application code with dateTimeFormatterProperty().bind(...)
        LocalizedTime.bindLocalDateTextProperty(textField.textProperty(), dateProperty, dateTimeFormatterProperty);
        datePicker.selectedDateProperty().bindBidirectional(dateProperty);
        SVGPath calendarIcon = new SVGPath();
        calendarIcon.setContent(CALENDAR_SVG_PATH);
        MonoPane calendarIconPane = new MonoPane(calendarIcon);
        container = new HBox(10, textField, calendarIconPane);
        HBox.setHgrow(textField, Priority.ALWAYS);
        container.setAlignment(Pos.BOTTOM_LEFT);
        calendarIconPane.setOnMouseClicked(e -> {
            if (isDatePickerShowing()) {
                hideDatePicker();
            } else {
                showDatePicker();
            }
        });
        EventHandler<MouseEvent> eventFilter = me -> {
            Node topClickedNode = me.getPickResult().getIntersectedNode();
            if (topClickedNode != null
                && !SceneUtil.hasAncestor(topClickedNode, datePicker.getView())
                && !SceneUtil.hasAncestor(topClickedNode, calendarIconPane)) {
                hideDatePicker();
            }
        };
        datePickerAutoHiderOnOutsideClick = new Unregisterable() {
            @Override
            public void register() {
                container.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, eventFilter);
            }

            @Override
            public void unregister() {
                container.getScene().removeEventFilter(MouseEvent.MOUSE_PRESSED, eventFilter);
            }
        };
    }

    private void showDatePicker() {
        // Adding the date picker view to the overlay pane
        ObservableList<Node> overlayChildren = overlayPane.getChildren();
        Node datePickerView = datePicker.getView();
        datePickerView.setManaged(false);
        overlayChildren.add(datePickerView);
        // Keeping the position of the date picker up-to-date
        datePickerRelocator = FXProperties.runNowAndOnPropertiesChange(this::relocateDatePicker,
            container.layoutXProperty(), container.layoutYProperty(), overlayPane.widthProperty(), overlayPane.heightProperty());
        // Auto hiding the date picker when leaving the scene (ex: on dialog close if we were in a dialog)
        datePickerAutoHiderOnLeavingScene = FXProperties.runNowAndOnPropertyChange(scene -> {
            if (scene == null) {
                // The reason for running later is to prevent an exception (this listener being called already during overlayChildren modification)
                Platform.runLater(this::hideDatePicker);
            } else {
                datePickerAutoHiderOnOutsideClick.register();
            }
        }, container.sceneProperty());
        datePickerAutoHiderOnFocusLoss = SceneUtil.runOnceFocusIsOutside(datePickerView, false, this::hideDatePicker);
    }

    private void hideDatePicker() {
        overlayPane.getChildren().remove(datePicker.getView());
        if (datePickerRelocator != null) {
            datePickerRelocator.unregister();
            datePickerAutoHiderOnLeavingScene.unregister();
            datePickerAutoHiderOnFocusLoss.unregister();
            datePickerAutoHiderOnOutsideClick.unregister();
            datePickerRelocator = datePickerAutoHiderOnLeavingScene = datePickerAutoHiderOnFocusLoss = null;
        }
    }

    private boolean isDatePickerShowing() {
        return overlayPane.getChildren().contains(datePicker.getView());
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

    public DateTimeFormatter getDateTimeFormatter() {
        return dateTimeFormatterProperty.get();
    }

    public void setDateTimeFormatter(DateTimeFormatter dateTimeFormatter) {
        dateTimeFormatterProperty.set(dateTimeFormatter);
    }

    public ObjectProperty<DateTimeFormatter> dateTimeFormatterProperty() {
        return dateTimeFormatterProperty;
    }
}
