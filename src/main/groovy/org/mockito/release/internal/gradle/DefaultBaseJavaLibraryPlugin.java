package org.mockito.release.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.CopySpec;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.publish.PublicationContainer;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.tasks.bundling.Jar;
import org.mockito.release.gradle.BaseJavaLibraryPlugin;
import org.mockito.release.internal.gradle.util.PomCustomizer;
import org.mockito.release.internal.gradle.util.GradleDSLHelper;

/**
 * Please keep documentation up to date at {@link BaseJavaLibraryPlugin}
 */
public class DefaultBaseJavaLibraryPlugin implements BaseJavaLibraryPlugin {

    public void apply(final Project project) {
        project.getPlugins().apply("java");
        project.getPlugins().apply("maven-publish");

        final CopySpec license = project.copySpec(new Action<CopySpec>() {
            public void execute(CopySpec copy) {
            copy.from(project.getRootDir()).include("LICENSE");
            }
        });

        ((Jar) project.getTasks().getByName("jar")).with(license);

        final JavaPluginConvention java = project.getConvention().getPlugin(JavaPluginConvention.class);

        final Task sourcesJar = project.getTasks().create("sourcesJar", Jar.class, new Action<Jar>() {
            public void execute(Jar jar) {
                jar.from(java.getSourceSets().getByName("main").getAllSource());
                jar.setClassifier("sources");
                jar.with(license);
            }
        });

        final Task javadocJar = project.getTasks().create("javadocJar", Jar.class, new Action<Jar>() {
            public void execute(Jar jar) {
                jar.from(project.getTasks().getByName("javadoc"));
                jar.setClassifier("javadoc");
                jar.with(license);
            }
        });

        project.getArtifacts().add("archives", sourcesJar);
        project.getArtifacts().add("archives", javadocJar);

        GradleDSLHelper.publications(project, new Action<PublicationContainer>() {
            public void execute(PublicationContainer publications) {
                publications.create("javaLibrary", MavenPublication.class, new Action<MavenPublication>() {
                    public void execute(MavenPublication publication) {
                        publication.from(project.getComponents().getByName("java"));
                        publication.artifact(sourcesJar);
                        publication.artifact(javadocJar);
                        PomCustomizer.customizePom(project, publication);
                    }
                });
            }
        });

        //so that we flesh out problems with maven publication during the build process
        project.getTasks().getByName("build").dependsOn("publishToMavenLocal");
    }
}
