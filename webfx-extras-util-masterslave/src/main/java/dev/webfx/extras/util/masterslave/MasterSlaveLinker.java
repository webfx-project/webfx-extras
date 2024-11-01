package dev.webfx.extras.util.masterslave;

import dev.webfx.kit.util.properties.FXProperties;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;

import java.util.Objects;

/**
 * @author Bruno Salmon
 */
public class MasterSlaveLinker<T> {

    private boolean internalMasterChange;
    private boolean isSlaveSwitchApprovalDialogShowing;

    private final ObjectProperty<T> masterProperty;
    private final SlaveEditor<T> slaveEditor;

    public MasterSlaveLinker(SlaveEditor<T> slaveEditor) {
        this.slaveEditor = slaveEditor;
        masterProperty = FXProperties.newObjectProperty(master -> {
            // System.out.println("master = " + master);
            // Preventing reentrant calls from internal changes
            if (internalMasterChange)
                return;
            // If the master and slave are already the same, we don't do anything
            if (Objects.equals(master, getSlave()))
                return;
            // Otherwise we check the approval for the slave switch (unless a dialog is already showing)
            if (!isSlaveSwitchApprovalDialogShowing) {
                checkSlaveSwitchApproval(false, () -> {
                    // If it has been approved, we apply the switch (slave = master) and ask the slave editor to edit it
                    slaveEditor.setSlave(master);
                });
            }
        });
    }

    public T getMaster() {
        return masterProperty.get();
    }

    public ObjectProperty<T> masterProperty() {
        return masterProperty;
    }

    public void setMaster(T master) {
        masterProperty.set(master);
    }

    private T getSlave() {
        return slaveEditor.getSlave();
    }

    public void checkSlaveSwitchApproval(boolean clearMasterOnApproval, Runnable onApprovalCallback) {
        // Case of immediate approval: when there is no slave edited, or when the edited slave has no changes
        if (getSlave() == null || !slaveEditor.hasChanges())
            callOnApprovalCallback(clearMasterOnApproval, onApprovalCallback);
        else { // Otherwise we need to ask the user for approval in a dialog
            isSlaveSwitchApprovalDialogShowing = true;
            Platform.runLater(() -> { // The reason why we postpone the call to show the dialog is to ensure that the
                // dialog area (in Modality) matches the current activity. Otherwise, if this code is ran while resuming
                // the activity (which happens when masterEntityProperty is bound to the selectedEntityProperty of a
                // ReactiveVisualMapper which becomes active again on activity resume), the dialog area hasn't been
                // updated yet, and the dialog would be displayed in the leaving activity instead of the entering activity.
                slaveEditor.showSlaveSwitchApprovalDialog(approved -> {
                    isSlaveSwitchApprovalDialogShowing = false;
                    // If the user approved the switch (which will discard the changes on the previous slave)
                    if (approved) // then we call the approval callback
                        callOnApprovalCallback(clearMasterOnApproval, onApprovalCallback);
                    else // otherwise (if the user disapproved the switch), we roll back the master to the slave
                        setMaster(getSlave());
                });
            });
        }
    }

    private void callOnApprovalCallback(boolean clearMasterOnApproval, Runnable onApprovalCallback) {
        if (clearMasterOnApproval) {
            internalMasterChange = true;
            setMaster(null);
            internalMasterChange = false;
        }
        onApprovalCallback.run();
    }
}
