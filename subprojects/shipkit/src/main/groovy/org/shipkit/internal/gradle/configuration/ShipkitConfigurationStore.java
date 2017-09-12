package org.shipkit.internal.gradle.configuration;

import org.gradle.api.GradleException;
import org.shipkit.internal.util.EnvVariables;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ShipkitConfigurationStore {

    private final boolean lenient;
    private final EnvVariables envVariables;
    private final Map<String, Object> configuration;

    public ShipkitConfigurationStore() {
        this(new HashMap<String, Object>(), new EnvVariables(), false);
    }

    ShipkitConfigurationStore(Map<String, Object> configuration, EnvVariables envVariables, boolean lenient) {
        this.lenient = lenient;
        this.configuration = configuration;
        this.envVariables = envVariables;
    }

    /**
     * Adds key-value pair to the story. If the key already exists, it is replaced.
     */
    public void put(String key, Object value) {
        configuration.put(key, value);
    }

    public String getStringUrl(String key) {
        String url = getString(key);
        if (url.endsWith("/")) {
            return url.replaceAll("/*$", "");
        }
        return url;
    }

    public String getString(String key) {
        return (String) getValue(key, "Please configure 'shipkit." + key + "' value (String).");
    }

    public Map getMap(String key) {
        return (Map) getValue(key, "Please configure 'shipkit." + key + "' value (Map).");
    }

    public Collection<String> getCollection(String key) {
        return (Collection) getValue(key, "Please configure 'shipkit." + key + "' value (Collection).");
    }

    private Object getValue(String key, String message) {
        return getValue(key, null, message);
    }

    public Object getValue(String key, String envVarName, String message) {
        Object value = configuration.get(key);

        if (value != null) {
            return value;
        }

        if (envVarName != null) {
            value = envVariables.getNonEmptyEnv(envVarName);
            if (value != null) {
                return value;
            }
        }

        if (lenient) {
            return null;
        }

        throw new GradleException(message);
    }

    public ShipkitConfigurationStore getLenient() {
        return new ShipkitConfigurationStore(this.configuration, new EnvVariables(), true);
    }
}
