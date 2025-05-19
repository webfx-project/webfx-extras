package dev.webfx.extras.responsive;

import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.kit.util.properties.Unregisterable;
import dev.webfx.platform.util.Arrays;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Region;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


/**
 * @author Bruno Salmon
 */
public final class ResponsiveDesign {

    private final Region responsiveRegion;
    private final ObservableDoubleValue responsiveWidthProperty;
    private final List<ResponsiveLayout> responsiveLayouts = new ArrayList<>();
    private Unregisterable unregisterable;

    public ResponsiveDesign(Region responsiveRegion) {
        this.responsiveRegion = responsiveRegion;
        this.responsiveWidthProperty = responsiveRegion.widthProperty();
    }

    public ResponsiveDesign(ObservableDoubleValue responsiveWidthProperty) {
        this.responsiveRegion = null;
        this.responsiveWidthProperty = responsiveWidthProperty;
    }

    public ResponsiveDesign addResponsiveLayout(ResponsiveLayout responsiveLayout) {
        responsiveLayouts.add(responsiveLayout);
        return this;
    }

    public ResponsiveDesign addResponsiveLayout(Function<Double, Boolean> applicabilityTestFunction, Runnable applyMethod, ObservableValue<?>... testDependencies) {
        return addResponsiveLayout(ResponsiveLayout.create(applicabilityTestFunction, applyMethod, testDependencies));
    }

    public ResponsiveDesign start() {
        ObservableValue[] dependencies = Arrays.flatMap(responsiveLayouts.toArray(new ResponsiveLayout[0]), ResponsiveLayout::getResponsiveTestDependencies, ObservableValue[]::new);
        dependencies = Arrays.add(ObservableValue[]::new, dependencies, responsiveWidthProperty);
        ResponsiveLayout[] activeResponsiveLayouts = { null };
        unregisterable = FXProperties.runNowAndOnPropertiesChange(() -> {
            double responsiveWidth = responsiveWidthProperty.get();
            if (responsiveWidth <= 0)
                return;
            ResponsiveLayout bestResponsiveLayout = null;
            for (ResponsiveLayout responsiveLayout : responsiveLayouts) {
                if (responsiveLayout.testResponsiveLayoutApplicability(responsiveWidth)) {
                    bestResponsiveLayout = responsiveLayout;
                    break;
                }
            }
            if (bestResponsiveLayout != null && bestResponsiveLayout != activeResponsiveLayouts[0]) {
                activeResponsiveLayouts[0] = bestResponsiveLayout;
                bestResponsiveLayout.applyResponsiveLayout();
            }
        }, dependencies);
        return this;
    }

    public ResponsiveDesign stop() {
        if (unregisterable != null)
            unregisterable.unregister();
        return this;
    }
}
