package org.shipkit.internal.notes.util;

// TODO remove once we migrate to java8
public interface Function<T, R> {

    R apply(T t);
}
