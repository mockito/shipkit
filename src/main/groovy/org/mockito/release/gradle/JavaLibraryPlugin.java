package org.mockito.release.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Intended to be applied in individual Java submodule. Applies following plugins:
 *
 * <ul>
 *     <li>org.mockito.mockito-release-tools.java-library - see {@link JavaLibraryPlugin}</li>
 *     <li>org.mockito.mockito-release-tools.bintray - see {@link BintrayPlugin}</li>
 * </ul>
 */
public interface JavaLibraryPlugin extends Plugin<Project> {
}