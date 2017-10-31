package org.shipkit.internal.gradle.java;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.bundling.Jar;
import org.shipkit.gradle.configuration.ShipkitConfiguration;
import org.shipkit.gradle.java.ComparePublicationsTask;
import org.shipkit.gradle.java.DownloadPreviousPublicationsTask;
import org.shipkit.internal.comparison.artifact.DefaultArtifactUrlResolver;
import org.shipkit.internal.comparison.artifact.DefaultArtifactUrlResolverFactory;
import org.shipkit.internal.gradle.configuration.DeferredConfiguration;
import org.shipkit.internal.gradle.configuration.ShipkitConfigurationPlugin;
import org.shipkit.internal.gradle.java.tasks.CreateDependencyInfoFileTask;
import org.shipkit.internal.gradle.util.TaskMaker;

import java.io.File;

/**
 * Comparing current publications with previous release.
 * Intended for submodule.
 * Adds createDependencyInfoFile task as a dependency of sourcesJar task.
 * This task creates a dependency-info.md file that contains all of declared dependencies of the project.
 * The file is included in META-INF directory of sources jar.
 * It is later used for comparing publications. Shipkit considers adding/changing version/removing a dependency
 * as an important change after which release is necessary.
 *
 * <p>
 * Applies:
 *
 * <ul>
 *     <li>{@link JavaPublishPlugin}</li>
 *     <li>{@link ShipkitConfigurationPlugin}</li>
 * </ul>
 *
 * Adds following tasks:
 *
 * <ul>
 *     <li>createDependencyInfoFile - {@link CreateDependencyInfoFileTask}</li>
 *     <li>downloadPreviousReleaseArtifacts - {@link DownloadPreviousPublicationsTask}</li>
 *     <li>comparePublications - {@link ComparePublicationsTask}</li>
 * </ul>
 */
public class ComparePublicationsPlugin implements Plugin<Project> {

    private static final Logger LOG = Logging.getLogger(ComparePublicationsPlugin.class);

    final static String DOWNLOAD_PUBLICATIONS_TASK = "downloadPreviousReleaseArtifacts";
    public final static String COMPARE_PUBLICATIONS_TASK = "comparePublications";

    final static String PREVIOUS_ARTIFACTS_DIR = "/previous-release-artifacts";

    @Override
    public void apply(final Project project) {
        project.getPlugins().apply(JavaPublishPlugin.class);
        final ShipkitConfiguration conf = project.getPlugins().apply(ShipkitConfigurationPlugin.class).getConfiguration();

        final Jar sourcesJar = (Jar) project.getTasks().getByName(JavaLibraryPlugin.SOURCES_JAR_TASK);

        String basePreviousVersionArtifactPath = getBasePreviousVersionArtifactPath(project, conf, sourcesJar);
        final File previousPom = new File(basePreviousVersionArtifactPath + ".pom");
        final File previousSourcesJar = new File(basePreviousVersionArtifactPath + "-sources.jar");

        final CreateDependencyInfoFileTask dependencyInfoTask = TaskMaker.task(project, "createDependencyInfoFile", CreateDependencyInfoFileTask.class, new Action<CreateDependencyInfoFileTask>() {
            @Override
            public void execute(final CreateDependencyInfoFileTask task) {
                task.setDescription("Creates a file with all declared runtime dependencies.");
                task.setOutputFile(new File(project.getBuildDir(), "dependency-info.md"));
                task.setConfiguration(project.getConfigurations().getByName("runtime"));
                task.setProjectVersion(project.getVersion().toString());

                DeferredConfiguration.deferredConfiguration(project, new Runnable() {
                    @Override
                    public void run() {
                        task.setProjectGroup(project.getGroup().toString());
                    }
                });
            }
        });

        sourcesJar.getMetaInf().from(dependencyInfoTask.getOutputFile());
        sourcesJar.dependsOn(dependencyInfoTask);

        TaskMaker.task(project, DOWNLOAD_PUBLICATIONS_TASK, DownloadPreviousPublicationsTask.class, new Action<DownloadPreviousPublicationsTask>() {
            @Override
            public void execute(final DownloadPreviousPublicationsTask t) {
                t.setDescription("Downloads artifacts of last released version and stores it locally for comparison");

                DeferredConfiguration.deferredConfiguration(project, new Runnable() {
                    @Override
                    public void run() {
                        DefaultArtifactUrlResolver artifactUrlResolver =
                                new DefaultArtifactUrlResolverFactory().getDefaultResolver(project, sourcesJar.getBaseName(), conf.getPreviousReleaseVersion());

                        String previousVersionPomUrl = getDefaultIfNull(t.getPreviousPomUrl(), "previousPomUrl", ".pom", artifactUrlResolver);
                        t.setPreviousPomUrl(previousVersionPomUrl);
                        String previousVersionSourcesJarUrl = getDefaultIfNull(t.getPreviousSourcesJarUrl(), "previousSourcesJarUrl", "-sources.jar", artifactUrlResolver);
                        t.setPreviousSourcesJarUrl(previousVersionSourcesJarUrl);

                        t.setPreviousPom(previousPom);
                        t.setPreviousSourcesJar(previousSourcesJar);
                    }
                });
            }
        });

        TaskMaker.task(project, COMPARE_PUBLICATIONS_TASK, ComparePublicationsTask.class, new Action<ComparePublicationsTask>() {
            public void execute(final ComparePublicationsTask t) {
                t.setDescription("Compares artifacts and poms between last version and the currently built one to see if there are any differences");

                t.dependsOn(DOWNLOAD_PUBLICATIONS_TASK);

                t.setComparisonResult(new File(project.getBuildDir(), "publications-comparison.txt"));

                t.setCurrentVersion(project.getVersion().toString());
                t.setPreviousVersion(conf.getPreviousReleaseVersion());
                t.setPreviousPom(previousPom);
                t.setPreviousSourcesJar(previousSourcesJar);

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

    private String getBasePreviousVersionArtifactPath(Project project, ShipkitConfiguration conf, Jar sourcesJar) {
        return project.getBuildDir().getAbsolutePath() + PREVIOUS_ARTIFACTS_DIR
                + File.separator + sourcesJar.getBaseName() + "-" + conf.getPreviousReleaseVersion();
    }

    private String getDefaultIfNull(String url, String variableName, String extension, DefaultArtifactUrlResolver defaultArtifactUrlResolver) {
        if (url == null) {
            /*
             * it's null when {@link DefaultArtifactUrlResolverFactory} can't find any implementation suitable for the current implementation
             */
            if (defaultArtifactUrlResolver == null) {
                return null;
            }
            String defaultUrl = defaultArtifactUrlResolver.getDefaultUrl(extension);
            LOG.info("Property {} of task {} not set. Setting it to default value - {}", variableName, COMPARE_PUBLICATIONS_TASK, defaultUrl);
            return defaultUrl;
        }
        return url;
    }
}
