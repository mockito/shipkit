package org.shipkit.internal.util;

public abstract class RethrowingResultHandler<T> implements ResultHandler<T> {
    @Override
    public void onFailure(RuntimeException e) {
        throw e;
    }
}
