package org.shipkit.internal.gradle.java;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.shipkit.internal.gradle.contributors.ContributorsPlugin;

import static org.shipkit.internal.gradle.contributors.ContributorsPlugin.FETCH_ALL_CONTRIBUTORS_TASK;
import static org.shipkit.internal.gradle.java.JavaPublishPlugin.POM_TASK;
import static org.shipkit.internal.gradle.util.Specs.withName;

/**
 * Ensuring contributors are listed in pom file.
 * Intended to be applied to the root project of your Gradle multi-project build.
 * <p>
 * Applies following plugins:
 * <ul>
 *     <li>{@link ContributorsPlugin}</li>
 * </ul>
 *
 * Other features:
 * <ul>
 *     <li>Injects configuration to all projects that have {@link JavaPublishPlugin} plugin.
 *     Makes sure that the task that generates pom file has dependency on task that fetches all contributors.
 *     All contributors are listed in the pom file.
 *     </li>
 * </ul>
 */
public abstract class PomContributorsPlugin implements Plugin<Project> {

    abstract void applyContributorsPlugin(Project project);

    @Override
    public void apply(final Project project) {
        applyContributorsPlugin(project);

        project.allprojects(new Action<Project>() {
            public void execute(final Project subproject) {
                subproject.getPlugins().withType(JavaPublishPlugin.class, new Action<Plugin>() {
                    @Override
                    public void execute(Plugin plugin) {
                        final Task fetcher = project.getTasks().getByName(FETCH_ALL_CONTRIBUTORS_TASK);
                        //Because maven-publish plugin uses new configuration model, we cannot get the task directly
                        //So we use 'matching' technique
                        subproject.getTasks().matching(withName(POM_TASK)).all(new Action<Task>() {
                            public void execute(Task t) {
                                t.dependsOn(fetcher);
                            }
                        });
                    }
                });
            }
        });
    }
}
