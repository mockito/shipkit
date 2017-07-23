package org.shipkit.internal.gradle.plugin;

import org.gradle.api.Project;
import org.gradle.api.file.FileTree;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.util.PatternSet;

import java.io.File;
import java.util.Set;

public class PluginUtil {
    static final String DOT_PROPERTIES = ".properties";

    static Set<File> discoverGradlePluginPropertyFiles(Project project) {
        FileTree resources = getMainSourceSet(project).getResources();
        FileTree plugins = resources.matching(new PatternSet().include("META-INF/gradle-plugins/*" + DOT_PROPERTIES));
        return plugins.getFiles();
    }

    static Set<File> discoverGradlePlugins(Project project) {
        FileTree allJava = getMainSourceSet(project).getAllJava();
        FileTree plugins = allJava.matching(new PatternSet().include("**/*Plugin.java").include("**/*Plugin.groovy"));
        return plugins.getFiles();
    }

    private static SourceSet getMainSourceSet(Project project) {
        final JavaPluginConvention java = project.getConvention().getPlugin(JavaPluginConvention.class);
        return java.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
    }
}
