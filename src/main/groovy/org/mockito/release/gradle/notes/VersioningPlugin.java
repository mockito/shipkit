package org.mockito.release.gradle.notes;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * The plugin adds following tasks:
 *
 * <ul>
 *     <li>bumpVersionFile - increments version in "version.properties" file, see {@link BumpVersionFileTask}</li>
 * </ul>
 *
 * Also, the plugin configures project's version to the value specified in "version.properties"
 */
public interface VersioningPlugin extends Plugin<Project> {
}