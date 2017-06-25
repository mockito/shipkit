package org.shipkit.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

import static org.shipkit.internal.gradle.BaseJavaLibraryPlugin.POM_TASK;
import static org.shipkit.internal.gradle.ContributorsPlugin.FETCH_ALL_CONTRIBUTORS_TASK;
import static org.shipkit.internal.gradle.util.Specs.withName;

/**
 * Ensures that contributors are fetched from GitHub  for the pom file generation in all java subprojects
 */
public class PomContributorsPlugin implements Plugin<Project> {

    @Override
    public void apply(final Project project) {
        project.getPlugins().apply(ContributorsPlugin.class);

        project.allprojects(new Action<Project>() {
            @Override
            public void execute(final Project subproject) {
                subproject.getPlugins().withType(BaseJavaLibraryPlugin.class, new Action<BaseJavaLibraryPlugin>() {
                    @Override
                    public void execute(BaseJavaLibraryPlugin p) {
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
