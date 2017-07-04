package org.shipkit.internal.gradle.release;

import com.jfrog.bintray.gradle.BintrayExtension;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.shipkit.gradle.UpdateReleaseNotesTask;
import org.shipkit.internal.gradle.*;
import org.shipkit.internal.gradle.util.BintrayUtil;

import static org.shipkit.internal.gradle.BaseJavaLibraryPlugin.MAVEN_LOCAL_TASK;
import static org.shipkit.internal.gradle.configuration.DeferredConfiguration.deferredConfiguration;

/**
 * Configures Java project for automated releases.
 * Applies some configuration to subprojects, too.
 *
 * Applies following plugins:
 *
 * <ul>
 *     <li>{@link GitPlugin}</li>
 *     <li>{@link PomContributorsPlugin}</li>
 *     <li>{@link ReleasePlugin}</li>
 * </ul>
 */
public class JavaReleasePlugin implements Plugin<Project> {

    public void apply(final Project project) {
        project.getPlugins().apply(GitPlugin.class);
        project.getPlugins().apply(PomContributorsPlugin.class);
        project.getPlugins().apply(ReleasePlugin.class);

        project.allprojects(new Action<Project>() {
            @Override
            public void execute(final Project subproject) {
                subproject.getPlugins().withType(JavaLibraryPlugin.class, new Action<JavaLibraryPlugin>() {
                    public void execute(JavaLibraryPlugin plugin) {
                        Task bintrayUpload = subproject.getTasks().getByName(BintrayPlugin.BINTRAY_UPLOAD_TASK);
                        Task performRelease = project.getTasks().getByName(ReleasePlugin.PERFORM_RELEASE_TASK);
                        performRelease.dependsOn(bintrayUpload);

                        //Making git push run as late as possible because it is an operation that is hard to reverse.
                        //Git push will be executed after all tasks needed by bintrayUpload
                        // but before bintrayUpload.
                        //Using task path as String because the task comes from maven-publish new configuration model
                        // and we cannot refer to it in a normal way, by task instance.
                        String mavenLocalTask = subproject.getPath() + ":" + MAVEN_LOCAL_TASK;
                        Task gitPush = project.getTasks().getByName(GitPlugin.GIT_PUSH_TASK);
                        gitPush.mustRunAfter(mavenLocalTask);
                        //bintray upload after git push so that when git push fails we don't publish jars to bintray
                        //git push is easier to undo than deleting published jars (not possible with Central)
                        bintrayUpload.mustRunAfter(gitPush);

                        final BintrayExtension bintray = subproject.getExtensions().getByType(BintrayExtension.class);
                        deferredConfiguration(subproject, new Runnable() {
                            public void run() {
                                UpdateReleaseNotesTask updateNotes = (UpdateReleaseNotesTask) project.getTasks().getByName(ReleaseNotesPlugin.UPDATE_NOTES_TASK);
                                updateNotes.setPublicationRepository(BintrayUtil.getRepoLink(bintray));
                            }
                        });
                    }
                });
            }
        });
    }
}
