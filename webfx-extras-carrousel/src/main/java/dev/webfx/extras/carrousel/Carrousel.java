package dev.webfx.extras.carrousel;

import dev.webfx.extras.panes.TransitionPane;
import dev.webfx.kit.util.properties.ObservableLists;
import dev.webfx.platform.util.Arrays;
import dev.webfx.platform.util.collection.Collections;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * @author Bruno Salmon
 */
public final class Carrousel {

    private final ObservableList<Supplier<Node>> slideSuppliers = FXCollections.observableArrayList();
    private final ObservableList<Circle> dots = ObservableLists.map(slideSuppliers, n -> createDot());
    private int displayedSlideIndex;
    private boolean loop = true;
    private final TransitionPane transitionPane = new TransitionPane();
    private final HBox dotsBox = new HBox(10);
    private final BorderPane container = new BorderPane(transitionPane);

    public Carrousel() {
        transitionPane.setMaxWidth(Double.MAX_VALUE);
        dotsBox.setPadding(new Insets(10));
        dotsBox.setAlignment(Pos.CENTER);
        dotsBox.getStyleClass().add("dots");
        ObservableLists.bind(dotsBox.getChildren(), dots);
        container.setBottom(dotsBox);
        dotsBox.setOnMouseClicked(e -> {
            if (e.getX() < dotsBox.getWidth() / 2)
                moveBackward();
            else
                moveForward();
        });
        ObservableLists.runOnListChange(change -> displaySlide(displayedSlideIndex), slideSuppliers);
    }

    public ObservableList<Supplier<Node>> getSlideSuppliers() {
        return slideSuppliers;
    }

    public void setSlideSuppliers(Collection<Supplier<Node>> slideSuppliers) {
        this.slideSuppliers.setAll(slideSuppliers);
    }

    public void setSlideSuppliers(Supplier<Node>... slideSuppliers) {
        this.slideSuppliers.setAll(slideSuppliers);
    }

    public void setSlides(Collection<Node> slides) {
        setSlideSuppliers(Collections.map(slides, slide -> () -> slide));
    }

    public void setSlides(Node... slides) {
        setSlideSuppliers(Arrays.map(slides, slide -> () -> slide, Supplier[]::new));
    }

    public Region getContainer() {
        return container;
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public void moveForward() {
        displaySlide(displayedSlideIndex + 1);
    }

    public void moveBackward() {
        displaySlide(displayedSlideIndex - 1);
    }
    public void displaySlide(int index) {
        displaySlide(index, true);
    }

    public void displaySlide(int index, boolean animate) {
        transitionPane.setDirection(index > displayedSlideIndex ? HPos.LEFT : HPos.RIGHT);
        int size = slideSuppliers.size();
        // index correction when it's out of the slides range (i.e. < 0 or >= size)
        if (loop)
            index = (index + size) % size;
        else {

            if (index < 0)
                index = 0;
            else if (index >= size)
                index = size - 1;
        }
        if (index != displayedSlideIndex || transitionPane.getContent() == null && !isTransiting()) {
            displayedSlideIndex = index;
            Node content = slideSuppliers.get(index).get();
            transitionPane.setAnimate(animate);
            transitionPane.setContent(content);
        }
        updateDotsStyleClass();
    }

    public boolean isTransiting() {
        return transitionPane.isTransiting();
    }

    public ReadOnlyBooleanProperty transitingProperty() {
        return transitionPane.transitingProperty();
    }

    public Node getDisplayedSlide() {
        return transitionPane.getContent();
    }

    public Pos getAlignment() {
        return transitionPane.getAlignment();
    }

    public ObjectProperty<Pos> alignmentProperty() {
        return transitionPane.alignmentProperty();
    }

    public void setAlignment(Pos alignment) {
        transitionPane.setAlignment(alignment);
    }

    private Circle createDot() {
        Circle dot = new Circle(5);
        dot.setOnMouseClicked(e -> {
            int index = dots.indexOf(dot);
            displaySlide(index);
            e.consume();
        });
        Platform.runLater(this::updateDotsStyleClass);
        return dot;
    }

    private void updateDotsStyleClass() {
        for (int i = 0; i < dots.size(); i++) {
            boolean isSelected = i == displayedSlideIndex;
            // Temporarily hardcoded (to replace with CSS)
            dots.get(i).setFill(isSelected ? Color.WHITE : Color.GRAY);
            //dots.get(i).getStyleClass().setAll(isSelected ? "selected" : "unselected");
        }
    }

}
