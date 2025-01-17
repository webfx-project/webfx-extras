package dev.webfx.extras.filepicker;

import dev.webfx.extras.filepicker.spi.FilePickerProvider;
import dev.webfx.kit.util.properties.ObservableLists;
import dev.webfx.platform.file.File;
import dev.webfx.platform.util.collection.Collections;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;

/**
 * @author Bruno Salmon
 */
public abstract class FilePicker {

    public static FilePicker create() {
        return FilePickerProvider.get().createFileChooserPeer();
    }

    private final ObjectProperty<Node> graphicProperty = new SimpleObjectProperty<>();

    private final BooleanProperty multipleProperty = new SimpleBooleanProperty();

    private final ObjectProperty<File> selectedFileProperty = new SimpleObjectProperty<>();

    private final ObservableList<File> selectedFiles = FXCollections.observableArrayList();

    private final ObservableList<String> acceptedExtensions = FXCollections.observableArrayList();

    public FilePicker() {
        ObservableLists.runOnListChange(() -> selectedFileProperty.set(Collections.first(selectedFiles)), acceptedExtensions);
    }

    public abstract Node getView();

    public Node getGraphic() {
        return graphicProperty.get();
    }

    public ObjectProperty<Node> graphicProperty() {
        return graphicProperty;
    }

    public void setGraphic(Node graphic) {
        graphicProperty.set(graphic);
    }

    public boolean isMultiple() {
        return multipleProperty.get();
    }

    public BooleanProperty multipleProperty() {
        return multipleProperty;
    }

    public void setMultiple(boolean multiple) {
        this.multipleProperty.set(multiple);
    }

    public ReadOnlyObjectProperty<File> selectedFileProperty() {
        return selectedFileProperty;
    }

    public ObservableList<File> getSelectedFiles() {
        return selectedFiles;
    }

    public ObservableList<String> getAcceptedExtensions() {
        return acceptedExtensions;
    }
}
