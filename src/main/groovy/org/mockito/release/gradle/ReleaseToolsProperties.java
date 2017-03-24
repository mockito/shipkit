package org.mockito.release.gradle;

/**
 * Properties required by release tools plugins.
 */
public enum ReleaseToolsProperties {

    /**
     * GitHub read only auth token
     */
    gh_readOnlyAuthToken,

    /**
     * GitHub repository, for example: "mockito/mockito"
     */
    gh_repository,

    /**
     * Release notes file relative path, for example: "docs/release-notes.md"
     */
    releaseNotes_file,

    /**
     * Issue tracker label mappings.
     * The mapping of "GitHub label" to human readable and presentable name.
     * The order of labels is important and will influence the order
     * in which groups of issues are generated in release notes.
     * Examples: ['java-9': 'Java 9 support', 'BDD': 'Behavior-Driven Development support']
     */
    releaseNotes_labelMapping
}
