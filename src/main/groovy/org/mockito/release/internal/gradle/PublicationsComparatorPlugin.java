package org.mockito.release.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.bundling.Jar;
import org.mockito.release.gradle.ReleaseConfiguration;
import org.mockito.release.internal.comparison.PublicationsComparatorTask;
import org.mockito.release.internal.comparison.artifact.DefaultArtifactUrlResolver;
import org.mockito.release.internal.comparison.artifact.DefaultArtifactUrlResolverFactory;
import org.mockito.release.internal.gradle.configuration.DeferredConfiguration;
import org.mockito.release.internal.gradle.util.TaskMaker;

/**
 * Opinionated continuous delivery plugin.
 * Applies following plugins and preconfigures tasks provided by those plugins:
 *
 * <ul>
 *     <li>{@link BaseJavaLibraryPlugin}</li>
 *     <li>{@link ReleaseConfigurationPlugin}</li>
 * </ul>
 *
 * Adds following tasks:
 *
 * <ul>
 *     <li>comparePublications</li>
 * </ul>
 */
public class PublicationsComparatorPlugin implements Plugin<Project> {

    final static String COMPARE_PUBLICATIONS_TASK = "comparePublications";

    @Override
    public void apply(final Project project) {
        project.getPlugins().apply(BaseJavaLibraryPlugin.class);
        final ReleaseConfiguration conf = project.getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();

        //TODO (big one). Figure out how to make this task incremental and avoid downloads each time it runs
        TaskMaker.task(project, COMPARE_PUBLICATIONS_TASK, PublicationsComparatorTask.class, new Action<PublicationsComparatorTask>() {
            public void execute(final PublicationsComparatorTask t) {
                t.setDescription("Compares artifacts and poms between last version and the currently built one to see if there are any differences");

                t.setCurrentVersion(project.getVersion().toString());
                t.setPreviousVersion(conf.getPreviousReleaseVersion());

                //Let's say that the initial implementation compares sources jar. We can this API method to the task:
                final Jar sourcesJar = (Jar) project.getTasks().getByName(BaseJavaLibraryPlugin.SOURCES_JAR_TASK);
                t.compareSourcesJar(sourcesJar);
                //Let's say we compare poms, we can add this API
                //maven-publish plugin is messed up in Gradle API, we cannot really access generate pom task and we have to pass String
                //The generate pom task is dynamically created by Gradle and we can only access it during execution
                t.comparePom(BaseJavaLibraryPlugin.POM_TASK);

                DeferredConfiguration.deferredConfiguration(project, new Runnable() {
                    @Override
                    public void run() {
                        t.setProjectGroup(project.getGroup().toString());
                        DefaultArtifactUrlResolver artifactUrlResolver =
                                new DefaultArtifactUrlResolverFactory().getDefaultResolver(project, sourcesJar.getBaseName(), conf.getPreviousReleaseVersion());
                        t.setDefaultArtifactUrlResolver(artifactUrlResolver);
                    }
                });
            }
        });
    }
}
