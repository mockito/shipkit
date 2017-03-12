package org.mockito.release.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.publish.Publication;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;

import java.io.File;

import static org.codehaus.groovy.runtime.StringGroovyMethods.capitalize;

public class DefaultPublicationsComparatorPlugin implements Plugin<Project> {

    @Override
    public void apply(final Project project) {
        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(Project project) {
                configureTasks(project);
            }
        });
    }

    static void configureTasks(Project project) {
        if (!project.getPlugins().hasPlugin("maven-publish")) {
            return;
        }

        project.getRepositories().maven(new Action<MavenArtifactRepository>() {
            public void execute(MavenArtifactRepository repo) {
                repo.setUrl("https://dl.bintray.com/mockito/mockito-release-tools-example-repo");
            }
        });

        PublishingExtension publishing = project.getExtensions().getByType(PublishingExtension.class);
        for (Publication publication : publishing.getPublications()) {
            if (publication instanceof MavenPublication) {
                MavenPublication p = (MavenPublication) publication;
                String capitalizedPublication = capitalize((CharSequence) publication.getName());

                DefaultPomComparatorTask task = project.getTasks().create(
                        "comparePomsFor" + capitalizedPublication + "Publication", DefaultPomComparatorTask.class);
                task.setGroup(CommonSettings.TASK_GROUP);
                task.setDescription("Compares local pom file with remote, previous version pom.");

                task.dependsOn("generatePomFileFor" + capitalizedPublication + "Publication");

                task.setLocalPom(new File(project.getBuildDir(), "publications/" + p.getName() + "/pom-default.xml"));

                String previousVersion = "0.0.2";
                task.setRemotePomUrl(p.getGroupId() + ":" + p.getArtifactId() + ":" + previousVersion + "@pom");
                task.setResultsFile(new File(project.getBuildDir(), "pom-comparison-result-" + p.getName() + ".txt"));
            }
        }
    }
}
