package dev.webfx.extras.validation;

import dev.webfx.extras.imagestore.ImageStore;
import dev.webfx.extras.util.background.BackgroundFactory;
import dev.webfx.extras.util.border.BorderFactory;
import dev.webfx.extras.util.scene.SceneUtil;
import dev.webfx.extras.validation.controlsfx.validation.ValidationMessage;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.platform.uischeduler.UiScheduler;
import dev.webfx.platform.util.Strings;
import dev.webfx.platform.util.collection.Collections;
import dev.webfx.extras.validation.controlsfx.control.decoration.Decoration;
import dev.webfx.extras.validation.controlsfx.control.decoration.GraphicDecoration;
import dev.webfx.extras.validation.controlsfx.validation.decoration.GraphicValidationDecoration;
import dev.webfx.extras.validation.mvvmfx.ObservableRuleBasedValidator;
import dev.webfx.extras.validation.mvvmfx.Validator;
import dev.webfx.extras.validation.mvvmfx.visualization.ControlsFxVisualizer;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author Bruno Salmon
 */
public final class ValidationSupport {

    private static final ObservableStringValue DEFAULT_REQUIRED_MESSAGE = new SimpleStringProperty("This field is required");

    private final List<Validator> validators = new ArrayList<>();
    private final List<Node> validatorErrorDecorationNodes = new ArrayList<>();
    private final BooleanProperty validatingProperty = new SimpleBooleanProperty();
    private Node popOverContentNode;
    private Node popOverOwnerNode;

    public boolean isValid() {
        validatingProperty.setValue(false);
        validatingProperty.setValue(true);
        Validator firstInvalidValidator = firstInvalidValidator();
        if (firstInvalidValidator != null)
            Platform.runLater(() -> {
                popUpOverAutoScroll = true;
                showValidatorErrorPopOver(firstInvalidValidator);
            });
        return firstInvalidValidator == null;
    }

    public void reset() {
        // This will hide the possible validation error popup and other warning icons
        validatingProperty.setValue(false);
    }

    public void clear() {
        reset(); // hiding possible validation messages
        validatorErrorDecorationNodes.forEach(this::uninstallNodeDecorator);
        validatorErrorDecorationNodes.clear();
        validators.forEach(validator -> {
            if (validator instanceof ObservableRuleBasedValidator) {
                ((ObservableRuleBasedValidator) validator).clear();
            }
        });
        validators.clear();
    }

    public boolean isEmpty() {
        return validators.isEmpty();
    }

    private Validator firstInvalidValidator() {
        return Collections.findFirst(validators, validator -> !validator.getValidationStatus().isValid());
    }

    public void addRequiredInputs(TextInputControl... textInputControls) {
        for (TextInputControl textInputControl : textInputControls)
            addRequiredInput(textInputControl);
    }

    public void addRequiredInput(TextInputControl textInputControl) {
        addRequiredInput(textInputControl, DEFAULT_REQUIRED_MESSAGE);
    }

    public void addRequiredInput(TextInputControl textInputControl, ObservableStringValue errorMessage) {
        addRequiredInput(textInputControl.textProperty(), textInputControl, errorMessage);
    }

    public void addRequiredInput(ObservableValue<?> valueProperty, Node inputNode) {
        addRequiredInput(valueProperty, inputNode, DEFAULT_REQUIRED_MESSAGE);
    }

    public void addRequiredInput(ObservableValue<?> valueProperty, Node inputNode, ObservableStringValue errorMessage) {
        addValidationRule(Bindings.createBooleanBinding(() -> testNotEmpty(valueProperty.getValue()), valueProperty), inputNode, errorMessage, true);
    }

    private static boolean testNotEmpty(Object value) {
        return value != null && (!(value instanceof String) || !((String) value).trim().isEmpty());
    }

    public ObservableBooleanValue addValidationRule(ObservableValue<Boolean> validProperty, Node node, ObservableStringValue errorMessage) {
        return addValidationRule(validProperty, node, errorMessage, false);
    }

    public ObservableBooleanValue addValidationRule(ObservableValue<Boolean> validProperty, Node node, ObservableStringValue errorMessageProperty, boolean required) {
        ObservableRuleBasedValidator validator = new ObservableRuleBasedValidator();
        ObservableBooleanValue finalValidProperty = // when true, no decorations are displayed on the node
                Bindings.createBooleanBinding(() ->
                    !validatingProperty.get() // no decoration displayed if not validation
                    || validProperty.getValue() // no decoration displayed if the node is valid
                    || !isShowing(node) // no decoration displayed if the node is not showing
                , validProperty, validatingProperty);
        dev.webfx.extras.validation.mvvmfx.ValidationMessage errorValidationMessage = dev.webfx.extras.validation.mvvmfx.ValidationMessage.error(errorMessageProperty);
        validator.addRule(finalValidProperty, errorValidationMessage);
        validators.add(validator);
        validatorErrorDecorationNodes.add(node);
        installNodeDecorator(node, validator, required);
        // The following code is to remove the error message after being displayed and the user is re-typing
        if (node instanceof TextInputControl) {
            FXProperties.runOnPropertiesChange(
                () -> {
                    validator.validateBooleanRule(true, errorValidationMessage);
                    uninstallNodeDecorator(node);
                }
            , ((TextInputControl) node).textProperty()); // ex of dependencies: textField.textProperty()
        }
        return finalValidProperty;
    }

    private void installNodeDecorator(Node node, ObservableRuleBasedValidator validator, boolean required) {
        if (node instanceof Control) {
            Control control = (Control) node;
            ControlsFxVisualizer validationVisualizer = new ControlsFxVisualizer();
            validationVisualizer.setDecoration(new GraphicValidationDecoration() {
                @Override
                protected Node createErrorNode() {
                    return ImageStore.createImageView(ValidationIcons.validationErrorIcon16Url);
                }

                @Override
                protected Collection<Decoration> createValidationDecorations(ValidationMessage message) {
                    boolean isTextInput = node instanceof TextInputControl;
                    boolean isButton = node instanceof Button;
                    // isInside flag will determine if we position the decoration inside the node or not (ie outside)
                    boolean isInside;
                    if (isTextInput) // inside for text inputs
                        isInside = true;
                    else { // for others, will be generally outside unless it is stretched to full width by its container
                        Parent parent = node.getParent();
                        while (parent instanceof Pane && !(parent instanceof VBox) && !(parent instanceof HBox))
                            parent = parent.getParent();
                        isInside = parent instanceof VBox && ((VBox) parent).isFillWidth();
                    }
                    double xRelativeOffset = isInside ? -1 : 1; // positioning the decoration inside the control for button and text input
                    double xOffset = isInside && isButton ?  -20 : 0; // moving the decoration before the drop down arrow
                    return java.util.Collections.singletonList(
                        new GraphicDecoration(createDecorationNode(message),
                            Pos.CENTER_RIGHT,
                            xOffset,
                            0,
                            xRelativeOffset,
                            0)
                    );
                }

                @Override
                protected Collection<Decoration> createRequiredDecorations(Control target) {
                    return java.util.Collections.singletonList(
                        new GraphicDecoration(ImageStore.createImageView(ValidationIcons.validationRequiredIcon16Url),
                            Pos.CENTER_LEFT,
                            -10,
                            0));
                }
            });
            validationVisualizer.initVisualization(validator.getValidationStatus(), control, required);
            node.getProperties().put("validationVisualizer", validationVisualizer);
        }
    }

    private void uninstallNodeDecorator(Node node) {
        if (node instanceof Control) {
            Control control = (Control) node;
            ControlsFxVisualizer validationVisualizer = (ControlsFxVisualizer) node.getProperties().get("validationVisualizer");
            if (validationVisualizer != null)
                validationVisualizer.removeDecorations(control);
        }
    }

    private void showValidatorErrorPopOver(Validator validator) {
        int index = validators.indexOf(validator);
        if (index >= 0) {
            Node decorationNode = validatorErrorDecorationNodes.get(index);
            if (decorationNode != null)
                showValidatorErrorPopOver(validator, decorationNode);
        }
    }

    private void showValidatorErrorPopOver(Validator validator, Node errorDecorationNode) {
        dev.webfx.extras.validation.mvvmfx.ValidationMessage errorMessage = Collections.first(validator.getValidationStatus().getErrorMessages());
        if (errorMessage != null) {
            Label label = new Label();
            label.textProperty().bind(errorMessage.messageProperty());
            label.setPadding(new Insets(8));
            label.setFont(Font.font("Verdana", 11.5));
            label.setTextFill(Color.WHITE);
            label.setBackground(BackgroundFactory.newBackground(Color.RED, 5, 2));
            label.setBorder(BorderFactory.newBorder(Color.WHITE, 5, 2));
            Rectangle diamond = new Rectangle(10, 10, Color.RED);
            diamond.getTransforms().add(new Rotate(45, 5, 5));
            diamond.layoutYProperty().bind(label.heightProperty().map(n -> n.doubleValue() - 7));
            diamond.setLayoutX(20d);
            popOverContentNode = new Group(label, diamond);
            //popOverContentNode.setOpacity(0.75);
            //popOverContentNode.setEffect(new DropShadow());
            showPopOver(errorDecorationNode);
            // Removing the error pop over when the status is valid again
            FXProperties.runOrUnregisterOnPropertyChange((thisListener, oldValue, valid) -> {
                if (valid) {
                    thisListener.unregister();
                    popOverOwnerNode = null;
                    hidePopOver();
                }
            }, validator.getValidationStatus().validProperty());
        }
    }

    private void showPopOver(Node node) {
        popOverOwnerNode = node;
        showPopOverNow();
        if (!node.getProperties().containsKey("popOverListen")) {
            node.getProperties().put("popOverListen", true);
            node.sceneProperty().addListener(observable -> {
                if (popOverOwnerNode == node) {
                    showPopOverNow();
                }
            });
            node.parentProperty().addListener(observable -> {
                if (popOverOwnerNode == node) {
                    showPopOverNow();
                }
            });
        }
    }

    public void addEmailValidation(TextField emailInput, Node where, ObservableStringValue errorMessage) {
        // Define the email pattern
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(emailPattern);
        // Create the validation rule
        addValidationRule(
                Bindings.createBooleanBinding(
                        () -> pattern.matcher(emailInput.getText()).matches(),
                        emailInput.textProperty()
                ),
                where,
                errorMessage
        );
    }

    public void addEmailNotEqualValidation(TextField emailInput, String forbiddenValue, Node where, ObservableStringValue errorMessage) {
        // Create the validation rule
        addValidationRule(
            Bindings.createBooleanBinding(
                () -> !emailInput.getText().equalsIgnoreCase(forbiddenValue),
                emailInput.textProperty()
            ),
            where,
            errorMessage
        );
    }

    public void addUrlValidation(TextField urlInput, Node where, ObservableStringValue errorMessage) {
        // Regex to match either:
        // 1. A standard HTTP(S) URL, or
        // 2. A custom bunny: URL with videoId and zoneId required
        String urlPattern =
            "^(https?://[^\\s]+)$" +                                 // Match normal URLs
                "|^(bunny:(?=.*\\bvideoId=[^&\\s]+)(?=.*\\bzoneId=[^&\\s]+)(.*))$";  // Match bunny: URLs with required params

        Pattern pattern = Pattern.compile(urlPattern);

        addValidationRule(
            Bindings.createBooleanBinding(
                () -> {
                    String input = urlInput.getText();
                    return input != null && pattern.matcher(input).matches();
                },
                urlInput.textProperty()
            ),
            where,
            errorMessage
        );
    }

    public void addMinimumDurationValidation(TextField timeInput, Node where, ObservableStringValue errorMessage) {
        addValidationRule(
            Bindings.createBooleanBinding(
                () -> {
                    try {
                        String input = timeInput.getText();
                        if (input == null || input.isEmpty()) return true; // Allow empty input
                        int value = Integer.parseInt(input);  // Try to parse the input to an integer
                        return value >= 60;  // Validate if the integer is at least 60
                    } catch (NumberFormatException e) {
                        return false;  // Invalid if input is not a valid integer
                    }
                },
                timeInput.textProperty()
            ),
            where,
            errorMessage
        );
    }

    public void  addMinimumDurationValidationIfOtherTextFieldNotNull (TextField timeInput, TextField linkedTextField, Node where, ObservableStringValue errorMessage) {
        {
            addValidationRule(
                Bindings.createBooleanBinding(
                    () -> {
                        try {
                            String input = timeInput.getText();
                            if (linkedTextField==null || Objects.equals(linkedTextField.getText(), "") || input == null || input.isEmpty()) return true; // Allow empty input
                            int value = Integer.parseInt(input);  // Try to parse the input to an integer
                            return value >= 60;  // Validate if the integer is at least 60
                        } catch (NumberFormatException e) {
                            return false;  // Invalid if input is not a valid integer
                        }
                    },
                    timeInput.textProperty()
                ),
                where,
                errorMessage
            );
        }
    }

    public void addUrlOrEmptyValidation(TextField urlInput, ObservableStringValue errorMessage) {
        // Looser but practical pattern for URLs including non-ASCII and punctuation
        String urlPattern = "^(https?|srt|rtmp|rtsp)://[^\\s]+$";
        Pattern pattern = Pattern.compile(urlPattern);//Not emulated for now: , Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

        addValidationRule(
            Bindings.createBooleanBinding(
                () -> {
                    String input = urlInput.getText();
                    return input == null || input.isEmpty() || pattern.matcher(input).matches();
                },
                urlInput.textProperty()
            ),
            urlInput,
            errorMessage
        );
    }



    public void addNonEmptyValidation(TextField textField, Node where, ObservableStringValue errorMessage) {
        // Create the validation rule
        addValidationRule(
                Bindings.createBooleanBinding(
                        () -> !textField.getText().trim().isEmpty(),
                        textField.textProperty()
                ),
                where,
                errorMessage);
    }

    public void addDateValidation(TextField textField, DateTimeFormatter dateFormatter, Node where, ObservableStringValue errorMessage) {
        // Create the validation rule
        addValidationRule(
                Bindings.createBooleanBinding(() -> {
                    try {
                        dateFormatter.parse(textField.getText().trim());
                        return true;
                    } catch (DateTimeParseException e) {
                        return false;
                    }}, textField.textProperty()),
                where,
                errorMessage
        );
    }

    public void addDateOrEmptyValidation(TextField textField, DateTimeFormatter dateFormatter, Node where, ObservableStringValue errorMessage) {
        // Create the validation rule
        addValidationRule(
            Bindings.createBooleanBinding(() -> {
                String input = Strings.toSafeString(textField.getText()).trim();
                // Allow empty input to be valid
                if (input.isEmpty()) {
                    return true;
                }
                try {
                    dateFormatter.parse(input);
                    return true;  // Input is valid if it can be parsed
                } catch (DateTimeParseException e) {
                    return false; // Invalid date format
                }
            }, textField.textProperty()),
            where,
            errorMessage
        );
    }

    public void addIntegerValidation(TextField textField, Node where, ObservableStringValue errorMessage) {
        // Create the validation rule
        addValidationRule(
            Bindings.createBooleanBinding(() -> {
                try {
                    // Try to parse the text as an integer
                    Integer.parseInt(textField.getText().trim());
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }, textField.textProperty()),
            where,
            errorMessage
        );
    }

    public void addLegalAgeValidation(TextField textField, DateTimeFormatter dateFormatter, int legalAge, Node where, ObservableStringValue errorMessage) {
        // Create the validation rule
        addValidationRule(
                Bindings.createBooleanBinding(() -> {
                    try {
                        LocalDate birthDate = LocalDate.parse(textField.getText().trim(), dateFormatter);
                        LocalDate now = LocalDate.now();
                        return birthDate.plusYears(legalAge).isBefore(now) || birthDate.plusYears(legalAge).isEqual(now);
                    } catch (DateTimeParseException e) {
                        return false;
                    }
                }, textField.textProperty()),
                where,
                errorMessage
        );
    }

    public void addPasswordMatchValidation(TextField passwordField, TextField repeatPasswordField, ObservableStringValue errorMessageProperty) {
        addValidationRule(
            Bindings.createBooleanBinding(
                () -> passwordField.getText().equals(repeatPasswordField.getText()),
                passwordField.textProperty(),
                repeatPasswordField.textProperty()
            ),
            repeatPasswordField,
            errorMessageProperty,
            true
        );
    }

    public void addPasswordStrengthValidation(TextField passwordField, ObservableStringValue errorMessage) {
        addValidationRule(
            Bindings.createBooleanBinding(
                () -> checkPasswordStrength(passwordField.getText()),
                passwordField.textProperty()
            ),
            passwordField,
            errorMessage,
            true
        );
    }
    /**
     * Checks if a password meets strength requirements.
     * @param password the password to validate.
     * @return true if the password meets the requirements, false otherwise.
     */
    private boolean checkPasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        // Minimum length
        if (password.length() < 8) {
            return false;
        }
        // Contains at least one uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }
        // Contains at least one lowercase letter
        if (!password.matches(".*[a-z].*")) {
            return false;
        }
        // Contains at least one digit
        if (!password.matches(".*\\d.*")) {
            return false;
        }
        // Contains at least one special character
        if (!password.matches(".*[@#$%^&+=!().,*?-].*")) {
            return false;
        }
        return true;
    }

/*
    private PopOver popOver;
    private void showPopOverNow() {
        if (popOver != null && popOver.getOwnerNode() != popOverOwnerNode) {
            popOver.hide();
            popOver = null;
        }
        if (popOver == null && isShowing(popOverOwnerNode)) {
            popOver = new PopOver();
            popOver.setContentNode(popOverContentNode);
            popOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_LEFT);
            //Platform.runLater(() -> {
                popOver.show(popOverOwnerNode, -(popOverOwnerNode instanceof ImageView ? ((ImageView) popOverOwnerNode).getImage().getHeight() : 0) + 4);
            //});
        }
    }
*/

    private GraphicDecoration popOverDecoration;
    private Node popOverDecorationTarget;
    private boolean popUpOverAutoScroll;

    private void showPopOverNow() {
        Platform.runLater(() -> {
            hidePopOver();
            if (isShowing(popOverOwnerNode)) {
                popOverDecorationTarget = popOverOwnerNode;
                popOverDecoration = new GraphicDecoration(popOverContentNode, 0, -1, 0, -1);
                popOverDecoration.applyDecoration(popOverDecorationTarget);
                if (popUpOverAutoScroll) {
                    SceneUtil.scrollNodeToBeVerticallyVisibleOnScene(popOverDecorationTarget);
                    SceneUtil.autoFocusIfEnabled(popOverOwnerNode);
                    popUpOverAutoScroll = false;
                }
            }
        });
    }

    private void hidePopOver() {
        UiScheduler.runInUiThread(() -> {
            if (popOverDecoration != null) {
                popOverDecoration.removeDecoration(popOverDecorationTarget);
                popOverDecoration = null;
            }
        });
    }

    private static boolean isShowing(Node node) {
        if (node == null || !node.isVisible())
            return false;
        Parent parent = node.getParent();
        if (parent != null)
            return isShowing(parent);
        Scene scene = node.getScene();
        return scene != null && scene.getRoot() == node;
    }

    public void addPasswordValidation(TextField passwordInput, Label passwordLabel, ObservableStringValue errorMessage) {
        addValidationRule(
                Bindings.createBooleanBinding(
                        () -> passwordInput.getText().length() >= 8,
                        passwordInput.textProperty()
                ),
                passwordLabel,
                errorMessage
        );
    }

    public void addRequiredInputIfOtherTextFieldNotNull(TextField requiredTextField, TextField linkedTextField, TextField node) {
        addValidationRule(
            Bindings.createBooleanBinding(
                () -> {
                    // If linkedTextField is null or its text is null or empty, skip validation
                    if (linkedTextField == null || linkedTextField.getText() == null || linkedTextField.getText().isEmpty()) {
                        return true;
                    }

                    // If linkedTextField has text, requiredTextField must also have text
                    return requiredTextField != null
                        && requiredTextField.getText() != null
                        && !requiredTextField.getText().isEmpty();
                },
                linkedTextField.textProperty(), requiredTextField.textProperty()
            ),
            node,
            DEFAULT_REQUIRED_MESSAGE
        );
    }
}
