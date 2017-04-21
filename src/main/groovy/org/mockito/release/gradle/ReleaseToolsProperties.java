package org.mockito.release.gradle;

/**
 * Properties required by release tools plugins.
 */
public enum ReleaseToolsProperties {

    /**
     * Developers to include in generated pom file.
     * It should be a collection of elements like "GITHUB_USER:FULL_NAME", example:
     * ['szczepiq:Szczepan Faber', 'mstachniuk:Marcin Stachniuk'].
     * <p>
     * See POM reference for <a href="https://maven.apache.org/pom.html#Developers">Developers</a>.
     */
    pom_developers,

    /**
     * Contributors to include in generated pom file.
     * It should be a collection of elements like "GITHUB_USER:FULL_NAME", example:
     * ['szczepiq:Szczepan Faber', 'mstachniuk:Marcin Stachniuk']
     * <p>
     * See POM reference for <a href="https://maven.apache.org/pom.html#Contributors">Contributors</a>.
     */
    pom_contributors,
}
