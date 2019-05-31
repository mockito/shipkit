package org.shipkit.internal.gradle.android;

import com.jfrog.bintray.gradle.BintrayExtension;

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.util.GradleVersion;
import org.shipkit.gradle.configuration.ShipkitConfiguration;
import org.shipkit.internal.gradle.configuration.ShipkitConfigurationPlugin;
import org.shipkit.internal.gradle.snapshot.LocalSnapshotPlugin;
import org.shipkit.internal.gradle.util.GradleDSLHelper;
import org.shipkit.internal.gradle.util.PomCustomizer;

import static org.shipkit.internal.gradle.java.JavaPublishPlugin.MAVEN_LOCAL_TASK;
import static org.shipkit.internal.gradle.java.JavaPublishPlugin.PUBLICATION_NAME;

/**
 * Publishing Android libraries using 'maven-publish' and 'com.github.technoir42.aar-publish' plugins.
 * Intended to be applied in individual Android library submodule.
 * Applies following plugins and tasks and configures them:
 *
 * <ul>
 *     <li>maven-publish</li>
 *     <li>com.github.technoir42.aar-publish</li>
 * </ul>
 *
 * Other features:
 * <ul>
 *     <li>Configures Gradle's publications to publish Android library</li>
 *     <li>Configures 'build' task to depend on 'publishJavaLibraryToMavenLocal'
 *          to flesh out publication issues during the build</li>
 *     <li>Configures 'snapshot' task to depend on 'publishJavaLibraryToMavenLocal'</li>
 * </ul>
 */
public class AndroidPublishPlugin implements Plugin<Project> {

    private final static Logger LOG = Logging.getLogger(AndroidPublishPlugin.class);

    public void apply(final Project project) {
        ensureGradleVersion();

        final ShipkitConfiguration conf = project.getPlugins().apply(ShipkitConfigurationPlugin.class).getConfiguration();

        project.getPlugins().apply(LocalSnapshotPlugin.class);
        Task snapshotTask = project.getTasks().getByName(LocalSnapshotPlugin.SNAPSHOT_TASK);
        snapshotTask.dependsOn(MAVEN_LOCAL_TASK);

        project.getPlugins().apply("com.github.technoir42.aar-publish");
        project.getPlugins().apply("maven-publish");

        BintrayExtension bintray = project.getExtensions().getByType(BintrayExtension.class);
        bintray.setPublications(PUBLICATION_NAME);

        project.getPlugins().withId("com.android.library", plugin -> {
            GradleDSLHelper.publications(project, publications -> {
                MavenPublication p = publications.create(PUBLICATION_NAME, MavenPublication.class, publication -> {
                    publication.setArtifactId(conf.getAndroid().getArtifactId());
                    publication.from(project.getComponents().getByName("android"));
                    PomCustomizer.customizePom(project, conf, publication);
                });
                LOG.info("{} - configured '{}' publication", project.getPath(), p.getArtifactId());
            });

            //so that we flesh out problems with maven publication during the build process
            project.getTasks().getByName("build").dependsOn(MAVEN_LOCAL_TASK);
        });
    }

    private static void ensureGradleVersion() {
        final GradleVersion currentGradleVersion = GradleVersion.current();
        final GradleVersion minimumGradleVersion = GradleVersion.version("5.3");
        if (currentGradleVersion.compareTo(minimumGradleVersion) < 0) {
            throw new GradleException("Current Gradle version: " + currentGradleVersion.getVersion() + " is less than minimum required: " + minimumGradleVersion.getVersion());
        }
    }
}
