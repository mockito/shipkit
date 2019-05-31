package org.shipkit.internal.gradle.java;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.tasks.bundling.Jar;
import org.shipkit.gradle.configuration.ShipkitConfiguration;
import org.shipkit.internal.gradle.configuration.DeferredConfiguration;
import org.shipkit.internal.gradle.configuration.ShipkitConfigurationPlugin;
import org.shipkit.internal.gradle.snapshot.LocalSnapshotPlugin;
import org.shipkit.internal.gradle.util.GradleDSLHelper;
import org.shipkit.internal.gradle.util.PomCustomizer;
import org.shipkit.internal.gradle.util.StringUtil;

/**
 * Publishing java libraries using 'maven-publish' plugin.
 * Intended to be applied in individual Java submodule.
 * Applies following plugins and tasks and configures them:
 *
 * <ul>
 *     <li>{@link JavaLibraryPlugin}</li>
 *     <li>maven-publish</li>
 *     <li>{@link LocalSnapshotPlugin}</li>
 * </ul>
 *
 * Other features:
 * <ul>
 *     <li>Configures Gradle's publications to publish java library</li>
 *     <li>Configures 'build' task to depend on 'publishJavaLibraryToMavenLocal'
 *          to flesh out publication issues during the build</li>
 *     <li>Configures 'snapshot' task to depend on 'publishJavaLibraryToMavenLocal'</li>
 * </ul>
 */
public class JavaPublishPlugin implements Plugin<Project> {

    private final static Logger LOG = Logging.getLogger(JavaPublishPlugin.class);

    public final static String PUBLICATION_NAME = "javaLibrary";
    public final static String POM_TASK = "generatePomFileFor" + StringUtil.capitalize(PUBLICATION_NAME) + "Publication";
    public final static String MAVEN_LOCAL_TASK = "publish" + StringUtil.capitalize(PUBLICATION_NAME) + "PublicationToMavenLocal";

    public void apply(final Project project) {
        final ShipkitConfiguration conf = project.getPlugins().apply(ShipkitConfigurationPlugin.class).getConfiguration();

        project.getPlugins().apply(LocalSnapshotPlugin.class);
        Task snapshotTask = project.getTasks().getByName(LocalSnapshotPlugin.SNAPSHOT_TASK);
        snapshotTask.dependsOn(MAVEN_LOCAL_TASK);

        project.getPlugins().apply(JavaLibraryPlugin.class);
        project.getPlugins().apply("maven-publish");

        final Jar sourcesJar = (Jar) project.getTasks().getByName(JavaLibraryPlugin.SOURCES_JAR_TASK);
        final Jar javadocJar = (Jar) project.getTasks().getByName(JavaLibraryPlugin.JAVADOC_JAR_TASK);

        GradleDSLHelper.publications(project, publications -> {
            MavenPublication p = publications.create(PUBLICATION_NAME, MavenPublication.class, publication -> {
                publication.from(project.getComponents().getByName("java"));
                publication.artifact(sourcesJar);
                publication.artifact(javadocJar);
                DeferredConfiguration.deferredConfiguration(project, () -> {
                    publication.setArtifactId(((Jar) project.getTasks().getByName("jar")).getBaseName());
                });
                PomCustomizer.customizePom(project, conf, publication);
            });
            LOG.info("{} - configured '{}' publication", project.getPath(), p.getArtifactId());
        });

        //so that we flesh out problems with maven publication during the build process
        project.getTasks().getByName("build").dependsOn(MAVEN_LOCAL_TASK);
    }
}
