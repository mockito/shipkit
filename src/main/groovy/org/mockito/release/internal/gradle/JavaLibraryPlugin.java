package org.mockito.release.internal.gradle;

import com.jfrog.bintray.gradle.BintrayExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import static org.mockito.release.internal.gradle.BaseJavaLibraryPlugin.PUBLICATION_NAME;

/**
 * Intended to be applied in individual Java submodule. Applies following plugins:
 *
 * <ul>
 *     <li>org.mockito.mockito-release-tools.java-library - see {@link JavaLibraryPlugin}</li>
 *     <li>org.mockito.mockito-release-tools.bintray - see {@link BintrayPlugin}</li>
 * </ul>
 */
public class JavaLibraryPlugin implements Plugin<Project> {

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
