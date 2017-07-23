package org.shipkit.internal.gradle.plugin;

import org.gradle.api.Project;
import org.gradle.api.file.FileTree;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.util.PatternSet;

import java.io.File;
import java.util.Set;

class PluginUtil {
    static final String DOT_PROPERTIES = ".properties";

    static Set<File> discoverGradlePluginPropertyFiles(Project project) {
        FileTree resources = getMainSourceSet(project).getResources();
        return getFilteredFileset(resources, "META-INF/gradle-plugins/*" + DOT_PROPERTIES);
    }

    static Set<File> discoverGradlePlugins(Project project) {
        FileTree allJava = getMainSourceSet(project).getAllJava();
        return getFilteredFileset(allJava, "**/*Plugin.java", "**/*Plugin.groovy");
    }

    private static SourceSet getMainSourceSet(Project project) {
        final JavaPluginConvention java = project.getConvention().getPlugin(JavaPluginConvention.class);
        return java.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
    }

    private static Set<File> getFilteredFileset(FileTree fileTree, String... includes) {
        return fileTree.matching(new PatternSet().include(includes)).getFiles();
    }
}
