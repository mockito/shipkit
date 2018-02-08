package org.shipkit.internal.gradle.util;

import java.util.NoSuchElementException;

/**
 * We need to migrate to Java8 and remove this.
 */
public class Optional<T> {

    private final T value;

    public Optional(T value) {
        this.value = value;
    }

    public T get() {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    public static <T> Optional<T> of(T object) {
        return new Optional<T>(object);
    }

    public boolean isPresent() {
        return value != null;
    }
}
