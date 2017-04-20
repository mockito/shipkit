package org.mockito.release.internal.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.mockito.release.gradle.ReleaseConfiguration;

/**
 * Adds extension for configuring the release to the root project.
 * Important: it will add to the root project because this is where the configuration belong to!
 * <p>
 * Adds extensions:
 * <ul>
 *     <li>releasing - {@link ReleaseConfiguration}</li>
 * </ul>
 */
public class ReleaseConfigurationPlugin implements Plugin<Project> {

    private ReleaseConfiguration configuration;

    public void apply(Project project) {
        //TODO unit test
        configuration = project.getRootProject().getExtensions()
                .create("releasing", ReleaseConfiguration.class);

        if (project.hasProperty("releaseDryRun")) {
            configuration.setDryRun(true);
        }
    }

    /**
     * Returns the release configuration instance that is hooked up to the root project
     */
    public ReleaseConfiguration getConfiguration() {
        return configuration;
    }
}
