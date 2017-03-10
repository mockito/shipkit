package org.mockito.release.gradle.notes;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * The plugin adds following tasks:
 *
 * <ul>
 *     <li>bumpVersionFile - increments version file, see {@link BumpVersionFileTask}</li>
 * </ul>
 */
public interface VersioningPlugin extends Plugin<Project> {
}