package dev.webfx.extras.time.pickers;

import dev.webfx.extras.theme.text.TextTheme;
import dev.webfx.extras.time.TimeUtil;
import dev.webfx.extras.time.format.TimeFormat;
import dev.webfx.extras.time.layout.calendar.CalendarLayout;
import dev.webfx.extras.time.layout.node.TimeGridPane;
import dev.webfx.extras.time.layout.node.TimePane;
import dev.webfx.extras.util.OptimizedObservableListWrapper;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.platform.util.tuples.Pair;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.function.Function;

/**
 * @author David Hello
 * @author Bruno Salmon
 */
public final class DatePicker {

    private final DatePickerOptions options;
    private final ObjectProperty<LocalDate> selectedDateProperty;
    private final ObservableList<LocalDate> selectedDates = new OptimizedObservableListWrapper<>();

    private final ObjectProperty<YearMonth> displayedYearMonthProperty = FXProperties.newObjectProperty(this::onMonthChanged);

    private VBox container;
    private CalendarLayout<YearMonth, YearMonth> yearMonthCalendarLayout;
    private CalendarLayout<LocalDate, LocalDate> daysOfMonthLayout;
    private Function<LocalDate, Boolean> isDateSelectableFunction = this::isDateSelectableDefault;
    private Function<LocalDate, String> getDateStyleClassFunction = this::getDateStyleClassDefault;
    private Label displayedYearMonthLabel;
    private SVGPath arrowPreviousMonthPath;
    private SVGPath arrowNextMonthPath;

    //This variable is used to retrieve the graphic object Label associated to a date - the string is the CSS property
    private final Map<LocalDate, Pair<Label, String>> dateLabelSelectedStyleClassMap = new HashMap<>();

    public DatePicker() {
        this(new DatePickerOptions());
    }

    public DatePicker(DatePickerOptions options) {
        this.options = options;
        YearMonth initialDisplayedYearMonth = options.getInitialDisplayedYearMonth();
        setDisplayedYearMonth(initialDisplayedYearMonth == null ? YearMonth.now() : initialDisplayedYearMonth);
        if (options.getIsDateSelectableFunction() != null)
            setIsDateSelectableFunction(options.getIsDateSelectableFunction());
        // selectedDate <-> selectedDates sync management
        selectedDateProperty = FXProperties.newObjectProperty(selectedDate -> {
            if (options.isMultipleSelectionAllowed()) { // multiple-selection
                if (selectedDate != null && !selectedDates.contains(selectedDate))
                    selectedDates.add(selectedDate);
            } else { // single selection
                if (selectedDate != null)
                    selectedDates.setAll(selectedDate);
                else
                    selectedDates.clear();
            }
        });
        selectedDates.addListener((ListChangeListener<LocalDate>) change -> {
            // We update the date styles to visually reflect selection to the user
            updateDatesStyles();
            // selectedDates <-> selectedDate sync management
            // Note: updating selectedDate might update selectedDates again, which we can't do during a change event,
            Platform.runLater(() -> { // that's why we postpone this synchronization
                LocalDate selectedDate = getSelectedDate();
                if (options.isMultipleSelectionAllowed()) {
                    if (options.isSortSelectedDates()) {
                        // We first work on a copy, because Collection.sort() always triggers a change event, even if
                        // the list is finally unchanged by the sort, so this would cause an infinite loop
                        List<LocalDate> copy = new ArrayList<>(selectedDates);
                        Collections.sort(copy);
                        if (!selectedDates.equals(copy))
                            selectedDates.setAll(copy); // OptimizedObservableListWrapper will trigger permutations only
                        // TODO: see if OptimizedObservableListWrapper can be improved so we can just replace the code
                        // TODO: above with Collections.sort(selectedDates)
                    }
                    if (selectedDate == null || !selectedDates.contains(selectedDate))
                        setSelectedDate(selectedDates.isEmpty() ? null : selectedDates.get(0));
                } else {
                    LocalDate lastAddedDate = null;
                    while (change.next()) {
                        List<? extends LocalDate> addedSubList = change.getAddedSubList();
                        if (!addedSubList.isEmpty()) {
                            lastAddedDate = addedSubList.get(0);
                        }
                    }
                    setSelectedDate(lastAddedDate != null ? lastAddedDate : selectedDates.isEmpty() ? null : selectedDates.get(0));
                }
            });
        });
    }

    public ObservableList<LocalDate> getSelectedDates() {
        return selectedDates;
    }

    public void setSelectedDates(Collection<LocalDate> col) {
        selectedDates.setAll(col);
    }

    public void clearSelection() {
        selectedDates.clear();
    }

    public LocalDate getSelectedDate() {
        return selectedDateProperty.get();
    }

    public ObjectProperty<LocalDate> selectedDateProperty() {
        return selectedDateProperty;
    }

    public void setSelectedDate(LocalDate date) {
        selectedDateProperty.set(date);
    }

    public YearMonth getDisplayedYearMonth() {
        return displayedYearMonthProperty.get();
    }

    public void setDisplayedYearMonth(YearMonth month) {
        displayedYearMonthProperty.set(month);
    }

    public ObjectProperty<YearMonth> displayedYearMonthProperty() {
        return displayedYearMonthProperty;
    }

    public Node getView() {
        if (container == null) {
            container = new VBox();
            container.getStyleClass().add("webfx-datepicker");
            if (options.isApplyBorderStyle())
                container.getStyleClass().add("border");
            if (options.isApplyMaxSize()) {
                container.setMaxWidth(200);
                container.setMaxHeight(225);
            }
            YearMonth month = getDisplayedYearMonth();
            yearMonthCalendarLayout = new CalendarLayout<>();
            yearMonthCalendarLayout.getChildren().setAll(month);
            BorderPane yearMonthSelectionHBox = new BorderPane();
            yearMonthSelectionHBox.setBackground(Background.fill(Color.WHITE));

            yearMonthSelectionHBox.setPadding(new Insets(10, 0, 10, 0));

            arrowPreviousMonthPath = new SVGPath();
            arrowPreviousMonthPath.setContent("M14.2187 28C10.6383 28 7.20455 26.525 4.67281 23.8995C2.14107 21.274 0.718751 17.713 0.718751 14C0.718751 10.287 2.14107 6.72601 4.67281 4.1005C7.20455 1.475 10.6383 -7.46609e-07 14.2188 -5.90104e-07C17.7992 -4.33598e-07 21.233 1.475 23.7647 4.10051C26.2964 6.72602 27.7187 10.287 27.7187 14C27.7187 17.713 26.2964 21.274 23.7647 23.8995C21.2329 26.525 17.7992 28 14.2187 28ZM20.125 14.875C20.3488 14.875 20.5634 14.7828 20.7216 14.6187C20.8799 14.4546 20.9687 14.2321 20.9687 14C20.9687 13.7679 20.8799 13.5454 20.7216 13.3813C20.5634 13.2172 20.3488 13.125 20.125 13.125L10.3493 13.125L13.9724 9.3695C14.1308 9.2052 14.2198 8.98236 14.2198 8.75C14.2198 8.51764 14.1308 8.2948 13.9724 8.1305C13.8139 7.9662 13.5991 7.87389 13.375 7.87389C13.1509 7.87389 12.9361 7.9662 12.7776 8.1305L7.71513 13.3805C7.63655 13.4618 7.57421 13.5583 7.53167 13.6646C7.48914 13.7709 7.46724 13.8849 7.46724 14C7.46724 14.1151 7.48914 14.2291 7.53167 14.3354C7.57421 14.4417 7.63655 14.5382 7.71513 14.6195L12.7776 19.8695C12.9361 20.0338 13.1509 20.1261 13.375 20.1261C13.5991 20.1261 13.8139 20.0338 13.9724 19.8695C14.1308 19.7052 14.2198 19.4824 14.2198 19.25C14.2198 19.0176 14.1308 18.7948 13.9724 18.6305L10.3493 14.875L20.125 14.875Z");
            arrowPreviousMonthPath.setStrokeWidth(2);
            arrowPreviousMonthPath.getStyleClass().add("arrow");
            arrowPreviousMonthPath.setScaleX(0.7);
            arrowPreviousMonthPath.setScaleY(0.7);
            arrowPreviousMonthPath.setOnMouseClicked(event -> setDisplayedYearMonth(month.minusMonths(1)));

            arrowNextMonthPath = new SVGPath();
            arrowNextMonthPath.setContent("M14.7187 1.29539e-06C18.4318 1.13309e-06 21.9927 1.475 24.6182 4.1005C27.2438 6.72601 28.7188 10.287 28.7188 14C28.7188 17.713 27.2438 21.274 24.6182 23.8995C21.9927 26.525 18.4318 28 14.7188 28C11.0057 28 7.44477 26.525 4.81926 23.8995C2.19375 21.274 0.71875 17.713 0.718749 14C0.718749 10.287 2.19375 6.72602 4.81925 4.10051C7.44476 1.475 11.0057 1.45769e-06 14.7187 1.29539e-06ZM8.59375 13.125C8.36169 13.125 8.13913 13.2172 7.97503 13.3813C7.81094 13.5454 7.71875 13.7679 7.71875 14C7.71875 14.2321 7.81094 14.4546 7.97503 14.6187C8.13913 14.7828 8.36169 14.875 8.59375 14.875L18.7315 14.875L14.9743 18.6305C14.8099 18.7948 14.7176 19.0176 14.7176 19.25C14.7176 19.4824 14.8099 19.7052 14.9743 19.8695C15.1386 20.0338 15.3614 20.1261 15.5938 20.1261C15.8261 20.1261 16.0489 20.0338 16.2133 19.8695L21.4633 14.6195C21.5447 14.5382 21.6094 14.4417 21.6535 14.3354C21.6976 14.2291 21.7203 14.1151 21.7203 14C21.7203 13.8849 21.6976 13.7709 21.6535 13.6646C21.6094 13.5583 21.5447 13.4618 21.4633 13.3805L16.2133 8.1305C16.0489 7.9662 15.8261 7.87389 15.5938 7.87389C15.3614 7.8739 15.1386 7.9662 14.9742 8.1305C14.8099 8.2948 14.7176 8.51764 14.7176 8.75C14.7176 8.98236 14.8099 9.2052 14.9742 9.3695L18.7315 13.125L8.59375 13.125Z");
            arrowNextMonthPath.setStrokeWidth(2);
            arrowNextMonthPath.getStyleClass().add("arrow");
            arrowNextMonthPath.setScaleX(0.7);
            arrowNextMonthPath.setScaleY(0.7);
            arrowNextMonthPath.setOnMouseClicked(event -> setDisplayedYearMonth(month.plusMonths(1)));

            displayedYearMonthLabel = new Label();
            displayedYearMonthLabel.getStyleClass().add("month");
            bindDisplayedYearMonthLabel();
            displayedYearMonthLabel.setPadding(new Insets(0, 0, 0, 0));
            yearMonthSelectionHBox.setLeft(arrowPreviousMonthPath);
            yearMonthSelectionHBox.setCenter(displayedYearMonthLabel);
            yearMonthSelectionHBox.setRight(arrowNextMonthPath);
            CalendarLayout<DayOfWeek, DayOfWeek> daysOfWeekLayout = new CalendarLayout<>();

            daysOfWeekLayout.getChildren().setAll(TimeUtil.generateDaysOfWeek());
            TimePane<DayOfWeek, DayOfWeek> daysOfWeekPane = new TimePane<>(daysOfWeekLayout, this::createDayOfWeekNode);
            daysOfWeekLayout.setChildFixedHeight(20);
            daysOfWeekPane.setBackground(Background.fill(Color.WHITE));

            daysOfMonthLayout = new CalendarLayout<>();
            daysOfMonthLayout.getChildren().setAll(TimeUtil.generateMonthDates(month));
            TimeGridPane<LocalDate, LocalDate> daysOfMonthPane = new TimeGridPane<>(daysOfMonthLayout, this::createDateNode);

            // We put a white background
            daysOfMonthPane.setBackground(Background.fill(Color.WHITE));
            daysOfWeekPane.setPadding(new Insets(10, 0, 20, 0));
            daysOfMonthPane.setHgap(5);
            daysOfMonthPane.setVgap(5);
            container.getChildren().addAll(yearMonthSelectionHBox, daysOfWeekPane, daysOfMonthPane);
        }
        return container;
    }

    public boolean isDateSelectableDefault(LocalDate localDate) {
        if (!options.isPastDatesSelectionAllowed() && localDate.isBefore(LocalDate.now())) {
            return false;
        }
        return true;
    }

    public String getDateStyleClassDefault(LocalDate localDate) {
        if (!options.isPastDatesSelectionAllowed() && localDate.isBefore(LocalDate.now()))
            return "date-unselectable";
        return "date-selected";
    }

    private void onMonthChanged(YearMonth newMonth) {
        // If a property is at null, it means, it's the first call and the object haven't been initialized because the function calling here
        // happens before the initialisation of the objects, so we need to test first if the objects are initialized
        if (yearMonthCalendarLayout != null)
            yearMonthCalendarLayout.getChildren().setAll(newMonth);
        if (daysOfMonthLayout != null)
            daysOfMonthLayout.getChildren().setAll(TimeUtil.generateMonthDates(newMonth));
        if (arrowPreviousMonthPath != null)
            arrowPreviousMonthPath.setOnMouseClicked(event -> setDisplayedYearMonth(newMonth.minusMonths(1)));
        if (arrowNextMonthPath != null)
            arrowNextMonthPath.setOnMouseClicked(event -> setDisplayedYearMonth(newMonth.plusMonths(1)));
        if (displayedYearMonthLabel != null) {
            bindDisplayedYearMonthLabel();
            updateDatesStyles();
            updateUnselectableDateStyle();
        }
    }

    private void bindDisplayedYearMonthLabel() {
        displayedYearMonthLabel.textProperty().bind(TimeFormat.yearMonthNameProperty(getDisplayedYearMonth()));
    }

    private static void bindDayOfWeekLabel(Label label, DayOfWeek dayOfWeek) {
        // We display the first letter of the day of week name
        label.textProperty().bind(FXProperties.compute(TimeFormat.dayOfWeekNameProperty(dayOfWeek), dayName ->
            dayName != null && !dayName.isEmpty() ? dayName.substring(0, 1) : ""));
    }

    private Node createDateNode(LocalDate date) {
        Label currentDayLabel = new Label();
        GridPane.setHalignment(currentDayLabel, HPos.CENTER);
        int size = 20;
        currentDayLabel.setMaxSize(size, size);
        currentDayLabel.setMinSize(size, size);
        currentDayLabel.setPrefSize(size, size);
        currentDayLabel.setText(String.valueOf(date.getDayOfMonth()));
        currentDayLabel.setAlignment(Pos.CENTER);
        currentDayLabel.setBackground(Background.EMPTY);
        dateLabelSelectedStyleClassMap.put(date, new Pair<>(currentDayLabel, getDateStyleClassFunction.apply(date)));
        currentDayLabel.setOnMouseClicked(event -> onDateClicked(date));
        return currentDayLabel;
    }

    private void onDateClicked(LocalDate date) {
        boolean selectable = isDateSelectableFunction.apply(date);
        if (!selectable)
            return;
        if (this.selectedDates.contains(date)) {
            removeSelectedDate(date);
        } else {
            addSelectedDate(date);
        }
    }

    private void addSelectedDate(LocalDate date) {
        this.selectedDates.add(date);
    }

    private void removeSelectedDate(LocalDate date) {
        this.selectedDates.remove(date);
    }

    public void setIsDateSelectableFunction(Function<LocalDate, Boolean> isDateSelectableFunction) {
        this.isDateSelectableFunction = isDateSelectableFunction;
        updateUnselectableDateStyle();
    }

    public void setGetDateStyleClassFunction(Function<LocalDate, String> function) {
        getDateStyleClassFunction = function;
    }

    private void updateDatesStyles() {
        // We take the CalendarLayout Children that are a list of LocalDate element and the TimeGridPane children that
        // are a list of Label, and the i element, of the children of both list are corresponding
        List<LocalDate> localDateElements = daysOfMonthLayout.getChildren();
        LocalDate currentDate;
        for (LocalDate localDateElement : localDateElements) {
            currentDate = localDateElement;
            updateDateStyle(currentDate, selectedDates.contains(currentDate));
        }
    }

    private void updateDateStyle(LocalDate date, boolean isSelected) {
        Pair<Label, String> LabelCss = dateLabelSelectedStyleClassMap.get(date);
        Label label = LabelCss.get1();
        String css = LabelCss.get2();
        if (!isSelected&&isDateSelectableFunction.apply(date)) {
            label.getStyleClass().remove(css);
        } else if (!label.getStyleClass().contains(css)) {
            label.getStyleClass().add(css);
        }
    }

    private void updateUnselectableDateStyle() {
        // We take the CalendarLayout Children that are a list of LocalDate element and the TimeGridPane children that
        // are a list of Label, and the i element, of the children of both list are corresponding
        List<LocalDate> localDateElements = daysOfMonthLayout.getChildren();
        LocalDate currentDate;
        for (LocalDate localDateElement : localDateElements) {
            currentDate = localDateElement;
            if (!selectedDates.contains(currentDate) && !isDateSelectableFunction.apply(currentDate)) {
                Pair<Label, String> LabelCss = dateLabelSelectedStyleClassMap.get(currentDate);
                Label label = LabelCss.get1();
                label.getStyleClass().add("date-unselectable");
            }
        }
    }

    private Node createDayOfWeekNode(DayOfWeek dayOfWeek) {
        Label currentDayLabel = new Label();
        int size = 20;
        currentDayLabel.setMaxSize(size, size);
        currentDayLabel.setMinSize(size, size);
        currentDayLabel.setPrefSize(size, size);

        bindDayOfWeekLabel(currentDayLabel, dayOfWeek);
        currentDayLabel.setAlignment(Pos.CENTER);
        currentDayLabel.setBackground(Background.EMPTY);
        TextTheme.createSecondaryTextFacet(currentDayLabel).style();

        return currentDayLabel;
    }

}
