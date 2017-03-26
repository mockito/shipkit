package org.mockito.release.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Applies "com.jfrog.bintray" plugin and preconfigures it
 */
public interface BintrayPlugin extends Plugin<Project> {

    /**
     * Name of the task that is configured by this plugin
     */
    String BINTRAY_UPLOAD_TASK = "bintrayUpload";
}
