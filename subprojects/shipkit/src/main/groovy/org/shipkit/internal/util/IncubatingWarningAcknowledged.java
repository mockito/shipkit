package org.shipkit.internal.util;

import org.shipkit.gradle.configuration.ShipkitConfiguration;

import java.util.Collection;
import java.util.function.Predicate;

public class IncubatingWarningAcknowledged implements Predicate<String> {

    private Collection<String> acknowledgedWarnings;

    private IncubatingWarningAcknowledged(ShipkitConfiguration configuration) {
        this.acknowledgedWarnings = configuration.getIncubatingWarnings().getAcknowledged();
    }

    @Override
    public boolean test(String test) {
     return acknowledgedWarnings.stream().anyMatch((acknowledged) -> test.startsWith(acknowledged));
    }
}
