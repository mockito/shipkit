package org.shipkit.internal.gradle.versionupgrade;

import java.util.List;

/**
 * Configuration of {@link UpgradeDownstreamPlugin}
 */
public class UpgradeDownstreamExtension {
    private List<String> repositories;

    /**
     * List of all consumers repositories names, for which version upgrade should be produced.
     * Each repository name should have a format "user/repo", eg. "mockito/shipkit".
     */
    public List<String> getRepositories() {
        return repositories;
    }

    /**
     * See {@link #getRepositories()}
     */
    public void setRepositories(List<String> repositories) {
        this.repositories = repositories;
    }
}
