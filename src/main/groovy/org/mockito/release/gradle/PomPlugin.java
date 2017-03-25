package org.mockito.release.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Adds "ext.pom_customizePom(Project, MavenPublication)" method to the project.
 * The method can be used to customize the pom.
 *
 * The method requires following properties to function correctly:
 * <ul>
 *  <li> project.description
 *  <li> project.archivesBaseName
 *  <li> project.rootProject.ext.gh_repository
 *  <li> project.rootProject.ext.pom_developers
 *  <li> project.rootProject.ext.pom_contributors
 * </ul>
 */
public interface PomPlugin extends Plugin<Project> {
}