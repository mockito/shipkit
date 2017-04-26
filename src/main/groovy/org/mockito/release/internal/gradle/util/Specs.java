package org.mockito.release.internal.gradle.util;

import org.gradle.api.Task;
import org.gradle.api.specs.Spec;

/**
 * Helper methods for Gradle Spec objects
 */
public class Specs {

    /**
     * Spec satisfied by task that matches provided name
     */
    public static Spec<Task> withName(final String name) {
        return new Spec<Task>() {
            @Override
            public boolean isSatisfiedBy(Task t) {
                return t.getName().equals(name);
            }
        };
    }
}
