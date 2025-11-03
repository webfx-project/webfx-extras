package dev.webfx.extras.operation.action;

import dev.webfx.platform.async.AsyncFunction;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;

import java.util.function.Function;

/**
 * @author Bruno Salmon
 */
public interface OperationActionAuthorizer {

    <OA, Rq> ObservableBooleanValue authorizedOperationProperty(
        ObservableValue<OA> operationActionProperty,
        Function<OA, Rq> operationRequestFactory,
        AsyncFunction<Rq, Boolean> authorizationFunction
    );

}
