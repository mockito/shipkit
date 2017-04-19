package org.mockito.release.internal.gradle.util;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

/**
 * Deferred configuration of Gradle objects (tasks, projects) so that they can reflect user-specified values.
 * See {@link #deferredConfiguration(Project, Action)}.
 */
public class DeferredConfiguration {

    private final static Logger LOGGER = Logging.getLogger(LazyValidator.class);

    /**
     * Defers configuring the project and tasks, making use of user-defined settings in the build script.
     * It is needed every time we need to configure project / tasks
     * based on values specified by the user inside of the "build.gradle" file.
     * Example "build.gradle" file:
     * <pre>
     *     //plugin gets applied and the tasks and extension object are added:
     *     apply plugin: "org.mockito.mockito-release-tools.continuous-delivery"
     *
     *     //the plugin was already applied but the user only now can configure the extension object:
     *     releasing {
     *         releaseNotes {
     *             file = file("CHANGELOG.md")
     *         }
     *     }
     *
     *     //the settings above need to be reflected in tasks added earlier by the plugin
     * </pre>
     */
    public static void deferredConfiguration(Project project, final Action<Project> action) {
        project.afterEvaluate(new Action<Project>() {
            public void execute(Project project) {
                LOGGER.info("{} - executing deferred configuration using 'afterEvaluate'", project.getPath());
                action.execute(project);
            }
        });
    }
}
