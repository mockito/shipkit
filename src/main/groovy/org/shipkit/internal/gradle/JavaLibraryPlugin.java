package org.shipkit.internal.gradle;

import com.jfrog.bintray.gradle.BintrayExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import static org.shipkit.internal.gradle.BaseJavaLibraryPlugin.PUBLICATION_NAME;

/**
 * Intended to be applied in individual Java submodule.
 * Applies following plugins and configures java publications for Bintray plugin:
 *
 * <ul>
 *     <li>{@link JavaLibraryPlugin}</li>
 *     <li>{@link BintrayPlugin}</li>
 * </ul>
 */
public class JavaLibraryPlugin implements Plugin<Project> {

    public void apply(Project project) {
        project.getPlugins().apply(BaseJavaLibraryPlugin.class);
        project.getPlugins().apply(BintrayPlugin.class);

        if (shouldConfigurePublications(project)) {
            BintrayExtension bintray = project.getExtensions().getByType(BintrayExtension.class);
            bintray.setPublications(PUBLICATION_NAME);
        }
    }

    private boolean shouldConfigurePublications(Project project) {
        //Sanity system property. Semi-internal.
        boolean workaroundTurnedOff = "false".equals(System.getProperty("org.mockito.shipkit.publications-bug-workaround"));
        if (workaroundTurnedOff) {
            return true;
        }
        //Workaround for bintray plugin/Gradle bug (https://github.com/bintray/gradle-bintray-plugin/issues/159)
        return !project.getGradle().getStartParameter().getTaskNames().contains("tasks");
    }
}
