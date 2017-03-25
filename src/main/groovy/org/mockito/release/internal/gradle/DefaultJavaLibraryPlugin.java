package org.mockito.release.internal.gradle;

import org.gradle.api.Project;
import org.mockito.release.gradle.JavaLibraryPlugin;

public class DefaultJavaLibraryPlugin implements JavaLibraryPlugin {

    public void apply(Project project) {
        project.getPlugins().apply("org.mockito.mockito-release-tools.base-java-library");
        project.getPlugins().apply("org.mockito.mockito-release-tools.bintray");
    }
}
