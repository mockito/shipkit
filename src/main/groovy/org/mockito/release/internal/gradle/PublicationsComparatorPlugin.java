package org.mockito.release.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.bundling.Jar;
import org.mockito.release.gradle.ReleaseConfiguration;
import org.mockito.release.internal.comparison.DownloadPreviousReleaseArtifactsTask;
import org.mockito.release.internal.comparison.PublicationsComparatorTask;
import org.mockito.release.internal.comparison.artifact.DefaultArtifactUrlResolver;
import org.mockito.release.internal.comparison.artifact.DefaultArtifactUrlResolverFactory;
import org.mockito.release.internal.gradle.configuration.DeferredConfiguration;
import org.mockito.release.internal.gradle.util.TaskMaker;

import java.io.File;

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
 *     <li>downloadPreviousReleaseArtifacts</li>
 *     <li>comparePublications</li>
 * </ul>
 */
public class PublicationsComparatorPlugin implements Plugin<Project> {

    private static final Logger LOG = Logging.getLogger(PublicationsComparatorPlugin.class);

    final static String DOWNLOAD_PREVIOUS_RELEASE_ARTIFACTS_TASK = "downloadPreviousReleaseArtifacts";
    final static String COMPARE_PUBLICATIONS_TASK = "comparePublications";

    final static String PREVIOUS_RELEASE_ARTIFACTS_DIR = "/previous-release-artifacts";

    @Override
    public void apply(final Project project) {
        project.getPlugins().apply(BaseJavaLibraryPlugin.class);
        final ReleaseConfiguration conf = project.getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();

        final Jar sourcesJar = (Jar) project.getTasks().getByName(BaseJavaLibraryPlugin.SOURCES_JAR_TASK);

        String basePreviousVersionArtifactPath = getBasePreviousVersionArtifactPath(project, conf, sourcesJar);
        final File previousVersionPomLocalFile = new File(basePreviousVersionArtifactPath + ".pom");
        final File previousVersionSourcesJarLocalFile = new File(basePreviousVersionArtifactPath + "-sources.jar");

        TaskMaker.task(project, DOWNLOAD_PREVIOUS_RELEASE_ARTIFACTS_TASK, DownloadPreviousReleaseArtifactsTask.class, new Action<DownloadPreviousReleaseArtifactsTask>() {
            @Override
            public void execute(final DownloadPreviousReleaseArtifactsTask t) {
                t.setDescription("Downloads artifacts of last released version and stores it locally for comparison");

                DeferredConfiguration.deferredConfiguration(project, new Runnable() {
                    @Override
                    public void run() {
                        DefaultArtifactUrlResolver artifactUrlResolver =
                                new DefaultArtifactUrlResolverFactory().getDefaultResolver(project, sourcesJar.getBaseName(), conf.getPreviousReleaseVersion());

                        String previousVersionPomUrl = getDefaultIfNull(t.getPreviousVersionPomUrl(), "previousVersionPomUrl", ".pom", artifactUrlResolver);
                        t.setPreviousVersionPomUrl(previousVersionPomUrl);
                        String previousVersionSourcesJarUrl = getDefaultIfNull(t.getPreviousVersionSourcesJarUrl(), "previousSourcesJarUrl", "-sources.jar", artifactUrlResolver);
                        t.setPreviousVersionSourcesJarUrl(previousVersionSourcesJarUrl);

                        t.setPreviousVersionPomLocalFile(previousVersionPomLocalFile);
                        t.setPreviousVersionSourcesJarLocalFile(previousVersionSourcesJarLocalFile);
                    }
                });
            }
        });

        /*
        TODO ww make this puppy incremental :)

        I suggest we split the functionality of this task into 2 separate tasks:
         - 1st gets the previously released files (we will make it incremental)
         - 2nd performs comparison (does not have to be incremental, it does not have download operation)

        I suggest that the JavaLibraryPlugin applies PublicationsComparatorPlugin because the former has access to Bintray extension

        It is more Gradle style (effective, incremental, easy to work with),
        when we divide operations into tasks and can pipe the inputs and outputs.
        See how we have done it in:
         - ContributorsPlugin: fetcher task + configurer task
         - ReleaseNotesPlugin: contributors fetcher + release notes fetcher + release notes builder

        Bonus (long term, Gradle craftsmanship :) - it would be great to divide the comparison operations even further and have:
         1. download previous releases (incremental)
         2. compare and produce comparison result object that we serialize to file or produce some 'diff' files (incremental)
         3. release needed task would deserialize results and read it or check for presence of 'diff' files (non-incremental)
        */
        TaskMaker.task(project, COMPARE_PUBLICATIONS_TASK, PublicationsComparatorTask.class, new Action<PublicationsComparatorTask>() {
            public void execute(final PublicationsComparatorTask t) {
                t.setDescription("Compares artifacts and poms between last version and the currently built one to see if there are any differences");

                t.dependsOn(DOWNLOAD_PREVIOUS_RELEASE_ARTIFACTS_TASK);

                t.setCurrentVersion(project.getVersion().toString());
                t.setPreviousVersion(conf.getPreviousReleaseVersion());
                t.setPreviousVersionPomFile(previousVersionPomLocalFile);
                t.setPreviousVersionSourcesJarFile(previousVersionSourcesJarLocalFile);

                //Set local sources jar for comparison with previously released
                t.compareSourcesJar(sourcesJar);

                //Set locally built pom file for comparison with previously released
                //maven-publish plugin is messed up in Gradle API, we cannot really access generate pom task and we have to pass String
                //The generate pom task is dynamically created by Gradle and we can only access it during execution
                t.comparePom(BaseJavaLibraryPlugin.POM_TASK);

                DeferredConfiguration.deferredConfiguration(project, new Runnable() {
                    @Override
                    public void run() {
                        t.setProjectGroup(project.getGroup().toString());
                    }
                });
            }
        });
    }

    private String getBasePreviousVersionArtifactPath(Project project, ReleaseConfiguration conf, Jar sourcesJar) {
        return project.getBuildDir().getAbsolutePath() + PREVIOUS_RELEASE_ARTIFACTS_DIR
                + File.separator + sourcesJar.getBaseName() + "-" + conf.getPreviousReleaseVersion();
    }

    private String getDefaultIfNull(String url, String variableName, String extension, DefaultArtifactUrlResolver defaultArtifactUrlResolver) {
        if(url == null){
            /**
             * it's null when {@link DefaultArtifactUrlResolverFactory} can't find any implementation suitable for the current implementation
             */
            if(defaultArtifactUrlResolver == null){
                return null;
            }
            String defaultUrl = defaultArtifactUrlResolver.getDefaultUrl(extension);
            LOG.info("Property {} of task {} not set. Setting it to default value - {}", variableName, COMPARE_PUBLICATIONS_TASK, defaultUrl);
            return defaultUrl;
        }
        return url;
    }
}
