package org.shipkit.internal.notes.util;

// TODO remove once we migrate to java8
/**
 * Generic predicate
 */
public interface Predicate<T> {

    /**
     * returns true if the predicate is satisfied for given object
     */
    boolean isTrue(T object);
}
