package dev.webfx.extras.action;

/**
 * @author Bruno Salmon
 */
public interface ActionFactoryMixin extends ActionFactory {

    default ActionBuilder newActionBuilder(Object actionKey) {
        return getActionFactory().newActionBuilder(actionKey);
    }

    default ActionFactory getActionFactory() {
        return ActionBuilderRegistry.get();
    }

}
