package org.shipkit.internal.gradle.plugin;

import org.gradle.api.Project;
import org.gradle.api.file.FileTree;
import org.gradle.api.tasks.util.PatternSet;
import org.shipkit.internal.gradle.util.JavaPluginUtil;

import java.io.File;
import java.util.Set;

class PluginUtil {
    static final String DOT_PROPERTIES = ".properties";

    static Set<File> discoverGradlePluginPropertyFiles(Project project) {
        FileTree resources = JavaPluginUtil.getMainSourceSet(project).getResources();
        return getFilteredFileset(resources, "META-INF/gradle-plugins/*" + DOT_PROPERTIES);
    }

    static Set<File> discoverGradlePlugins(Project project) {
        FileTree allJava = JavaPluginUtil.getMainSourceSet(project).getAllJava();
        return getFilteredFileset(allJava, "**/*Plugin.java", "**/*Plugin.groovy");
    }

    private static Set<File> getFilteredFileset(FileTree fileTree, String... includes) {
        return fileTree.matching(new PatternSet().include(includes)).getFiles();
    }
}
