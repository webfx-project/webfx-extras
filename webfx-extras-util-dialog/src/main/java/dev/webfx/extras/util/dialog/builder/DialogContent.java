package dev.webfx.extras.util.dialog.builder;

import dev.webfx.extras.styles.bootstrap.Bootstrap;
import dev.webfx.extras.util.dialog.DialogCallback;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

/**
 * DialogContent provides a comprehensive system for creating various types of dialogs
 * following UX best practices. Supports Info, Success, Warning, Error, and Delete dialogs
 * with customizable content and styling.
 *
 * @author Bruno Salmon
 */
public final class DialogContent implements DialogBuilder {

    public enum ButtonStyle {
        SUCCESS,   // Green button
        PRIMARY,   // Blue button (information)
        WARNING,   // Orange button (confirmation danger)
        DANGER,    // Red button (error)
        SECONDARY  // Gray button (neutral)
    }

    private String title;
    private String headerText;
    private String contentText;
    private String primaryButtonText;
    private String secondaryButtonText;

    private Node content;
    private Node headerIcon;
    private Node infoBox;  // Highlighted info box
    private Button primaryButton;
    private Button secondaryButton;
    private ButtonStyle primaryButtonStyle = ButtonStyle.PRIMARY;
    private ButtonStyle secondaryButtonStyle = ButtonStyle.SECONDARY;

    // Technical error details
    private String errorCode;
    private String timestamp;
    private String technicalDetails;

    private DialogCallback dialogCallback;

    // ========== Factory Methods for Different Dialog Types ==========

    /**
     * Creates a simple information dialog with only a message.
     */
    public static DialogContent createInformationDialog(String title, String headerText, String contentText) {
        return new DialogContent()
                .setTitle(title)
                .setHeaderText(headerText)
                .setContentText(contentText)
                .setHeaderIcon(createInfoIcon())
                .setPrimaryButtonStyle(ButtonStyle.PRIMARY)
                .setOk();
    }

    /**
     * Creates an information dialog with a highlighted info box.
     */
    public static DialogContent createInformationDialogWithBox(String title, String headerText, String contentText, String boxContent) {
        return new DialogContent()
                .setTitle(title)
                .setHeaderText(headerText)
                .setContentText(contentText)
                .setHeaderIcon(createInfoIcon())
                .setInfoBox(createAlertInfoBox(boxContent))
                .setPrimaryButtonStyle(ButtonStyle.PRIMARY)
                .setOk();
    }

    /**
     * Creates an information dialog with two action buttons.
     * Use this when the user needs to choose between two actions (e.g., Accept/Decline).
     */
    public static DialogContent createInformationDialogWithTwoActions(String title, String headerText, String contentText, String boxContent) {
        return new DialogContent()
                .setTitle(title)
                .setHeaderText(headerText)
                .setContentText(contentText)
                .setHeaderIcon(createInfoIcon())
                .setInfoBox(createAlertInfoBox(boxContent))
                .setPrimaryButtonStyle(ButtonStyle.PRIMARY)
                .setSecondaryButtonStyle(ButtonStyle.SECONDARY)
                .setYesNo();  // Default to Yes/No, but can be customized by caller
    }

    /**
     * Creates a simple success dialog.
     */
    public static DialogContent createSuccessDialog(String title, String headerText, String contentText) {
        return new DialogContent()
                .setTitle(title)
                .setHeaderText(headerText)
                .setContentText(contentText)
                .setHeaderIcon(createSuccessIcon())
                .setPrimaryButtonStyle(ButtonStyle.SUCCESS)
                .setOk();
    }

    /**
     * Creates a success dialog with a details box.
     */
    public static DialogContent createSuccessDialogWithBox(String title, String headerText, String contentText, String boxContent) {
        return new DialogContent()
                .setTitle(title)
                .setHeaderText(headerText)
                .setContentText(contentText)
                .setHeaderIcon(createSuccessIcon())
                .setInfoBox(createAlertSuccessBox(boxContent))
                .setPrimaryButtonStyle(ButtonStyle.SUCCESS)
                .setOk();
    }

    /**
     * Creates a simple warning/confirmation dialog.
     */
    public static DialogContent createWarningDialog(String title, String headerText, String contentText) {
        return new DialogContent()
                .setTitle(title)
                .setHeaderText(headerText)
                .setContentText(contentText)
                .setHeaderIcon(createWarningIcon())
                .setPrimaryButtonStyle(ButtonStyle.WARNING)
                .setYesNo();
    }

    /**
     * Creates a warning dialog with a highlighted warning box.
     */
    public static DialogContent createWarningDialogWithBox(String title, String headerText, String contentText, String boxContent) {
        return new DialogContent()
                .setTitle(title)
                .setHeaderText(headerText)
                .setContentText(contentText)
                .setHeaderIcon(createWarningIcon())
                .setInfoBox(createAlertWarningBox(boxContent))
                .setPrimaryButtonStyle(ButtonStyle.WARNING)
                .setYesNo();
    }

    /**
     * Creates a simple error dialog without technical details.
     */
    public static DialogContent createErrorDialog(String title, String headerText, String contentText) {
        return new DialogContent()
                .setTitle(title)
                .setHeaderText(headerText)
                .setContentText(contentText)
                .setHeaderIcon(createErrorIcon())
                .setPrimaryButtonStyle(ButtonStyle.DANGER)
                .setOk();
    }

    /**
     * Creates an error dialog with technical details (error code, timestamp, stack trace).
     */
    public static DialogContent createErrorDialogWithTechnicalDetails(String title, String headerText, String contentText,
                                                                       String technicalDetails, String errorCode, String timestamp) {
        return new DialogContent()
                .setTitle(title)
                .setHeaderText(headerText)
                .setContentText(contentText)
                .setHeaderIcon(createErrorIcon())
                .setTechnicalDetails(technicalDetails)
                .setErrorCode(errorCode)
                .setTimestamp(timestamp)
                .setPrimaryButtonStyle(ButtonStyle.DANGER)
                .setOk();
    }

    /**
     * Creates a delete confirmation dialog for destructive actions.
     */
    public static DialogContent createDeleteDialog(String title, String headerText, String contentText) {
        return new DialogContent()
                .setTitle(title)
                .setHeaderText(headerText)
                .setContentText(contentText)
                .setHeaderIcon(createDeleteIcon())
                .setPrimaryButtonStyle(ButtonStyle.DANGER)
                .setYesNo();
    }

    /**
     * Creates a delete dialog with additional warning information.
     */
    public static DialogContent createDeleteDialogWithWarning(String title, String headerText, String contentText, String warningContent) {
        return new DialogContent()
                .setTitle(title)
                .setHeaderText(headerText)
                .setContentText(contentText)
                .setHeaderIcon(createDeleteIcon())
                .setInfoBox(createAlertWarningBox(warningContent))
                .setPrimaryButtonStyle(ButtonStyle.DANGER)
                .setYesNo();
    }

    // ========== Legacy Factory Methods (for backward compatibility) ==========

    public static DialogContent createConfirmationDialog(String headerText, String contentText) {
        return createConfirmationDialog("Confirmation", headerText, contentText);
    }

    public static DialogContent createConfirmationDialog(String title, String headerText, String contentText) {
        return new DialogContent().setTitle(title).setHeaderText(headerText).setContentText(contentText).setYesNo();
    }

    public static DialogContent createConfirmationDangerDialog(String headerText, String contentText) {
        return createConfirmationDangerDialog("Confirmation", headerText, contentText);
    }

    public static DialogContent createConfirmationDangerDialog(String title, String headerText, String contentText) {
        return new DialogContent()
                .setTitle(title)
                .setHeaderText(headerText)
                .setContentText(contentText)
                .setHeaderIcon(createWarningIcon())
                .setPrimaryButtonStyle(ButtonStyle.WARNING)
                .setYesNo();
    }

    // ========== Helper Methods to Create Icons ==========

    private static Node createInfoIcon() {
        SVGPath svgPath = new SVGPath();
        svgPath.setContent("M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z");
        svgPath.setFill(Color.web("#00a6e0"));
        svgPath.setScaleX(2);
        svgPath.setScaleY(2);
        return svgPath;
    }

    private static Node createSuccessIcon() {
        SVGPath svgPath = new SVGPath();
        svgPath.setContent("M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z");
        svgPath.setFill(Color.web("#28a745"));
        svgPath.setScaleX(2);
        svgPath.setScaleY(2);
        return svgPath;
    }

    private static Node createWarningIcon() {
        SVGPath svgPath = new SVGPath();
        svgPath.setContent("M32.427,7.987c2.183,0.124 4,1.165 5.096,3.281l17.936,36.208c1.739,3.66 -0.954,8.585 -5.373,8.656l-36.119,0c-4.022,-0.064 -7.322,-4.631 -5.352,-8.696l18.271,-36.207c0.342,-0.65 0.498,-0.838 0.793,-1.179c1.186,-1.375 2.483,-2.111 4.748,-2.063Zm-0.295,3.997c-0.687,0.034 -1.316,0.419 -1.659,1.017c-6.312,11.979 -12.397,24.081 -18.301,36.267c-0.546,1.225 0.391,2.797 1.762,2.863c12.06,0.195 24.125,0.195 36.185,0c1.325,-0.064 2.321,-1.584 1.769,-2.85c-5.793,-12.184 -11.765,-24.286 -17.966,-36.267c-0.366,-0.651 -0.903,-1.042 -1.79,-1.03Z M33.631,40.581l-3.348,0l-0.368,-16.449l4.1,0l-0.384,16.449Zm-3.828,5.03c0,-0.609 0.197,-1.113 0.592,-1.514c0.396,-0.4 0.935,-0.601 1.618,-0.601c0.684,0 1.223,0.201 1.618,0.601c0.395,0.401 0.593,0.905 0.593,1.514c0,0.587 -0.193,1.078 -0.577,1.473c-0.385,0.395 -0.929,0.593 -1.634,0.593c-0.705,0 -1.249,-0.198 -1.634,-0.593c-0.384,-0.395 -0.576,-0.886 -0.576,-1.473Z");
        svgPath.setFill(Color.web("#ff9800"));
        svgPath.setScaleX(0.5);
        svgPath.setScaleY(0.5);
        return svgPath;
    }

    private static Node createErrorIcon() {
        SVGPath svgPath = new SVGPath();
        svgPath.setContent("M12 2C6.47 2 2 6.47 2 12s4.47 10 10 10 10-4.47 10-10S17.53 2 12 2zm5 13.59L15.59 17 12 13.41 8.41 17 7 15.59 10.59 12 7 8.41 8.41 7 12 10.59 15.59 7 17 8.41 13.41 12 17 15.59z");
        svgPath.setFill(Color.web("#dc3545"));
        svgPath.setScaleX(2);
        svgPath.setScaleY(2);
        return svgPath;
    }

    private static Node createDeleteIcon() {
        SVGPath svgPath = new SVGPath();
        svgPath.setContent("M 6.3952 2.7317 H 9.4429 C 9.4429 2.3177 9.2823 1.9207 8.9965 1.6279 C 8.7107 1.3352 8.3232 1.1707 7.919 1.1707 C 7.5149 1.1707 7.1273 1.3352 6.8416 1.6279 C 6.5558 1.9207 6.3952 2.3177 6.3952 2.7317 Z M 5.2524 2.7317 C 5.2524 2.373 5.3214 2.0178 5.4554 1.6863 C 5.5894 1.3549 5.7858 1.0538 6.0334 0.8001 C 6.2811 0.5464 6.575 0.3452 6.8986 0.2079 C 7.2221 0.0707 7.5689 0 7.919 0 C 8.2692 0 8.616 0.0707 8.9395 0.2079 C 9.263 0.3452 9.5571 0.5464 9.8047 0.8001 C 10.0523 1.0538 10.2487 1.3549 10.3827 1.6863 C 10.5168 2.0178 10.5857 2.373 10.5857 2.7317 H 14.9667 C 15.1182 2.7317 15.2636 2.7934 15.3707 2.9032 C 15.4779 3.0129 15.5381 3.1618 15.5381 3.3171 C 15.5381 3.4723 15.4779 3.6212 15.3707 3.731 C 15.2636 3.8408 15.1182 3.9024 14.9667 3.9024 H 13.961 L 13.0695 13.3549 C 13.0011 14.0792 12.6718 14.7514 12.1459 15.2405 C 11.6198 15.7295 10.9349 16.0003 10.2246 16 H 5.6135 C 4.9033 16.0001 4.2186 15.7292 3.6927 15.2402 C 3.1669 14.7512 2.8377 14.0791 2.7693 13.3549 L 1.8771 3.9024 H 0.8714 C 0.7199 3.9024 0.5745 3.8408 0.4674 3.731 C 0.3602 3.6212 0.3 3.4723 0.3 3.3171 C 0.3 3.1618 0.3602 3.0129 0.4674 2.9032 C 0.5745 2.7934 0.7199 2.7317 0.8714 2.7317 H 5.2524 Z M 6.7762 6.439 C 6.7762 6.2838 6.716 6.1349 6.6088 6.0251 C 6.5017 5.9153 6.3563 5.8537 6.2048 5.8537 C 6.0532 5.8537 5.9079 5.9153 5.8007 6.0251 C 5.6935 6.1349 5.6333 6.2838 5.6333 6.439 V 12.2926 C 5.6333 12.4479 5.6935 12.5968 5.8007 12.7066 C 5.9079 12.8164 6.0532 12.878 6.2048 12.878 C 6.3563 12.878 6.5017 12.8164 6.6088 12.7066 C 6.716 12.5968 6.7762 12.4479 6.7762 12.2926 V 6.439 Z M 9.6333 5.8537 C 9.4818 5.8537 9.3364 5.9153 9.2293 6.0251 C 9.1221 6.1349 9.0619 6.2838 9.0619 6.439 V 12.2926 C 9.0619 12.4479 9.1221 12.5968 9.2293 12.7066 C 9.3364 12.8164 9.4818 12.878 9.6333 12.878 C 9.7849 12.878 9.9302 12.8164 10.0374 12.7066 C 10.1446 12.5968 10.2048 12.4479 10.2048 12.2926 V 6.439 C 10.2048 6.2838 10.1446 6.1349 10.0374 6.0251 C 9.9302 5.9153 9.7849 5.8537 9.6333 5.8537 Z");
        svgPath.setFill(Color.web("#dc3545"));
        svgPath.setScaleX(1.2);
        svgPath.setScaleY(1.2);
        return svgPath;
    }

    // ========== Helper Methods to Create Colored Boxes ==========

    /**
     * Creates an info box (blue background) using Bootstrap alert styles.
     */
    private static Node createAlertInfoBox(String content) {
        Label label = new Label(content);
        label.setWrapText(true);
        VBox box = new VBox(label);
        box.setPadding(new Insets(16));
        Bootstrap.alertInfo(box);
        return box;
    }

    /**
     * Creates a success box (green background) using Bootstrap alert styles.
     */
    private static Node createAlertSuccessBox(String content) {
        Label label = new Label(content);
        label.setWrapText(true);
        VBox box = new VBox(label);
        box.setPadding(new Insets(16));
        Bootstrap.alertSuccess(box);
        return box;
    }

    /**
     * Creates a warning box (yellow/orange background) using Bootstrap alert styles.
     */
    private static Node createAlertWarningBox(String content) {
        Label label = new Label(content);
        label.setWrapText(true);
        VBox box = new VBox(label);
        box.setPadding(new Insets(16));
        Bootstrap.alertWarning(box);
        return box;
    }

    /**
     * Creates an error box (red background) using Bootstrap alert styles.
     */
    private static Node createAlertDangerBox(String content) {
        Label label = new Label(content);
        label.setWrapText(true);
        VBox box = new VBox(label);
        box.setPadding(new Insets(16));
        Bootstrap.alertDanger(box);
        return box;
    }

    // ========== Constructor and Basic Setters ==========

    public DialogContent() {
        setPrimaryButton(new Button());
        setSecondaryButton(new Button());
        setOkCancel();
    }

    @Override
    public void setDialogCallback(DialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    @Override
    public DialogCallback getDialogCallback() {
        return dialogCallback;
    }

    public DialogContent setTitle(String title) {
        this.title = title;
        return this;
    }

    public DialogContent setHeaderText(String headerText) {
        this.headerText = headerText;
        return this;
    }

    public DialogContent setContentText(String contentText) {
        this.contentText = contentText;
        return this;
    }

    public DialogContent setContent(Node content) {
        this.content = content;
        return this;
    }

    public DialogContent setHeaderIcon(Node headerIcon) {
        this.headerIcon = headerIcon;
        return this;
    }

    public DialogContent setInfoBox(Node infoBox) {
        this.infoBox = infoBox;
        return this;
    }

    public DialogContent setTechnicalDetails(String technicalDetails) {
        this.technicalDetails = technicalDetails;
        return this;
    }

    public DialogContent setErrorCode(String errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public DialogContent setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public DialogContent setPrimaryButtonStyle(ButtonStyle style) {
        this.primaryButtonStyle = style;
        if (primaryButton != null) {
            applyButtonStyle(primaryButton, style);
        }
        return this;
    }

    public DialogContent setSecondaryButtonStyle(ButtonStyle style) {
        this.secondaryButtonStyle = style;
        if (secondaryButton != null) {
            applyButtonStyle(secondaryButton, style);
        }
        return this;
    }

    private void applyButtonStyle(Button button, ButtonStyle style) {
        switch (style) {
            case SUCCESS:
                Bootstrap.largeSuccessButton(button);
                break;
            case PRIMARY:
                Bootstrap.largePrimaryButton(button);
                break;
            case WARNING:
                Bootstrap.largeButton(button, Bootstrap.WARNING);
                break;
            case DANGER:
                Bootstrap.largeDangerButton(button);
                break;
            case SECONDARY:
                Bootstrap.largeSecondaryButton(button);
                break;
        }
    }

    // Button text setters
    public DialogContent setYesNo() {
        primaryButtonText = "Yes";
        secondaryButtonText = "No";
        return this;
    }

    public DialogContent setSaveCancel() {
        primaryButtonText = "Save";
        secondaryButtonText = "Cancel";
        return this;
    }

    public DialogContent setOk() {
        primaryButtonText = "Ok";
        secondaryButton.setManaged(false);
        return this;
    }

    public DialogContent setOkCancel() {
        primaryButtonText = "Ok";
        secondaryButtonText = "Cancel";
        return this;
    }

    /**
     * Sets custom text for both primary and secondary buttons.
     * Useful for dialogs that need specific action labels (e.g., "Accept Invitation" / "Decline").
     */
    public DialogContent setCustomButtons(String primaryText, String secondaryText) {
        primaryButtonText = primaryText;
        secondaryButtonText = secondaryText;
        secondaryButton.setManaged(true);  // Ensure secondary button is visible
        return this;
    }

    /**
     * Sets custom text for the primary button only.
     */
    public DialogContent setPrimaryButtonText(String text) {
        primaryButtonText = text;
        return this;
    }

    /**
     * Sets custom text for the secondary button only.
     */
    public DialogContent setSecondaryButtonText(String text) {
        secondaryButtonText = text;
        secondaryButton.setManaged(true);  // Ensure secondary button is visible
        return this;
    }

    public Button getPrimaryButton() {
        return primaryButton;
    }

    public DialogContent setPrimaryButton(Button primaryButton) {
        this.primaryButton = primaryButton;
        applyButtonStyle(primaryButton, primaryButtonStyle);
        primaryButton.setDefaultButton(true);
        return this;
    }

    public Button getSecondaryButton() {
        return secondaryButton;
    }

    public DialogContent setSecondaryButton(Button secondaryButton) {
        this.secondaryButton = secondaryButton;
        applyButtonStyle(secondaryButton, secondaryButtonStyle);
        secondaryButton.setCancelButton(true);
        return this;
    }

    @Override
    public Region build() {
        GridPaneBuilder builder = new GridPaneBuilder();

        // Title
        if (title != null) {
            builder.addTextRow(title);
        }

        // Header with optional icon
        if (headerText != null) {
            Label headerLabel = Bootstrap.h3(newLabel(headerText));
            headerLabel.setWrapText(true);

            if (headerIcon != null) {
                HBox headerBox = new HBox(15);
                headerBox.setAlignment(Pos.CENTER);
                headerBox.getChildren().addAll(headerIcon, headerLabel);
                GridPane.setHalignment(headerBox, HPos.CENTER);
                GridPane.setMargin(headerBox, new Insets(10));
                builder.addNodeFillingRow(headerBox);
            } else {
                GridPane.setHalignment(headerLabel, HPos.CENTER);
                GridPane.setMargin(headerLabel, new Insets(10));
                builder.addNodeFillingRow(headerLabel);
            }
        }

        // Main content text
        if (contentText != null) {
            Label contentLabel = Bootstrap.h4(newLabel(contentText));
            contentLabel.setWrapText(true);
            GridPane.setMargin(contentLabel, new Insets(20));
            builder.addNodeFillingRow(contentLabel);
        }

        // Info box (if present)
        if (infoBox != null) {
            GridPane.setMargin(infoBox, new Insets(12, 20, 12, 20));
            builder.addNodeFillingRow(infoBox);
        }

        // Technical error details (if present)
        if (technicalDetails != null || errorCode != null || timestamp != null) {
            VBox errorBox = createTechnicalErrorBox();
            GridPane.setMargin(errorBox, new Insets(12, 20, 12, 20));
            builder.addNodeFillingRow(errorBox);
        }

        // Custom content
        if (content != null) {
            builder.addNodeFillingRow(content);
            GridPane.setVgrow(content, Priority.ALWAYS);
        }

        // Buttons
        return builder
                .addButtons(primaryButtonText, primaryButton, secondaryButtonText, secondaryButton)
                .build();
    }

    /**
     * Creates a technical error box with error code, timestamp, and stack trace.
     */
    private VBox createTechnicalErrorBox() {
        VBox errorBox = new VBox();
        errorBox.setSpacing(8);
        errorBox.setPadding(new Insets(16));
        Bootstrap.alertDanger(errorBox);

        Label errorTitle = Bootstrap.strong(new Label("Error Details:"));
        errorBox.getChildren().add(errorTitle);

        if (technicalDetails != null) {
            Label technicalLabel = new Label(technicalDetails);
            technicalLabel.setWrapText(true);
            technicalLabel.getStyleClass().add("error-technical");
            VBox technicalBox = new VBox(technicalLabel);
            technicalBox.setPadding(new Insets(8));
            technicalBox.getStyleClass().add("error-technical-box");
            errorBox.getChildren().add(technicalBox);
        }

        if (errorCode != null) {
            Label errorCodeLabel = new Label("Error Code: " + errorCode);
            errorCodeLabel.getStyleClass().add("error-code");
            Bootstrap.textDanger(Bootstrap.strong(errorCodeLabel));
            errorBox.getChildren().add(errorCodeLabel);
        }

        if (timestamp != null) {
            Label timestampLabel = Bootstrap.small(new Label("Timestamp: " + timestamp));
            timestampLabel.getStyleClass().add("error-timestamp");
            Bootstrap.textSecondary(timestampLabel);
            errorBox.getChildren().add(timestampLabel);
        }

        return errorBox;
    }

    /**
     * Helper method to create a new Label (following code guidelines to avoid hardcoded text).
     * This is a placeholder that should be replaced with I18nControls.newLabel() when i18n is needed.
     */
    private static Label newLabel(String text) {
        return new Label(text);
    }
}
