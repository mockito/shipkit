package org.shipkit.internal.util;

public interface ResultHandler<T> {
    void onSuccess(T result);
    void onFailure(RuntimeException e);
}
