package org.mockito.release.internal.gradle.configuration;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.mockito.release.internal.util.ExposedForTesting;
import org.mockito.release.internal.util.MultiMap;

/**
 * Deferred configuration of Gradle objects (tasks, projects) so that they can reflect user-specified values.
 *
 * See {@link #deferredConfiguration(Project, Runnable)}.
 * See also {@link LazyConfiguration}
 */
public class DeferredConfiguration {

    private final static Logger LOGGER = Logging.getLogger(DeferredConfiguration.class);

    private static final MultiMap<Project, Runnable> ACTIONS_BY_PROJECT = new MultiMap<Project, Runnable>();

    /**
     * Defers configuring the project and tasks, making use of user-defined settings in the build.gradle.
     * Use it for settings that should be configured in the build.gradle by the user.
     * For secret settings that are not required by regular developer builds, use {@link LazyConfiguration}.
     * Deferred configuration is needed every time we need to configure project / tasks
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
    public static void deferredConfiguration(Project project, final Runnable runnable) {
        ACTIONS_BY_PROJECT.put(project, runnable);
        project.afterEvaluate(new Action<Project>() {
            public void execute(Project project) {
                LOGGER.info("{} - executing deferred configuration using 'afterEvaluate'", project.getPath());
                runnable.run();
            }
        });
    }

    @ExposedForTesting
    public static void forceConfiguration(Project project){
        for(Runnable action : ACTIONS_BY_PROJECT.get(project)){
            action.run();
        }
    }
}
