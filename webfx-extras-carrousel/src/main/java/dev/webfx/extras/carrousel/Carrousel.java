package dev.webfx.extras.carrousel;

import dev.webfx.extras.panes.TransitionPane;
import dev.webfx.kit.util.properties.ObservableLists;
import dev.webfx.platform.util.Arrays;
import dev.webfx.platform.util.collection.Collections;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
    private final BooleanProperty loopProperty = new SimpleBooleanProperty(true);
    private final BooleanProperty showingDotsProperty = new SimpleBooleanProperty(true) {
        @Override
        protected void invalidated() {
            dotsBox.setVisible(get());
            dotsBox.setManaged(get());
        }
    };
    private final TransitionPane transitionPane = new TransitionPane();
    private final HBox dotsBox = new HBox(10);
    private final BorderPane container = new BorderPane(transitionPane);
    private int displayedSlideIndex;

    public Carrousel() {
        transitionPane.setMaxWidth(Double.MAX_VALUE);
        // Don't know if it's specific to Modality front-office, but we need to set the Alignment to TOP_CENTER because
        // otherwise the GoldenRatioPane are moved down when transiting from or to a node with a bigger height.
        transitionPane.setAlignment(Pos.TOP_CENTER);
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
        return loopProperty.get();
    }

    public BooleanProperty loopProperty() {
        return loopProperty;
    }

    public void setLoop(boolean loop) {
        loopProperty.set(loop);
    }

    public boolean isShowingDots() {
        return showingDotsProperty.get();
    }

    public BooleanProperty showingDotsProperty() {
        return showingDotsProperty;
    }

    public void setShowingDots(boolean showingDots) {
        showingDotsProperty.set(showingDots);
    }

    public void moveForward() {
        displaySlide(displayedSlideIndex + 1);
    }

    public void moveBackward() {
        displaySlide(displayedSlideIndex - 1);
    }

    public void displaySlide(Node slide) {
        displaySlide(slide, true);
    }

    public void displaySlide(Node slide, boolean animate) {
        for (int index = 0; index < slideSuppliers.size(); index++) {
            if (slideSuppliers.get(index).get() == slide) {
                displaySlide(index, animate);
                break;
            }
        }
    }

    public void displaySlide(int index) {
        displaySlide(index, true);
    }

    public void displaySlide(int index, boolean animate) {
        int size = slideSuppliers.size();
        if (size == 0)
            return;
        transitionPane.setDirection(index > displayedSlideIndex ? HPos.LEFT : HPos.RIGHT);
        // index correction when it's out of the slides range (i.e. < 0 or >= size)
        if (isLoop())
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
            // Don't know if it's specific to Modality front-office, but we need to manage the min & max height to have
            // the content (a GoldRatioPane in this case) correctly positioned during the transition.
            if (content instanceof Region) {
                Region region = (Region) content;
                // If the application code set a minHeight on the Carrousel (actually its container) such as setting it
                // to the ScrollPane height in Modality front-office, we propagate this constraint to the content (the
                // GoldenRatioPane in this case).
                region.minHeightProperty().bind(container.minHeightProperty());
                // Also if the leaving node if higher, it could make the GoldenRatioPane higher than needed (moving down
                // its content during the transition). To avoid this, we set the Max height to the preferred height.
                region.setMaxHeight(Region.USE_PREF_SIZE);
            }
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
