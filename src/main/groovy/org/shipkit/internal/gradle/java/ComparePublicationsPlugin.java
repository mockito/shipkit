package org.shipkit.internal.gradle.java;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.bundling.Jar;
import org.shipkit.gradle.ReleaseConfiguration;
import org.shipkit.internal.comparison.DownloadPreviousReleaseArtifactsTask;
import org.shipkit.internal.comparison.PublicationsComparatorTask;
import org.shipkit.internal.comparison.artifact.DefaultArtifactUrlResolver;
import org.shipkit.internal.comparison.artifact.DefaultArtifactUrlResolverFactory;
import org.shipkit.internal.gradle.configuration.DeferredConfiguration;
import org.shipkit.internal.gradle.configuration.ReleaseConfigurationPlugin;
import org.shipkit.internal.gradle.util.TaskMaker;

import java.io.File;

/**
 * Comparing current publications with previous release.
 * Intended for submodule.
 * <p>
 * Applies:
 *
 * <ul>
 *     <li>{@link JavaPublishPlugin}</li>
 * </ul>
 *
 * Adds following tasks:
 *
 * <ul>
 *     <li>downloadPreviousReleaseArtifacts</li>
 *     <li>comparePublications</li>
 * </ul>
 */
public class ComparePublicationsPlugin implements Plugin<Project> {

    private static final Logger LOG = Logging.getLogger(ComparePublicationsPlugin.class);

    final static String DOWNLOAD_PREVIOUS_RELEASE_ARTIFACTS_TASK = "downloadPreviousReleaseArtifacts";
    public final static String COMPARE_PUBLICATIONS_TASK = "comparePublications";

    final static String PREVIOUS_RELEASE_ARTIFACTS_DIR = "/previous-release-artifacts";

    @Override
    public void apply(final Project project) {
        project.getPlugins().apply(JavaPublishPlugin.class);
        final ReleaseConfiguration conf = project.getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();

        final Jar sourcesJar = (Jar) project.getTasks().getByName(JavaLibraryPlugin.SOURCES_JAR_TASK);

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
                t.comparePom(JavaPublishPlugin.POM_TASK);

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
            /*
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