package dev.webfx.extras.i18n.operations;

import dev.webfx.extras.operation.HasOperationExecutor;
import dev.webfx.platform.async.AsyncFunction;

/**
 * @author Bruno Salmon
 */
public class ChangeLanguageRequest implements HasOperationExecutor<ChangeLanguageRequest, Void> {

    private final Object language;

    public ChangeLanguageRequest(Object language) {
        this.language = language;
    }

    public Object getLanguage() {
        return language;
    }

    @Override
    public AsyncFunction<ChangeLanguageRequest, Void> getOperationExecutor() {
        return ChangeLanguageExecutor::executeRequest;
    }
}
