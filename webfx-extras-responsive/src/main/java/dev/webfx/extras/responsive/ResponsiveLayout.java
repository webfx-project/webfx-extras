package dev.webfx.extras.responsive;

import javafx.beans.value.ObservableValue;

import java.util.function.Function;

/**
 * @author Bruno Salmon
 */
public interface ResponsiveLayout {

    default boolean testResponsiveLayoutApplicability(double width) {
        return true;
    }

    void applyResponsiveLayout();

    default ObservableValue<?>[] getResponsiveTestDependencies() {
        return new ObservableValue[0];
    }

    static ResponsiveLayout create(Function<Double, Boolean> applicabilityTestFunction, Runnable applyMethod, ObservableValue<?>... testDependencies) {
        return new ResponsiveLayout() {
            @Override
            public boolean testResponsiveLayoutApplicability(double width) {
                return applicabilityTestFunction.apply(width);
            }

            @Override
            public void applyResponsiveLayout() {
                applyMethod.run();
            }

            @Override
            public ObservableValue<?>[] getResponsiveTestDependencies() {
                return testDependencies;
            }
        };
    }

}
