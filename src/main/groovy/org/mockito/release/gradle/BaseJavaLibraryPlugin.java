package org.mockito.release.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Intended to be applied in individual Java submodule. Applies following plugins and tasks and configures them:
 *
 * <ul>
 *     <li>java</li>
 *     <li>maven-publish</li>
 * </ul>
 *
 * Adds following tasks:
 * <ul>
 *     <li>sourcesJar</li>
 *     <li>javadocJar</li>
 * </ul>
 *
 * Automatically includes "LICENSE" file in all jars.
 */
public interface BaseJavaLibraryPlugin extends Plugin<Project> {
}
