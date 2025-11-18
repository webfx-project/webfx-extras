package dev.webfx.extras.filepicker.spi.impl.elemental2;

import dev.webfx.extras.filepicker.spi.impl.FilePickerClickableRegion;
import dev.webfx.kit.mapper.peers.javafxgraphics.SceneRequester;
import dev.webfx.kit.mapper.peers.javafxgraphics.base.RegionPeerBase;
import dev.webfx.kit.mapper.peers.javafxgraphics.base.RegionPeerMixin;
import dev.webfx.kit.mapper.peers.javafxgraphics.elemental2.html.HtmlRegionPeer;
import dev.webfx.kit.mapper.peers.javafxgraphics.elemental2.util.HtmlUtil;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.kit.util.properties.ObservableLists;
import elemental2.dom.FileList;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLLabelElement;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bruno Salmon
 */
public class HtmlFilePickerClickableRegionPeer
        <N extends FilePickerClickableRegion, NB extends RegionPeerBase<N, NB, NM>, NM extends RegionPeerMixin<N, NB, NM>>
        extends HtmlRegionPeer<N, NB, NM> {

    private final HTMLInputElement fileInput = HtmlUtil.createInputElement("file");

    public HtmlFilePickerClickableRegionPeer() {
        super((NB) new RegionPeerBase(), HtmlUtil.createLabelElement());
        HTMLLabelElement fileLabel = (HTMLLabelElement) getElement();
        fileInput.style.visibility = "hidden"; // We don't want to show that ugly "Choose file" button
        HtmlUtil.setChild(fileLabel, fileInput); // We embed it in a label, so clicking on the label triggers the file input (ie the file chooser)
        // By default, the element (ie fileLabel) is also the children container, so if we don't do anything special,
        // the WebFX mapper will reset the fileLabel children to an empty list (because FilePickerClickableRegion has
        // no JavaFX children). To avoid losing the fileInput in this way, we declare a separate children container.
        setChildrenContainer(HtmlUtil.createSpanElement()); // Dummy children container preventing losing fileInput
        fileInput.addEventListener("change", e -> {
            FileList webFiles = fileInput.files;
            List<dev.webfx.platform.file.File> webFXFiles = new ArrayList<>(webFiles.length);
            for (int i = 0; i < webFiles.length; i++)
                webFXFiles.add(dev.webfx.platform.file.File.create(webFiles.getAt(i)));
            getNode().getFileChooser().getSelectedFiles().setAll(webFXFiles);
            // Resetting the input value to null, otherwise this listener won't be called again if the user opens the
            // file picker again and chooses the same file(s)
            fileInput.value = null;
        });
    }

    @Override
    public void bind(N node, SceneRequester sceneRequester) {
        super.bind(node, sceneRequester);
        // Binding fileInput.multiple with fileChooser.multipleProperty()
        FXProperties.runNowAndOnPropertyChange(multiple -> fileInput.multiple = multiple, node.getFileChooser().multipleProperty());
        ObservableList<String> acceptedExtensions = node.getFileChooser().getAcceptedExtensions();
        ObservableLists.runNowAndOnListChange(obs ->
            fileInput.accept = String.join(",", acceptedExtensions)
        , acceptedExtensions);
    }
}
