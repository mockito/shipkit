package org.shipkit.internal.gradle.util;


import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;

public class JavaPluginUtil {

    public static SourceSet getMainSourceSet(Project project) {
        final JavaPluginConvention java = project.getConvention().getPlugin(JavaPluginConvention.class);
        return java.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
    }
}
