package org.shipkit.internal.gradle.java;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.publish.PublicationContainer;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.tasks.bundling.Jar;
import org.shipkit.gradle.ReleaseConfiguration;
import org.shipkit.internal.gradle.ReleaseConfigurationPlugin;
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
 * </ul>
 *
 * Other features:
 * <ul>
 *     <li>Configures Gradle's publications to publish java library</li>
 *     <li>Adds build.dependsOn "publishToMavenLocal" to flesh out publication issues during the build</li>
 * </ul>
 */
public class JavaPublishPlugin implements Plugin<Project> {

    private final static Logger LOG = Logging.getLogger(JavaPublishPlugin.class);

    public final static String PUBLICATION_NAME = "javaLibrary";
    public final static String POM_TASK = "generatePomFileFor" + StringUtil.capitalize(PUBLICATION_NAME) + "Publication";
    public final static String MAVEN_LOCAL_TASK = "publish" + StringUtil.capitalize(PUBLICATION_NAME) + "PublicationToMavenLocal";

    public void apply(final Project project) {
        final ReleaseConfiguration conf = project.getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();

        project.getPlugins().apply(JavaLibraryPlugin.class);
        project.getPlugins().apply("maven-publish");

        final Jar sourcesJar = (Jar) project.getTasks().getByName(JavaLibraryPlugin.SOURCES_JAR_TASK);
        final Jar javadocJar = (Jar) project.getTasks().getByName(JavaLibraryPlugin.JAVADOC_JAR_TASK);

        GradleDSLHelper.publications(project, new Action<PublicationContainer>() {
            public void execute(PublicationContainer publications) {
                MavenPublication p = publications.create(PUBLICATION_NAME, MavenPublication.class, new Action<MavenPublication>() {
                    public void execute(MavenPublication publication) {
                        publication.from(project.getComponents().getByName("java"));
                        publication.artifact(sourcesJar);
                        publication.artifact(javadocJar);
                        publication.setArtifactId(((Jar) project.getTasks().getByName("jar")).getBaseName());
                        PomCustomizer.customizePom(project, conf, publication);
                    }
                });
                LOG.info("{} - configured '{}' publication", project.getPath(), p.getArtifactId());
            }
        });

        //so that we flesh out problems with maven publication during the build process
        project.getTasks().getByName("build").dependsOn("publishToMavenLocal");
    }
}
