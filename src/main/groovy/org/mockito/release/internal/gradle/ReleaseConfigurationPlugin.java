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
        if (project.getParent() == null) {
            //root project, add the extension
            configuration = project.getRootProject().getExtensions()
                    .create("releasing", ReleaseConfiguration.class);

            if (project.hasProperty("releasing.dryRun")) {
                Object value = project.getProperties().get("releasing.dryRun");
                configuration.setDryRun(!"false".equals(value));
                //TODO we can actually implement it so that we automatically preconfigure everything by command line parameters
                //e.g. releasing.gitHub.repository is also a property
            }
        } else {
            //not root project, get extension from root project
            configuration = project.getRootProject().getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();
        }
    }

    /**
     * Returns the release configuration instance that is hooked up to the root project
     */
    public ReleaseConfiguration getConfiguration() {
        return configuration;
    }
}
