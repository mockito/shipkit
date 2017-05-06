package org.mockito.release.internal.gradle.util;

import org.gradle.api.Task;
import org.gradle.api.specs.Spec;

import java.io.File;

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

    /**
     * Spec that checks if file exists using {@link File#isFile()} method.
     */
    public static Spec<Task> fileExists(final File file) {
        return new Spec<Task>() {
            @Override
            public boolean isSatisfiedBy(Task task) {
                return file.isFile();
            }
        };
    }
}
