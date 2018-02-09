package org.shipkit.internal.gradle.util;

import org.shipkit.internal.util.ArgumentValidation;

import java.util.NoSuchElementException;

/**
 * We need to migrate to Java8 and remove this.
 */
public class Optional<T> {

    private final T value;

    private Optional(T value) {
        this.value = value;
    }

    public T get() {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    public static <T> Optional<T> of(T object) {
        ArgumentValidation.notNull();
        return new Optional<T>(object);
    }

    public static <T> Optional<T> ofNullable(T value) {
        return new Optional<T>(value);
    }

    public boolean isPresent() {
        return value != null;
    }
}
