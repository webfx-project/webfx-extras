package dev.webfx.extras.responsive;

import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.kit.util.properties.Unregisterable;
import dev.webfx.platform.util.Arrays;
import dev.webfx.platform.util.Objects;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Region;


/**
 * @author Bruno Salmon
 */
public final class ResponsiveDesign {

    public static Unregisterable startResponsiveDesign(Region responsiveRegion, ResponsiveLayout... responsiveLayouts) {
        return startResponsiveDesign(responsiveRegion.widthProperty(), responsiveLayouts);
    }

    public static Unregisterable startResponsiveDesign(ObservableDoubleValue responsiveWidthProperty, ResponsiveLayout... responsiveLayouts) {
        ObservableValue[] dependencies = Arrays.flatMap(responsiveLayouts, ResponsiveLayout::getResponsiveDependencies, ObservableValue[]::new);
        dependencies = Arrays.filter(dependencies, Objects::nonNull, ObservableValue[]::new);
        dependencies = Arrays.add(ObservableValue[]::new, dependencies, responsiveWidthProperty);
        ResponsiveLayout[] activeResponsiveLayouts = { null };
        return FXProperties.runNowAndOnPropertiesChange(() -> {
            double responsiveWidth = responsiveWidthProperty.get();
            ResponsiveLayout bestResponsiveLayout = null;
            for (ResponsiveLayout responsiveLayout : responsiveLayouts) {
                if (responsiveLayout instanceof MinWidthResponsiveLayout layout) {
                    if (responsiveWidth >= layout.getResponsiveMinWidth())
                        bestResponsiveLayout = responsiveLayout;
                } else if (responsiveLayout instanceof ApplicableResponsiveLayout layout) {
                    if (layout.isResponsiveLayoutApplicable())
                        bestResponsiveLayout = responsiveLayout;
                } else
                    bestResponsiveLayout = responsiveLayout;
                if (bestResponsiveLayout != null)
                    break;
            }
            if (bestResponsiveLayout != null && bestResponsiveLayout != activeResponsiveLayouts[0]) {
                activeResponsiveLayouts[0] = bestResponsiveLayout;
                bestResponsiveLayout.applyResponsiveLayout();
            }
        }, dependencies);
    }
}
