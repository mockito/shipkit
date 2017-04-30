package org.mockito.release.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.CopySpec;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.publish.PublicationContainer;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.tasks.bundling.Jar;
import org.mockito.release.gradle.ReleaseConfiguration;
import org.mockito.release.internal.comparison.BaseProjectProperties;
import org.mockito.release.internal.comparison.PublicationsComparatorTask;
import org.mockito.release.internal.gradle.util.GradleDSLHelper;
import org.mockito.release.internal.gradle.util.PomCustomizer;
import org.mockito.release.internal.gradle.util.TaskMaker;
import org.mockito.release.version.VersionInfo;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.release.internal.gradle.util.StringUtil.capitalize;

/**
 * Intended to be applied in individual Java submodule. Applies following plugins and tasks and configures them:
 *
 * <ul>
 *     <li>java</li>
 *     <li>maven-publish</li>
 * </ul>
 *
 * Adds following tasks:
 * <ul>
 *     <li>sourcesJar</li>
 *     <li>javadocJar</li>
 * </ul>
 *
 * Other features:
 * <ul>
 *     <li>Automatically includes "LICENSE" file in all jars.</li>
 *     <li>Adds build.dependsOn "publishToMavenLocal" to flesh out publication issues during the build</li>
 *     <li>Makes pom-generating task depend on "fetchAllProjectContributorsFromGitHub" to load all contributors from GitHub</li>
 * </ul>
 */
public class BaseJavaLibraryPlugin implements Plugin<Project> {

    private final static Logger LOG = Logging.getLogger(BaseJavaLibraryPlugin.class);

    final static String PUBLICATION_NAME = "javaLibrary";
    final static String COMPARE_PUBLICATIONS_TASK = "comparePublications";
    final static String POM_TASK = "generatePomFileFor" + capitalize(PUBLICATION_NAME) + "Publication";


    public void apply(final Project project) {
        final ReleaseConfiguration conf = project.getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();

        project.getPlugins().apply("java");
        project.getPlugins().apply("maven-publish");

        final CopySpec license = project.copySpec(new Action<CopySpec>() {
            public void execute(CopySpec copy) {
            copy.from(project.getRootDir()).include("LICENSE");
            }
        });

        ((Jar) project.getTasks().getByName("jar")).with(license);

        final JavaPluginConvention java = project.getConvention().getPlugin(JavaPluginConvention.class);

        final Jar sourcesJar = project.getTasks().create("sourcesJar", Jar.class, new Action<Jar>() {
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

        TaskMaker.task(project, COMPARE_PUBLICATIONS_TASK, PublicationsComparatorTask.class, new Action<PublicationsComparatorTask>() {
            public void execute(final PublicationsComparatorTask t) {
                t.setDescription("Compares artifacts and poms between last version and the currently built one to see if there are any differences");

                project.getRootProject().getPlugins().withType(
                        VersioningPlugin.class,
                        new Action<VersioningPlugin>() {
                            @Override
                            public void execute(VersioningPlugin versioningPlugin) {
                                System.out.println("here");
                                VersionInfo versionInfo = project.getRootProject().getExtensions().getByType(VersionInfo.class);

                                t.setCurrentVersion(versionInfo.getVersion());
                                t.setPreviousVersion(versionInfo.getPreviousVersion());
//                                t.doFirst(new Action<Task>() {
//                                    @Override
//                                    public void execute(Task task) {
//                                        VersionInfo versionInfo = project.getRootProject().getExtensions().getByType(VersionInfo.class);
//
//                                        t.setCurrentVersion(versionInfo.getVersion());
//                                        t.setPreviousVersion(versionInfo.getPreviousVersion());
//
//                                        System.out.println("prev " + versionInfo.getPreviousVersion());
//                                        System.out.println("current " + versionInfo.getVersion());
//                                    }
//                                });
                            }
                        }
                );


                //Let's say that the initial implementation compares sources jar. We can this API method to the task:
                t.compareSourcesJar(sourcesJar);
                //Let's say we compare poms, we can add this API
                //maven-publish plugin is messed up in Gradle API, we cannot really access generate pom task and we have to pass String
                //The generate pom task is dynamically created by Gradle and we can only access it during execution
                t.comparePom(POM_TASK);

                t.setProjectGroup(project.getGroup().toString());
                t.setProjectName(project.getName());

                final Set<BaseProjectProperties> dependentSiblingProjects = new HashSet<BaseProjectProperties>();
                project.getRootProject().allprojects(new Action<Project>() {
                    @Override
                    public void execute(Project subproject) {
                        dependentSiblingProjects.add(
                            new BaseProjectProperties(subproject.getGroup().toString(), subproject.getName()));
                    }
                });
                t.setDependentSiblingProjects(dependentSiblingProjects);

                t.setLocalRepository(project.getRepositories().mavenLocal().getUrl().getPath());
            }
        });

        project.getArtifacts().add("archives", sourcesJar);
        project.getArtifacts().add("archives", javadocJar);

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
