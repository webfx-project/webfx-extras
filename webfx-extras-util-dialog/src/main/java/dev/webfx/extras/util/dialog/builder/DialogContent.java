package dev.webfx.extras.util.dialog.builder;

import dev.webfx.extras.i18n.controls.I18nControls;
import dev.webfx.extras.styles.bootstrap.Bootstrap;
import dev.webfx.extras.util.dialog.DialogCallback;
import dev.webfx.extras.util.dialog.i18n.DialogI18nKeys;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

/**
 * DialogContent provides a fluent API for creating modal dialogs with consistent styling.
 * All dialogs follow the HTML design template with proper spacing, borders, and colors.
 *
 * <h2>Quick Selection Guide:</h2>
 * <ul>
 *   <li><b>Information dialogs</b> (blue icon): Use when showing non-critical information to the user</li>
 *   <li><b>Success dialogs</b> (green icon): Use when confirming successful operations</li>
 *   <li><b>Warning dialogs</b> (yellow icon): Use when asking for confirmation of potentially risky actions</li>
 *   <li><b>Error dialogs</b> (red X icon): Use when displaying error messages</li>
 *   <li><b>Delete dialogs</b> (red trash icon): Use when confirming destructive delete operations</li>
 *   <li><b>Form/Edit dialogs</b> (no icon): Use for forms with Save/Cancel buttons</li>
 * </ul>
 *
 * <h2>Visual Structure:</h2>
 * All dialogs have three sections:
 * <ul>
 *   <li><b>Header</b>: White background, contains title text only (no icon)</li>
 *   <li><b>Body</b>: Light grey background (#fafbfc), contains content text, info boxes, or custom content</li>
 *   <li><b>Footer</b>: White background, contains action buttons (horizontal layout)</li>
 * </ul>
 *
 * @author Bruno Salmon
 */
public final class DialogContent implements DialogBuilder {

    /**
     * Button styling options that automatically apply Bootstrap CSS classes.
     */
    public enum ButtonStyle {
        SUCCESS,   // Green button - for successful/positive actions (e.g., "Save", "Confirm")
        PRIMARY,   // Blue button - for primary/informational actions (e.g., "OK", "Continue")
        WARNING,   // Orange button - for potentially dangerous confirmations (e.g., "Yes, proceed")
        DANGER,    // Red button - for destructive/error actions (e.g., "Delete", "Remove")
        SECONDARY  // Grey button - for neutral/cancel actions (e.g., "Cancel", "Close")
    }

    private String headerText;
    private String contentText;
    private String primaryButtonText;
    private String secondaryButtonText;

    private Node content;
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

    // ==================== FACTORY METHODS: INFORMATION DIALOGS ====================
    // Use these when you need to display non-critical information to the user.
    // These dialogs have a blue info icon and a single "OK" button.

    /**
     * Creates a simple information dialog with a message.
     *
     * <p><b>Use when:</b> Displaying simple informational messages that don't require additional context.
     *
     * <p><b>Visual:</b> Blue info icon, white header, grey body, single "OK" button (blue/primary).
     *
     * <p><b>Example usage:</b>
     * <pre>
     * DialogContent dialog = DialogContent.createInformationDialog(
     *     "Account Information",
     *     "Account Status",
     *     "Your account is currently active."
     * );
     * </pre>
     *
     * @param title Window title (not displayed in modern design, kept for compatibility)
     * @param headerText Header text displayed at the top of the dialog
     * @param contentText Main message text in the body
     * @return Configured DialogContent ready to build or customize further
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
     * Creates an information dialog with a highlighted blue info box.
     *
     * <p><b>Use when:</b> Displaying information with an important detail that needs visual emphasis.
     *
     * <p><b>Visual:</b> Blue info icon, main content text, plus a blue highlighted box below for emphasis.
     *
     * <p><b>Example usage:</b>
     * <pre>
     * DialogContent dialog = DialogContent.createInformationDialogWithBox(
     *     "Payment Information",
     *     "Payment Received",
     *     "Your payment has been processed successfully.",
     *     "Transaction ID: 12345-ABCDE"
     * );
     * </pre>
     *
     * @param title Window title (not displayed in modern design)
     * @param headerText Header text displayed at the top
     * @param contentText Main message text
     * @param boxContent Text to display in the highlighted blue info box
     * @return Configured DialogContent ready to build or customize
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
     * Creates an information dialog requiring the user to choose between two actions.
     *
     * <p><b>Use when:</b> Displaying information that requires a user decision between two options.
     * This is NOT for warnings or destructive actions (use warning/delete dialogs for those).
     *
     * <p><b>Visual:</b> Blue info icon, info box for details, two buttons (default: Yes/No, but customizable).
     *
     * <p><b>Common customizations after creation:</b>
     * <ul>
     *   <li>Call {@code .setCancelSave()} for form dialogs with Save/Cancel buttons</li>
     *   <li>Call {@code .setCustomButtons("Accept", "Decline")} for custom button labels</li>
     *   <li>Call {@code .setContent(formNode)} to add form fields</li>
     * </ul>
     *
     * <p><b>Example usage for forms:</b>
     * <pre>
     * DialogContent dialog = DialogContent.createInformationDialogWithTwoActions(
     *     "Edit Member",
     *     "Edit Member Details",
     *     "",
     *     "Please update the member information below."
     * )
     * .setContent(formFieldsNode)
     * .setCancelSave();
     * </pre>
     *
     * @param title Window title (not displayed in modern design)
     * @param headerText Header text displayed at the top
     * @param contentText Main message text (can be empty if using only boxContent)
     * @param boxContent Text to display in the highlighted blue info box
     * @return Configured DialogContent with two buttons (Yes/No by default)
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

    // ==================== FACTORY METHODS: SUCCESS DIALOGS ====================
    // Use these when confirming successful completion of an operation.
    // These dialogs have a green checkmark icon and a single "OK" button.

    /**
     * Creates a simple success confirmation dialog.
     *
     * <p><b>Use when:</b> Confirming that an operation completed successfully (e.g., "Member added", "Changes saved").
     *
     * <p><b>Visual:</b> Green checkmark icon, success message, single "OK" button (green).
     *
     * <p><b>Note:</b> Consider using Bootstrap success messages instead of dialogs for non-critical success notifications,
     * as they're less intrusive and don't block user interaction.
     *
     * <p><b>Example usage:</b>
     * <pre>
     * DialogContent dialog = DialogContent.createSuccessDialog(
     *     "Success",
     *     "Member Added",
     *     "The new member has been added to your account."
     * );
     * </pre>
     *
     * @param title Window title (not displayed in modern design)
     * @param headerText Header text displayed at the top
     * @param contentText Success message text
     * @return Configured DialogContent ready to build
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
     * Creates a success dialog with a highlighted green success box for additional details.
     *
     * <p><b>Use when:</b> Confirming success with important details to highlight (e.g., confirmation number, next steps).
     *
     * <p><b>Visual:</b> Green checkmark icon, success message, green highlighted box for emphasis.
     *
     * <p><b>Example usage:</b>
     * <pre>
     * DialogContent dialog = DialogContent.createSuccessDialogWithBox(
     *     "Booking Confirmed",
     *     "Reservation Successful",
     *     "Your booking has been confirmed.",
     *     "Confirmation number: RES-2024-001234"
     * );
     * </pre>
     *
     * @param title Window title (not displayed in modern design)
     * @param headerText Header text displayed at the top
     * @param contentText Success message text
     * @param boxContent Text to display in the highlighted green success box
     * @return Configured DialogContent ready to build
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

    // ==================== FACTORY METHODS: WARNING DIALOGS ====================
    // Use these when asking the user to confirm a potentially risky or irreversible action.
    // These dialogs have a yellow warning icon and Yes/No buttons.

    /**
     * Creates a warning dialog for confirming potentially risky actions.
     *
     * <p><b>Use when:</b> Asking the user to confirm an action that has significant consequences but isn't destructive
     * (e.g., "Proceed with payment?", "Confirm booking?").
     *
     * <p><b>Visual:</b> Yellow warning icon, warning message, Yes/No buttons (Yes is orange/warning style).
     *
     * <p><b>Example usage:</b>
     * <pre>
     * DialogContent dialog = DialogContent.createWarningDialog(
     *     "Confirm Payment",
     *     "Proceed with Payment?",
     *     "This will charge your credit card $150.00. Continue?"
     * );
     * </pre>
     *
     * @param title Window title (not displayed in modern design)
     * @param headerText Header text displayed at the top
     * @param contentText Warning message asking for confirmation
     * @return Configured DialogContent with Yes/No buttons
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
     * Creates a warning dialog with a highlighted yellow warning box for additional emphasis.
     *
     * <p><b>Use when:</b> Asking for confirmation with important consequences that need extra visual emphasis.
     *
     * <p><b>Visual:</b> Yellow warning icon, warning message, yellow highlighted box, Yes/No buttons.
     *
     * <p><b>Example usage:</b>
     * <pre>
     * DialogContent dialog = DialogContent.createWarningDialogWithBox(
     *     "Confirm Cancellation",
     *     "Cancel Booking?",
     *     "Are you sure you want to cancel this booking?",
     *     "This action may result in cancellation fees."
     * );
     * </pre>
     *
     * @param title Window title (not displayed in modern design)
     * @param headerText Header text displayed at the top
     * @param contentText Warning message text
     * @param boxContent Text to display in the highlighted yellow warning box
     * @return Configured DialogContent with Yes/No buttons
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

    // ==================== FACTORY METHODS: ERROR DIALOGS ====================
    // Use these when displaying error messages to the user.
    // These dialogs have a red X icon and a single "OK" button.

    /**
     * Creates a simple error dialog for user-friendly error messages.
     *
     * <p><b>Use when:</b> Displaying user-friendly error messages without technical details
     * (e.g., validation errors, operation failures).
     *
     * <p><b>Visual:</b> Red X icon, error message, single "OK" button (red/danger style).
     *
     * <p><b>Example usage:</b>
     * <pre>
     * DialogContent dialog = DialogContent.createErrorDialog(
     *     "Error",
     *     "Unable to Save",
     *     "Please fill in all required fields before saving."
     * );
     * </pre>
     *
     * @param title Window title (not displayed in modern design)
     * @param headerText Header text displayed at the top
     * @param contentText Error message text (user-friendly)
     * @return Configured DialogContent ready to build
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
     * Creates an error dialog with technical details for debugging.
     *
     * <p><b>Use when:</b> Displaying system errors with technical information for support/debugging
     * (e.g., API errors, database errors, unexpected exceptions).
     *
     * <p><b>Visual:</b> Red X icon, user-friendly error message, red box with technical details, error code, and timestamp.
     *
     * <p><b>Example usage:</b>
     * <pre>
     * DialogContent dialog = DialogContent.createErrorDialogWithTechnicalDetails(
     *     "System Error",
     *     "An Error Occurred",
     *     "Unable to complete your request. Please contact support if this persists.",
     *     "NullPointerException at BookingService.java:123",
     *     "ERR-500-DB",
     *     "2024-01-15 14:30:22"
     * );
     * </pre>
     *
     * @param title Window title (not displayed in modern design)
     * @param headerText Header text displayed at the top
     * @param contentText User-friendly error message
     * @param technicalDetails Technical error details (stack trace, exception message, etc.)
     * @param errorCode Error code for support reference
     * @param timestamp When the error occurred
     * @return Configured DialogContent ready to build
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

    // ==================== FACTORY METHODS: DELETE DIALOGS ====================
    // Use these when confirming destructive delete operations.
    // These dialogs have a red trash icon and Yes/No buttons to prevent accidental deletion.

    /**
     * Creates a delete confirmation dialog for destructive actions.
     *
     * <p><b>Use when:</b> Asking the user to confirm deletion or removal of data
     * (e.g., "Delete member?", "Remove booking?").
     *
     * <p><b>Visual:</b> Red trash icon, confirmation question, Yes/No buttons (Yes is red/danger style).
     *
     * <p><b>Example usage:</b>
     * <pre>
     * DialogContent dialog = DialogContent.createDeleteDialog(
     *     "Delete Member",
     *     "Remove Member",
     *     "Are you sure you want to remove this member from your account?"
     * );
     * </pre>
     *
     * @param title Window title (not displayed in modern design)
     * @param headerText Header text displayed at the top
     * @param contentText Confirmation question asking if user wants to delete
     * @return Configured DialogContent with Yes/No buttons
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
     * Creates a delete confirmation dialog with additional warning information.
     *
     * <p><b>Use when:</b> Asking to confirm deletion with important consequences that need extra emphasis
     * (e.g., "This will also cancel their active bookings", "This cannot be undone").
     *
     * <p><b>Visual:</b> Red trash icon, confirmation question, yellow warning box for consequences, Yes/No buttons.
     *
     * <p><b>Example usage:</b>
     * <pre>
     * DialogContent dialog = DialogContent.createDeleteDialogWithWarning(
     *     "Delete Member",
     *     "Remove Member",
     *     "Are you sure you want to remove this member?",
     *     "This will also cancel all their active bookings. This action cannot be undone."
     * );
     * </pre>
     *
     * @param title Window title (not displayed in modern design)
     * @param headerText Header text displayed at the top
     * @param contentText Confirmation question
     * @param warningContent Warning text about consequences (displayed in yellow box)
     * @return Configured DialogContent with Yes/No buttons
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

    // ==================== LEGACY FACTORY METHODS ====================
    // These methods are kept for backward compatibility with existing code.
    // For new code, prefer the more specific factory methods above (createWarningDialog, etc.)

    /**
     * Creates a confirmation dialog without icon.
     *
     * @deprecated Use {@link #createWarningDialog} for risky confirmations or
     *             {@link #createInformationDialogWithTwoActions} for neutral confirmations.
     */
    @Deprecated
    public static DialogContent createConfirmationDialog(String headerText, String contentText) {
        return createConfirmationDialog("Confirmation", headerText, contentText);
    }

    /**
     * Creates a confirmation dialog without icon.
     *
     * @deprecated Use {@link #createWarningDialog} for risky confirmations or
     *             {@link #createInformationDialogWithTwoActions} for neutral confirmations.
     */
    @Deprecated
    public static DialogContent createConfirmationDialog(String title, String headerText, String contentText) {
        return new DialogContent().setTitle(title).setHeaderText(headerText).setContentText(contentText).setYesNo();
    }

    /**
     * Creates a confirmation dialog with warning icon (yellow).
     *
     * @deprecated Use {@link #createWarningDialog} or {@link #createWarningDialogWithBox} instead.
     */
    @Deprecated
    public static DialogContent createConfirmationDangerDialog(String headerText, String contentText) {
        return createConfirmationDangerDialog("Confirmation", headerText, contentText);
    }

    /**
     * Creates a confirmation dialog with warning icon (yellow).
     *
     * @deprecated Use {@link #createWarningDialog} or {@link #createWarningDialogWithBox} instead.
     */
    @Deprecated
    public static DialogContent createConfirmationDangerDialog(String title, String headerText, String contentText) {
        return new DialogContent()
                .setTitle(title)
                .setHeaderText(headerText)
                .setContentText(contentText)
                .setHeaderIcon(createWarningIcon())
                .setPrimaryButtonStyle(ButtonStyle.WARNING)
                .setYesNo();
    }

    // ==================== ICON CREATION HELPERS ====================
    // These create the SVG icons used in dialog headers.
    // Icons are NOT displayed in the header (design requirement), but may be used elsewhere.

    /**
     * Creates a blue information icon (circle with "i").
     */
    private static Node createInfoIcon() {
        SVGPath svgPath = new SVGPath();
        svgPath.setContent("M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z");
        svgPath.setFill(Color.web("#00a6e0"));
        svgPath.setScaleX(2);
        svgPath.setScaleY(2);
        return svgPath;
    }

    /**
     * Creates a green success icon (circle with checkmark).
     */
    private static Node createSuccessIcon() {
        SVGPath svgPath = new SVGPath();
        svgPath.setContent("M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z");
        svgPath.setFill(Color.web("#28a745"));
        svgPath.setScaleX(2);
        svgPath.setScaleY(2);
        return svgPath;
    }

    /**
     * Creates an orange/yellow warning icon (triangle with exclamation mark).
     */
    private static Node createWarningIcon() {
        SVGPath svgPath = new SVGPath();
        svgPath.setContent("M32.427,7.987c2.183,0.124 4,1.165 5.096,3.281l17.936,36.208c1.739,3.66 -0.954,8.585 -5.373,8.656l-36.119,0c-4.022,-0.064 -7.322,-4.631 -5.352,-8.696l18.271,-36.207c0.342,-0.65 0.498,-0.838 0.793,-1.179c1.186,-1.375 2.483,-2.111 4.748,-2.063Zm-0.295,3.997c-0.687,0.034 -1.316,0.419 -1.659,1.017c-6.312,11.979 -12.397,24.081 -18.301,36.267c-0.546,1.225 0.391,2.797 1.762,2.863c12.06,0.195 24.125,0.195 36.185,0c1.325,-0.064 2.321,-1.584 1.769,-2.85c-5.793,-12.184 -11.765,-24.286 -17.966,-36.267c-0.366,-0.651 -0.903,-1.042 -1.79,-1.03Z M33.631,40.581l-3.348,0l-0.368,-16.449l4.1,0l-0.384,16.449Zm-3.828,5.03c0,-0.609 0.197,-1.113 0.592,-1.514c0.396,-0.4 0.935,-0.601 1.618,-0.601c0.684,0 1.223,0.201 1.618,0.601c0.395,0.401 0.593,0.905 0.593,1.514c0,0.587 -0.193,1.078 -0.577,1.473c-0.385,0.395 -0.929,0.593 -1.634,0.593c-0.705,0 -1.249,-0.198 -1.634,-0.593c-0.384,-0.395 -0.576,-0.886 -0.576,-1.473Z");
        svgPath.setFill(Color.web("#ff9800"));
        svgPath.setScaleX(0.5);
        svgPath.setScaleY(0.5);
        return svgPath;
    }

    /**
     * Creates a red error icon (circle with X).
     */
    private static Node createErrorIcon() {
        SVGPath svgPath = new SVGPath();
        svgPath.setContent("M12 2C6.47 2 2 6.47 2 12s4.47 10 10 10 10-4.47 10-10S17.53 2 12 2zm5 13.59L15.59 17 12 13.41 8.41 17 7 15.59 10.59 12 7 8.41 8.41 7 12 10.59 15.59 7 17 8.41 13.41 12 17 15.59z");
        svgPath.setFill(Color.web("#dc3545"));
        svgPath.setScaleX(2);
        svgPath.setScaleY(2);
        return svgPath;
    }

    /**
     * Creates a red delete/trash icon.
     */
    private static Node createDeleteIcon() {
        SVGPath svgPath = new SVGPath();
        svgPath.setContent("M 6.3952 2.7317 H 9.4429 C 9.4429 2.3177 9.2823 1.9207 8.9965 1.6279 C 8.7107 1.3352 8.3232 1.1707 7.919 1.1707 C 7.5149 1.1707 7.1273 1.3352 6.8416 1.6279 C 6.5558 1.9207 6.3952 2.3177 6.3952 2.7317 Z M 5.2524 2.7317 C 5.2524 2.373 5.3214 2.0178 5.4554 1.6863 C 5.5894 1.3549 5.7858 1.0538 6.0334 0.8001 C 6.2811 0.5464 6.575 0.3452 6.8986 0.2079 C 7.2221 0.0707 7.5689 0 7.919 0 C 8.2692 0 8.616 0.0707 8.9395 0.2079 C 9.263 0.3452 9.5571 0.5464 9.8047 0.8001 C 10.0523 1.0538 10.2487 1.3549 10.3827 1.6863 C 10.5168 2.0178 10.5857 2.373 10.5857 2.7317 H 14.9667 C 15.1182 2.7317 15.2636 2.7934 15.3707 2.9032 C 15.4779 3.0129 15.5381 3.1618 15.5381 3.3171 C 15.5381 3.4723 15.4779 3.6212 15.3707 3.731 C 15.2636 3.8408 15.1182 3.9024 14.9667 3.9024 H 13.961 L 13.0695 13.3549 C 13.0011 14.0792 12.6718 14.7514 12.1459 15.2405 C 11.6198 15.7295 10.9349 16.0003 10.2246 16 H 5.6135 C 4.9033 16.0001 4.2186 15.7292 3.6927 15.2402 C 3.1669 14.7512 2.8377 14.0791 2.7693 13.3549 L 1.8771 3.9024 H 0.8714 C 0.7199 3.9024 0.5745 3.8408 0.4674 3.731 C 0.3602 3.6212 0.3 3.4723 0.3 3.3171 C 0.3 3.1618 0.3602 3.0129 0.4674 2.9032 C 0.5745 2.7934 0.7199 2.7317 0.8714 2.7317 H 5.2524 Z M 6.7762 6.439 C 6.7762 6.2838 6.716 6.1349 6.6088 6.0251 C 6.5017 5.9153 6.3563 5.8537 6.2048 5.8537 C 6.0532 5.8537 5.9079 5.9153 5.8007 6.0251 C 5.6935 6.1349 5.6333 6.2838 5.6333 6.439 V 12.2926 C 5.6333 12.4479 5.6935 12.5968 5.8007 12.7066 C 5.9079 12.8164 6.0532 12.878 6.2048 12.878 C 6.3563 12.878 6.5017 12.8164 6.6088 12.7066 C 6.716 12.5968 6.7762 12.4479 6.7762 12.2926 V 6.439 Z M 9.6333 5.8537 C 9.4818 5.8537 9.3364 5.9153 9.2293 6.0251 C 9.1221 6.1349 9.0619 6.2838 9.0619 6.439 V 12.2926 C 9.0619 12.4479 9.1221 12.5968 9.2293 12.7066 C 9.3364 12.8164 9.4818 12.878 9.6333 12.878 C 9.7849 12.878 9.9302 12.8164 10.0374 12.7066 C 10.1446 12.5968 10.2048 12.4479 10.2048 12.2926 V 6.439 C 10.2048 6.2838 10.1446 6.1349 10.0374 6.0251 C 9.9302 5.9153 9.7849 5.8537 9.6333 5.8537 Z");
        svgPath.setFill(Color.web("#dc3545"));
        svgPath.setScaleX(1.2);
        svgPath.setScaleY(1.2);
        return svgPath;
    }

    // ==================== COLORED BOX CREATION HELPERS ====================
    // These create styled boxes for highlighting important information in dialog bodies.

    /**
     * Creates a blue info box with Bootstrap alert-info styling.
     * Use for highlighting important informational details.
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
     * Creates a green success box with Bootstrap alert-success styling.
     * Use for highlighting success details (confirmation numbers, next steps).
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
     * Creates a yellow/orange warning box with Bootstrap alert-warning styling.
     * Use for highlighting consequences or important warnings.
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
     * Creates a red danger/error box with Bootstrap alert-danger styling.
     * Use for highlighting error messages or critical warnings.
     */
    private static Node createAlertDangerBox(String content) {
        Label label = new Label(content);
        label.setWrapText(true);
        VBox box = new VBox(label);
        box.setPadding(new Insets(16));
        Bootstrap.alertDanger(box);
        return box;
    }

    // ==================== CONSTRUCTOR AND BASIC SETTERS ====================

    /**
     * Creates a new DialogContent with default OK/Cancel buttons.
     * <p>
     * <b>Note:</b> In most cases, you should use the static factory methods instead
     * (e.g., createInformationDialog, createWarningDialog) rather than this constructor directly.
     * The factory methods provide better defaults and clearer intent.
     */
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

    /**
     * Sets the window title (not displayed in modern dialog design, kept for compatibility).
     */
    public DialogContent setTitle(String title) {
        return this;
    }

    /**
     * Sets the header text displayed at the top of the dialog (in white section).
     */
    public DialogContent setHeaderText(String headerText) {
        this.headerText = headerText;
        return this;
    }

    /**
     * Sets the main content text displayed in the dialog body (in grey section).
     */
    public DialogContent setContentText(String contentText) {
        this.contentText = contentText;
        return this;
    }

    /**
     * Sets custom content node (e.g., form fields, complex layouts) to display in the dialog body.
     * This will appear below contentText and infoBox if those are also set.
     */
    public DialogContent setContent(Node content) {
        this.content = content;
        return this;
    }

    /**
     * Sets the header icon (not displayed in current design - header shows title text only).
     * Kept for potential future use or backward compatibility.
     */
    public DialogContent setHeaderIcon(Node headerIcon) {
        return this;
    }

    /**
     * Sets a custom info box node to display in the dialog body.
     * Use the createAlert*Box() helper methods to create styled boxes,
     * or use factory methods that include boxes (e.g., createInformationDialogWithBox).
     */
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

    // ==================== BUTTON TEXT PRESETS ====================
    // Convenient methods to set common button text combinations.
    // These set the button text only - button styles are set separately via factory methods.

    /**
     * Sets buttons to "Yes" / "No".
     * Use for confirmation questions (already set by warning/delete factory methods).
     */
    public DialogContent setYesNo() {
        primaryButtonText = getI18nText(DialogI18nKeys.ButtonYes);
        secondaryButtonText = getI18nText(DialogI18nKeys.ButtonNo);
        return this;
    }

    /**
     * Sets buttons to "Save" / "Cancel".
     * Use for form/edit dialogs. Same as setCancelSave().
     */
    public DialogContent setSaveCancel() {
        primaryButtonText = getI18nText(DialogI18nKeys.ButtonSave);
        secondaryButtonText = getI18nText(DialogI18nKeys.ButtonCancel);
        return this;
    }

    /**
     * Sets buttons to "Save" / "Cancel".
     * Use for form/edit dialogs. Same as setSaveCancel().
     * <p>
     * <b>Example:</b>
     * <pre>
     * dialog.setContent(formFields).setCancelSave();
     * </pre>
     */
    public void setCancelSave() {
        primaryButtonText = getI18nText(DialogI18nKeys.ButtonSave);
        secondaryButtonText = getI18nText(DialogI18nKeys.ButtonCancel);
    }

    /**
     * Sets a single "OK" button (hides secondary button).
     * Use for information/success/error dialogs that only need acknowledgment.
     * Already set by info/success/error factory methods.
     */
    public DialogContent setOk() {
        primaryButtonText = getI18nText(DialogI18nKeys.ButtonOk);
        secondaryButton.setManaged(false);
        return this;
    }

    /**
     * Sets buttons to "OK" / "Cancel".
     * Use for optional confirmations where user can cancel.
     */
    public void setOkCancel() {
        primaryButtonText = getI18nText(DialogI18nKeys.ButtonOk);
        secondaryButtonText = getI18nText(DialogI18nKeys.ButtonCancel);
    }

    /**
     * Sets buttons to "Confirm" / "Cancel".
     * Use for confirmations where "Confirm" is clearer than "Yes" or "OK".
     * Same as setConfirmCancel().
     */
    public void setCancelConfirm() {
        primaryButtonText = getI18nText(DialogI18nKeys.ButtonConfirm);
        secondaryButtonText = getI18nText(DialogI18nKeys.ButtonCancel);
    }

    /**
     * Sets buttons to "Confirm" / "Cancel".
     * Use for confirmations where "Confirm" is clearer than "Yes" or "OK".
     * Same as setCancelConfirm().
     */
    public void setConfirmCancel() {
        primaryButtonText = getI18nText(DialogI18nKeys.ButtonConfirm);
        secondaryButtonText = getI18nText(DialogI18nKeys.ButtonCancel);
    }

    /**
     * Sets custom text for both primary and secondary buttons.
     * Useful for dialogs that need specific action labels (e.g., "Accept Invitation" / "Decline").
     */
    public void setCustomButtons(String primaryText, String secondaryText) {
        primaryButtonText = primaryText;
        secondaryButtonText = secondaryText;
        secondaryButton.setManaged(true);  // Ensure secondary button is visible
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

    public void setSecondaryButton(Button secondaryButton) {
        this.secondaryButton = secondaryButton;
        applyButtonStyle(secondaryButton, secondaryButtonStyle);
        secondaryButton.setCancelButton(true);
    }

    // ==================== BUILD METHOD ====================

    /**
     * Builds the complete dialog UI following the HTML design template.
     * <p>
     * The dialog has a three-section structure:
     * <ol>
     *   <li><b>Header</b> (white background): Title text only, no icon</li>
     *   <li><b>Body</b> (light grey background): Content text, info boxes, custom content</li>
     *   <li><b>Footer</b> (white background): Action buttons in horizontal layout</li>
     * </ol>
     * <p>
     * Visual styling (all applied via Java API, no CSS):
     * <ul>
     *   <li>12px rounded corners with drop shadow</li>
     *   <li>1px solid #e9ecef borders between sections</li>
     *   <li>520px max-width</li>
     *   <li>Consistent padding following HTML design (24-28px)</li>
     * </ul>
     *
     * @return The complete dialog UI as a Region ready to display
     */
    @Override
    public Region build() {
        // Ensure button text is set
        if (primaryButtonText != null) {
            primaryButton.setText(primaryButtonText);
        }
        if (secondaryButtonText != null) {
            secondaryButton.setText(secondaryButtonText);
        }

        // Create main container with max-width constraint (520px as per HTML design)
        VBox dialogContainer = new VBox();
        dialogContainer.setMaxWidth(520);
        dialogContainer.getStyleClass().add("modal-content");

        // Apply rounded corners and shadow from HTML design (following code guidelines: use Java code, not CSS)
        CornerRadii cornerRadii = new CornerRadii(12);
        dialogContainer.setBackground(new Background(new BackgroundFill(Color.WHITE, cornerRadii, Insets.EMPTY)));

        // Add drop shadow effect
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.15));
        dropShadow.setRadius(10);
        dropShadow.setOffsetY(2);
        dialogContainer.setEffect(dropShadow);

        // Header section
        VBox headerSection = createHeaderSection();
        if (headerSection != null) {
            dialogContainer.getChildren().add(headerSection);
        }

        // Body section
        VBox bodySection = createBodySection();
        if (bodySection != null) {
            dialogContainer.getChildren().add(bodySection);
        }

        // Footer section with buttons
        Region footerSection = createResponsiveFooterSection();
        dialogContainer.getChildren().add(footerSection);

        // Apply desktop padding by default (dialogs are always in desktop mode)
        applyDesktopPadding(dialogContainer);

        return dialogContainer;
    }

    // ==================== PRIVATE SECTION BUILDERS ====================

    /**
     * Creates the header section with title text only (no icon as per design requirement).
     * <p>
     * Visual styling:
     * <ul>
     *   <li>White background with top corners rounded (12px)</li>
     *   <li>Bottom border separator (1px solid #e9ecef)</li>
     *   <li>Title as Bootstrap h3 heading</li>
     * </ul>
     *
     * @return Header section VBox, or null if no headerText is set
     */
    private VBox createHeaderSection() {
        if (headerText == null) {
            return null;
        }

        VBox header = new VBox();
        header.getStyleClass().add("modal-header");

        // Add white background with rounded top corners (following code guidelines: use Java code, not CSS)
        CornerRadii topCorners = new CornerRadii(12, 12, 0, 0, false);
        header.setBackground(new Background(new BackgroundFill(Color.WHITE, topCorners, Insets.EMPTY)));

        // Add bottom border separator (1px solid #e9ecef from HTML design)
        Color borderColor = Color.web("#e9ecef");
        BorderStroke bottomBorder = new BorderStroke(
            Color.TRANSPARENT, Color.TRANSPARENT, borderColor, Color.TRANSPARENT,
            BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
            CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0), Insets.EMPTY
        );
        header.setBorder(new Border(bottomBorder));

        // Create title (no icon as per HTML design)
        Label headerLabel = Bootstrap.h3(newLabel(headerText));
        headerLabel.setWrapText(true);
        headerLabel.getStyleClass().add("modal-title");

        header.getChildren().add(headerLabel);

        return header;
    }

    /**
     * Creates the body section containing content text, info boxes, and custom content.
     * <p>
     * Visual styling:
     * <ul>
     *   <li>Light grey background (#fafbfc)</li>
     *   <li>Contains (in order, if present): contentText, infoBox, technical errors, custom content</li>
     *   <li>12px margin between elements</li>
     * </ul>
     *
     * @return Body section VBox, or null if no content is set
     */
    private VBox createBodySection() {
        VBox body = new VBox();
        body.getStyleClass().add("modal-body");

        // Light grey background for body (following code guidelines: use Java code, not CSS)
        Color lightGrey = Color.web("#fafbfc");
        body.setBackground(new Background(new BackgroundFill(lightGrey, CornerRadii.EMPTY, Insets.EMPTY)));

        boolean hasContent = false;

        // Main content text
        if (contentText != null) {
            Label contentLabel = newLabel(contentText);
            contentLabel.setWrapText(true);
            contentLabel.getStyleClass().add("modal-text");
            body.getChildren().add(contentLabel);
            hasContent = true;
        }

        // Info box (if present)
        if (infoBox != null) {
            if (hasContent) {
                VBox.setMargin(infoBox, new Insets(12, 0, 0, 0));
            }
            body.getChildren().add(infoBox);
            hasContent = true;
        }

        // Technical error details (if present)
        if (technicalDetails != null || errorCode != null || timestamp != null) {
            VBox errorBox = createTechnicalErrorBox();
            if (hasContent) {
                VBox.setMargin(errorBox, new Insets(12, 0, 0, 0));
            }
            body.getChildren().add(errorBox);
            hasContent = true;
        }

        // Custom content
        if (content != null) {
            if (hasContent) {
                VBox.setMargin(content, new Insets(12, 0, 0, 0));
            }
            body.getChildren().add(content);
            VBox.setVgrow(content, Priority.ALWAYS);
            hasContent = true;
        }

        return hasContent ? body : null;
    }

    /**
     * Creates the footer section with action buttons in horizontal layout.
     * <p>
     * Modal dialogs (max-width 520px) always use desktop horizontal layout - no responsive switching needed.
     * <p>
     * Visual styling:
     * <ul>
     *   <li>White background with bottom corners rounded (12px)</li>
     *   <li>Top border separator (1px solid #e9ecef)</li>
     *   <li>Buttons aligned right: [Secondary] [Primary]</li>
     *   <li>16px spacing between buttons</li>
     * </ul>
     *
     * @return Footer section with buttons
     */
    private Region createResponsiveFooterSection() {
        VBox footer = new VBox();
        footer.getStyleClass().add("modal-footer");

        // Add white background with rounded bottom corners (following code guidelines: use Java code, not CSS)
        CornerRadii bottomCorners = new CornerRadii(0, 0, 12, 12, false);
        footer.setBackground(new Background(new BackgroundFill(Color.WHITE, bottomCorners, Insets.EMPTY)));

        // Add top border separator (1px solid #e9ecef from HTML design)
        Color borderColor = Color.web("#e9ecef");
        BorderStroke topBorder = new BorderStroke(
            borderColor, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT,
            BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE,
            CornerRadii.EMPTY, new BorderWidths(1, 0, 0, 0), Insets.EMPTY
        );
        footer.setBorder(new Border(topBorder));

        // Always use horizontal desktop layout for modal dialogs
        // The dialog max-width is 520px, so buttons always fit horizontally
        HBox buttonBar = createDesktopButtonLayout();
        footer.getChildren().add(buttonBar);

        return footer;
    }

    /**
     * Creates horizontal button layout with buttons aligned right.
     * <p>
     * Button order: [Secondary (Cancel/No)] [Primary (Save/Yes/OK)]
     * <p>
     * Button sizing from HTML design:
     * <ul>
     *   <li>Primary: 10px top/bottom, 28px left/right padding</li>
     *   <li>Secondary: 10px top/bottom, 24px left/right padding</li>
     *   <li>16px spacing between buttons</li>
     * </ul>
     *
     * @return HBox containing the buttons
     */
    private HBox createDesktopButtonLayout() {
        HBox buttonBar = new HBox(16); // Increased spacing between buttons
        buttonBar.setAlignment(Pos.CENTER_RIGHT);

        // Set compact sizing for desktop buttons
        primaryButton.setMaxWidth(Region.USE_COMPUTED_SIZE);
        primaryButton.setPrefWidth(Region.USE_COMPUTED_SIZE);
        primaryButton.setMinWidth(Region.USE_PREF_SIZE);
        primaryButton.setPadding(new Insets(10, 28, 10, 28)); // 0.625rem × 1.75rem from HTML design

        secondaryButton.setMaxWidth(Region.USE_COMPUTED_SIZE);
        secondaryButton.setPrefWidth(Region.USE_COMPUTED_SIZE);
        secondaryButton.setMinWidth(Region.USE_PREF_SIZE);
        secondaryButton.setPadding(new Insets(10, 24, 10, 24)); // Slightly less padding for secondary

        // Add secondary button first (left), then primary (right)
        if (secondaryButton.isManaged()) {
            buttonBar.getChildren().add(secondaryButton);
        }
        buttonBar.getChildren().add(primaryButton);

        return buttonBar;
    }

    /**
     * Applies padding to dialog sections following the HTML design template.
     * <p>
     * Padding values (all set via Java Insets, not CSS):
     * <ul>
     *   <li>Header: 24px top/bottom, 28px left/right</li>
     *   <li>Body: 28px all sides</li>
     *   <li>Footer: 20px top/bottom, 28px left/right</li>
     * </ul>
     *
     * @param dialogContainer The dialog container whose children should have padding applied
     */
    private void applyDesktopPadding(Region dialogContainer) {
        // Find and update padding for each section
        for (Node node : ((VBox) dialogContainer).getChildren()) {
            if (node.getStyleClass().contains("modal-header")) {
                ((VBox) node).setPadding(new Insets(24, 28, 24, 28)); // 1.5rem, 1.75rem
            } else if (node.getStyleClass().contains("modal-body")) {
                ((VBox) node).setPadding(new Insets(28, 28, 28, 28)); // 1.75rem
                ((VBox) node).setSpacing(0); // Content manages its own spacing
            } else if (node.getStyleClass().contains("modal-footer")) {
                ((VBox) node).setPadding(new Insets(20, 28, 20, 28)); // 1.25rem, 1.75rem
            }
        }
    }

    /**
     * Creates a technical error box with error code, timestamp, and stack trace.
     */
    private VBox createTechnicalErrorBox() {
        VBox errorBox = new VBox();
        errorBox.setSpacing(8);
        errorBox.setPadding(new Insets(16));
        Bootstrap.alertDanger(errorBox);

        Label errorTitle = Bootstrap.strong(I18nControls.newLabel(DialogI18nKeys.ErrorDetails));
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
            Label errorCodeLabel = new Label(getI18nText(DialogI18nKeys.ErrorCode) + " " + errorCode);
            errorCodeLabel.getStyleClass().add("error-code");
            Bootstrap.textDanger(Bootstrap.strong(errorCodeLabel));
            errorBox.getChildren().add(errorCodeLabel);
        }

        if (timestamp != null) {
            Label timestampLabel = Bootstrap.small(new Label(getI18nText(DialogI18nKeys.ErrorTimestamp) + " " + timestamp));
            timestampLabel.getStyleClass().add("error-timestamp");
            Bootstrap.textSecondary(timestampLabel);
            errorBox.getChildren().add(timestampLabel);
        }

        return errorBox;
    }

    /**
     * Helper method to get i18n text for a given key.
     * Uses I18nControls to access the current language translation.
     */
    private static String getI18nText(Object i18nKey) {
        // Create a temporary label to extract the text (I18nControls doesn't have a direct getText method)
        Label tempLabel = I18nControls.newLabel(i18nKey);
        return tempLabel.getText();
    }

    /**
     * Helper method to create a new Label (following code guidelines to avoid hardcoded text).
     * Uses I18nControls when i18nKey is provided, otherwise creates a plain label.
     */
    private static Label newLabel(String text) {
        return new Label(text);
    }
}
