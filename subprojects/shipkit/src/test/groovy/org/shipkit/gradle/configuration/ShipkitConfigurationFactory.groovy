package org.shipkit.gradle.configuration

import org.shipkit.internal.util.EnvVariables

class ShipkitConfigurationFactory {

    static ShipkitConfiguration create(EnvVariables envVariables) {
        return new ShipkitConfiguration(envVariables)
    }
}
