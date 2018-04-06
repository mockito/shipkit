package org.shipkit.internal.gradle.bintray;

import com.jfrog.bintray.gradle.BintrayExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.shipkit.gradle.configuration.ShipkitConfiguration;
import org.shipkit.gradle.notes.UpdateReleaseNotesTask;
import org.shipkit.internal.gradle.configuration.ShipkitConfigurationPlugin;
import org.shipkit.internal.gradle.git.GitPlugin;
import org.shipkit.internal.gradle.java.JavaBintrayPlugin;
import org.shipkit.internal.gradle.notes.ReleaseNotesPlugin;
import org.shipkit.internal.gradle.release.ReleasePlugin;
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
 *     <li>Configures {@link ReleasePlugin#CONTRIBUTOR_TEST_RELEASE_TASK} task to exclude
 *     {@link ShipkitBintrayPlugin#BINTRAY_UPLOAD_TASK} task from contributor test.
 *     This way, contributors can test release logic without having secret keys.
 *     </li>
 * </ul>
 */
public class BintrayReleasePlugin implements Plugin<Project> {

    public void apply(final Project project) {
        ReleasePlugin releasePlugin = project.getPlugins().apply(ReleasePlugin.class);
        final ShipkitConfiguration conf = project.getPlugins().apply(ShipkitConfigurationPlugin.class).getConfiguration();

        final Task gitPush = project.getTasks().getByName(GitPlugin.GIT_PUSH_TASK);
        final Task performRelease = project.getTasks().getByName(ReleasePlugin.PERFORM_RELEASE_TASK);

        project.allprojects(subproject -> {
            subproject.getPlugins().withType(ShipkitBintrayPlugin.class, plugin -> {
                Task bintrayUpload = subproject.getTasks().getByName(ShipkitBintrayPlugin.BINTRAY_UPLOAD_TASK);
                performRelease.dependsOn(bintrayUpload);

                //bintray upload after git push so that when git push fails we don't publish jars to bintray
                //git push is easier to undo than deleting published jars (not possible with Central)
                bintrayUpload.mustRunAfter(gitPush);

                final BintrayExtension bintray = subproject.getExtensions().getByType(BintrayExtension.class);
                deferredConfiguration(subproject, () -> {
                    UpdateReleaseNotesTask updateNotes = (UpdateReleaseNotesTask) project.getTasks().getByName(ReleaseNotesPlugin.UPDATE_NOTES_TASK);
                    String userSpecifiedRepo = conf.getLenient().getReleaseNotes().getPublicationRepository();
                    if (userSpecifiedRepo != null) {
                        updateNotes.setPublicationRepository(userSpecifiedRepo);
                    } else {
                        updateNotes.setPublicationRepository(BintrayUtil.getRepoLink(bintray));
                    }
                });
            });

            subproject.getPlugins().withType(JavaBintrayPlugin.class, plugin -> {
                //Making git push run as late as possible because it is an operation that is hard to reverse.
                //Git push will be executed after all tasks needed by bintrayUpload
                // but before bintrayUpload.
                //Using task path as String because the task comes from maven-publish new configuration model
                // and we cannot refer to it in a normal way, by task instance.
                String mavenLocalTask = subproject.getPath() + ":" + MAVEN_LOCAL_TASK;
                gitPush.mustRunAfter(mavenLocalTask);
            });

            subproject.getPlugins().withType(ShipkitBintrayPlugin.class, plugin -> {
                //when contributors are testing, we need to avoid bintray task because it requires secret keys
                releasePlugin.excludeFromContributorTest(ShipkitBintrayPlugin.BINTRAY_UPLOAD_TASK);
            });
        });
    }
}
