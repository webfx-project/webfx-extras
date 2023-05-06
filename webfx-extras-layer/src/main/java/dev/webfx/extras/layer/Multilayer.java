package dev.webfx.extras.layer;

import javafx.collections.ObservableList;

public interface Multilayer<L extends Layer<?>> {

    ObservableList<L> getLayers();

    void addLayer(L layer);

    void removeLayer(L layer);

}
