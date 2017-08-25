package org.shipkit.internal.gradle.plugin.tasks;

import org.gradle.api.Project;
import org.gradle.api.file.FileTree;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.util.PatternSet;
import org.shipkit.internal.gradle.util.JavaPluginUtil;
import org.shipkit.internal.util.PropertiesUtil;

import java.io.File;
import java.util.Properties;
import java.util.Set;

class PluginUtil {
    static final String DOT_PROPERTIES = ".properties";

    static Set<File> discoverGradlePluginPropertyFiles(Project project) {
        return discoverGradlePluginPropertyFiles(JavaPluginUtil.getMainSourceSet(project));
    }

    static Set<File> discoverGradlePluginPropertyFiles(SourceSet sourceSet) {
        return getFilteredFileset(sourceSet.getResources(), "META-INF/gradle-plugins/*" + DOT_PROPERTIES);
    }

    private static Set<File> getFilteredFileset(FileTree fileTree, String... includes) {
        return fileTree.matching(new PatternSet().include(includes)).getFiles();
    }

    static String getImplementationClass(File file) {
        Properties properties = PropertiesUtil.readProperties(file);
        return properties.getProperty("implementation-class");
    }
}
