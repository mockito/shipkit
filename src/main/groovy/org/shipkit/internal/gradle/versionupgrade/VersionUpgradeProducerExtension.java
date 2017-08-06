package org.shipkit.internal.gradle.versionupgrade;

import java.util.List;

/**
 * Configuration of {@link VersionUpgradeProducerPlugin}
 */
public class VersionUpgradeProducerExtension {
    private List<String> consumersRepositoriesNames;

    /**
     * List of all consumers repositories names, for which version upgrade should be produced.
     * Each repository name should have a format "user/repo", eg. "mockito/shipkit".
     */
    public List<String> getConsumersRepositoriesNames() {
        return consumersRepositoriesNames;
    }

    /**
     * See {@link #getConsumersRepositoriesNames()}
     */
    public void setConsumersRepositoriesNames(List<String> consumersRepositoriesNames) {
        this.consumersRepositoriesNames = consumersRepositoriesNames;
    }
}
