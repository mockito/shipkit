package org.shipkit.internal.gradle.java;

import com.jfrog.bintray.gradle.BintrayExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.internal.gradle.ShipkitBintrayPlugin;

import static org.shipkit.internal.gradle.java.JavaPublishPlugin.PUBLICATION_NAME;

/**
 * Publishing java library using Bintray.
 * Intended to be applied in individual Java submodule.
 * Applies following plugins and configures java publications for Bintray plugin:
 *
 * <ul>
 *     <li>{@link JavaPublishPlugin}</li>
 *     <li>{@link ShipkitBintrayPlugin}</li>
 * </ul>
 */
public class JavaBintrayPlugin implements Plugin<Project> {

    public void apply(Project project) {
        project.getPlugins().apply(JavaPublishPlugin.class);
        project.getPlugins().apply(ShipkitBintrayPlugin.class);
        project.getPlugins().apply(PublicationsComparatorPlugin.class);

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
