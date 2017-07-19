package org.shipkit.internal.gradle.release;

import com.jfrog.bintray.gradle.BintrayExtension;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.shipkit.gradle.notes.UpdateReleaseNotesTask;
import org.shipkit.internal.gradle.ShipkitBintrayPlugin;
import org.shipkit.internal.gradle.ReleaseNotesPlugin;
import org.shipkit.internal.gradle.git.GitPlugin;
import org.shipkit.internal.gradle.java.JavaBintrayPlugin;
import org.shipkit.internal.gradle.util.BintrayUtil;

import static org.shipkit.internal.gradle.configuration.DeferredConfiguration.deferredConfiguration;
import static org.shipkit.internal.gradle.java.JavaPublishPlugin.MAVEN_LOCAL_TASK;

/**
 * Configures Java project for automated releases with Bintray.
 * <p>
 * Applies following plugins:
 *
 * <ul>
 *     <li>{@link ReleasePlugin}</li>
 * </ul>
 *
 * Adds following behavior to all submodules that have {@link JavaBintrayPlugin}:
 *
 * <ul>
 *     <li>Hooks up bintray upload tasks to the release tasks</li>
 *     <li>Configures Bintray publication repository on the release notes tasks</li>
 * </ul>
 */
public class BintrayReleasePlugin implements Plugin<Project> {

    public void apply(final Project project) {
        project.getPlugins().apply(ReleasePlugin.class);

        project.allprojects(new Action<Project>() {
            @Override
            public void execute(final Project subproject) {
                subproject.getPlugins().withType(JavaBintrayPlugin.class, new Action<JavaBintrayPlugin>() {
                    public void execute(JavaBintrayPlugin plugin) {
                        Task bintrayUpload = subproject.getTasks().getByName(ShipkitBintrayPlugin.BINTRAY_UPLOAD_TASK);
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
