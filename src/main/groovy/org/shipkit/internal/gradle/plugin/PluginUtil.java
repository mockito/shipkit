package org.shipkit.internal.gradle.plugin;

import org.gradle.api.Project;
import org.gradle.api.file.FileTree;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.util.PatternSet;

import java.io.File;
import java.util.Set;

public class PluginUtil {
    static Set<File> discoverGradlePluginPropertyFiles(Project project) {
        final JavaPluginConvention java = project.getConvention().getPlugin(JavaPluginConvention.class);
        FileTree resources = java.getSourceSets().getByName("main").getResources();
        FileTree plugins = resources.matching(new PatternSet().include("META-INF/gradle-plugins/*.properties"));
        return plugins.getFiles();
    }

    static Set<File> discoverGradlePlugins(Project project) {
        final JavaPluginConvention java = project.getConvention().getPlugin(JavaPluginConvention.class);
        FileTree resources = java.getSourceSets().getByName("main").getAllJava();
        FileTree plugins = resources.matching(new PatternSet().include("**/*Plugin.java").include("**/*Plugin.groovy"));
        return plugins.getFiles();
    }
}
