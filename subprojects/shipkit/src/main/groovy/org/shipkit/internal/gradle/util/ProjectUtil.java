package org.shipkit.internal.gradle.util;

import org.gradle.api.GradleException;
import org.gradle.api.Project;

public class ProjectUtil {

    public static void requireRootProject(final Project project, final Class<?> clazz) {
        requireRootProject(project, clazz, null);
    }

    public static void requireRootProject(final Project project, final Class<?> clazz, final String explanation) {
        if (project.getParent() != null) {
            throw new GradleException("Plugin '" + clazz.getSimpleName() + "' is intended to be applied only root project.\n" +
                (explanation != null ? explanation + "\n" : "" ) +
                "Please apply this plugin to the root project instead of '" + project.getPath() + "'.");
        }
    }
}
