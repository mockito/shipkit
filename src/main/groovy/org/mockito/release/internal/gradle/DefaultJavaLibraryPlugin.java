package org.mockito.release.internal.gradle;

import com.jfrog.bintray.gradle.BintrayExtension;
import org.gradle.api.Project;
import org.mockito.release.gradle.JavaLibraryPlugin;

import static org.mockito.release.internal.gradle.BaseJavaLibraryPlugin.PUBLICATION_NAME;

public class DefaultJavaLibraryPlugin implements JavaLibraryPlugin {

    public void apply(Project project) {
        project.getPlugins().apply("org.mockito.mockito-release-tools.base-java-library");
        project.getPlugins().apply("org.mockito.mockito-release-tools.bintray");

        if (shouldConfigurePublications(project)) {
            BintrayExtension bintray = project.getExtensions().getByType(BintrayExtension.class);
            bintray.setPublications(PUBLICATION_NAME);
        }
    }

    private boolean shouldConfigurePublications(Project project) {
        //Sanity system property. Semi-internal.
        boolean workaroundTurnedOff = "false".equals(System.getProperty("org.mockito.mockito-release-tools.publications-bug-workaround"));
        if (workaroundTurnedOff) {
            return true;
        }
        //Workaround for bintray plugin/Gradle bug (https://github.com/bintray/gradle-bintray-plugin/issues/159)
        return !project.getGradle().getStartParameter().getTaskNames().contains("tasks");
    }
}
