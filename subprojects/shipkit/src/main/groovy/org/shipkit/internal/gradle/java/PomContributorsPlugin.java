package org.shipkit.internal.gradle.java;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.shipkit.internal.gradle.bintray.ShipkitBintrayPlugin;
import org.shipkit.internal.gradle.contributors.github.GitHubContributorsPlugin;

import static org.shipkit.internal.gradle.java.JavaPublishPlugin.POM_TASK;
import static org.shipkit.internal.gradle.util.Specs.withName;

/**
 * Ensuring contributors are listed in pom file.
 * Intended to be applied to the root project of your Gradle multi-project build.
 * <p>
 * Applies following plugins:
 * <ul>
 *     <li>{@link GitHubContributorsPlugin}</li>
 * </ul>
 * Other features:
 * <ul>
 *     <li>Injects configuration to all projects that have {@link JavaBintrayPlugin} plugin.
 *     Makes sure that the task that generates pom file will have the contributors data generated first.
 *     All contributors are listed in the pom file.
 *     </li>
 *     <li>
 *     Fetching contributors only occurs if we're publishing to Bintray. For normal builds like './gradlew build',
 *     we don't fetch contributors. This way the builds are faster and can run offline.
 *     </li>
 * </ul>
 */
public class PomContributorsPlugin implements Plugin<Project> {

    @Override
    public void apply(final Project project) {
        project.getPlugins().apply(GitHubContributorsPlugin.class);
        final Task fetcher = project.getTasks().getByName(GitHubContributorsPlugin.FETCH_CONTRIBUTORS);

        project.allprojects(subproject ->
            subproject.getPlugins().withType(JavaBintrayPlugin.class, plugin -> {
                //Because maven-publish plugin uses new configuration model, we cannot get the task directly
                //So we use 'matching' technique.
                subproject.getTasks().matching(withName(POM_TASK)).all(t -> t.mustRunAfter(fetcher));

                //Pom task needs data from fetcher hence 'mustRunAfter' above.
                //We don't use 'dependsOn' because we want the fetcher to be included only when we are publishing to Bintray
                Task upload = subproject.getTasks().getByName(ShipkitBintrayPlugin.BINTRAY_UPLOAD_TASK);
                upload.dependsOn(fetcher);
            }));
    }
}
