package org.shipkit.internal.gradle.util;

import org.gradle.api.GradleException;
import org.gradle.api.Project;

import java.util.Optional;

public class ProjectUtil {

    public static void requireRootProject(final Project project, final Class<?> clazz) {
        requireRootProject(project, clazz, Optional.empty());
    }

    public static void requireRootProject(final Project project, final Class<?> clazz, final Optional<String> explanation) {
        if (project.getParent() != null) {
            throw new GradleException("Plugin '" + clazz.getSimpleName() + "' is intended to be applied only root project.\n" +
                (explanation.isPresent() ? explanation.get() + "\n" : "" ) +
                "Please apply this plugin to the root project instead of '" + project.getPath() + "'.");
        }
    }
}
