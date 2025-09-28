package dev.webfx.extras.operation;

import dev.webfx.platform.async.AsyncFunction;
import dev.webfx.platform.async.Future;

/**
 * @author Bruno Salmon
 */
public final class OperationDirect {

    public static <Rq extends HasOperationExecutor, Rs> Future<Rs> executeOperation(Rq operationRequest) {
        return executeOperation(operationRequest, null);
    }

    public static <Rq, Rs> Future<Rs> executeOperation(Rq operationRequest, AsyncFunction<Rq, Rs> operationExecutor) {
        if (operationExecutor == null && operationRequest instanceof HasOperationExecutor)
            //noinspection unchecked
            operationExecutor = ((HasOperationExecutor<Rq, Rs>) operationRequest).getOperationExecutor();
        if (operationExecutor != null)
            return operationExecutor.apply(operationRequest);
        return Future.failedFuture(new IllegalArgumentException("No executor found for operation request " + operationRequest));
    }

}
