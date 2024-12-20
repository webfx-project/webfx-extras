package dev.webfx.extras.filepicker.spi.impl;

import dev.webfx.extras.filepicker.FilePicker;
import dev.webfx.kit.util.properties.FXProperties;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 * @author Bruno Salmon
 */
public abstract class BaseFilePicker extends FilePicker {

    protected final FilePickerClickableRegion filePickerClickableRegion;
    protected final StackPane view;


    public BaseFilePicker() {
        filePickerClickableRegion = new FilePickerClickableRegion(this);
        view = new StackPane() {
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                Node graphic = getGraphic();
                if (graphic != null) {
                    Bounds graphicBounds = graphic.getLayoutBounds();
                    Pos alignment = getAlignment();
                    if (alignment == null)
                        alignment = Pos.CENTER;
                    layoutInArea(filePickerClickableRegion,
                            graphic.getLayoutX() + graphicBounds.getMinX(),
                            graphic.getLayoutY() + graphicBounds.getMinY(),
                            graphicBounds.getWidth(),
                            graphicBounds.getHeight(),
                            0,
                            alignment.getHpos(),
                            alignment.getVpos());
                }
            }
        };
        view.getStyleClass().add("file-picker");
        FXProperties.runNowAndOnPropertyChange(this::onGraphicChanged, graphicProperty());
    }

    private void onGraphicChanged() {
        //view.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));
        Node graphic = getGraphic();
        view.getChildren().setAll(filePickerClickableRegion);
        if (graphic != null) {
            graphic.setMouseTransparent(true);
            view.getChildren().add(graphic);
        }
        filePickerClickableRegion.setManaged(graphic == null);
    }

    @Override
    public Node getView() {
        return view;
    }

}
