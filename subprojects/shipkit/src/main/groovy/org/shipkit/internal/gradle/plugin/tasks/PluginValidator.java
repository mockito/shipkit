package org.shipkit.internal.gradle.plugin.tasks;

import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.SourceSet;
import org.shipkit.internal.gradle.util.StringUtil;

import java.io.File;
import java.util.*;

import static org.shipkit.internal.gradle.plugin.tasks.PluginUtil.DOT_PROPERTIES;
import static org.shipkit.internal.gradle.plugin.tasks.PluginUtil.getImplementationClass;


public class PluginValidator {

    private static final Logger LOG = Logging.getLogger(PluginValidator.class);
    private static final String[] PLUGIN_EXTENSIONS = new String[]{".groovy", ".java"};

    private Set<File> sourceDirs;

    public void validate(SourceSet sourceSet) {
        Set<File> gradleProperties = PluginUtil.discoverGradlePluginPropertyFiles(sourceSet);
        sourceDirs = sourceSet.getAllJava().getSrcDirs();
        ensureNamingConvention(gradleProperties);
    }

    private void ensureNamingConvention(Set<File> gradlePropertiesFiles) {
        Map<String, String> errors = new HashMap<>();
        for (File gradlePropertiesFile: gradlePropertiesFiles) {
            String pluginId = gradlePropertiesFile.getName().substring(0, gradlePropertiesFile.getName().lastIndexOf(DOT_PROPERTIES));
            List<String> candidates = getClassCandidates(pluginId);

            String implementationClass = getImplementationClass(gradlePropertiesFile);
            boolean containsDots = implementationClass.contains(".");

            boolean foundClass = false;
            for (String candidate: candidates) {
                if (containsDots) {
                    candidate = "." + candidate;
                }
                if (implementationClass.toLowerCase().endsWith(candidate.toLowerCase())) {
                    // we found the matching one
                    LOG.info("plugin-id: " + pluginId + " matching implementation class found " + implementationClass);
                    foundClass = true;
                    break;
                }
            }

            if (!foundClass) {
                errors.put(pluginId, "Implementation class " + implementationClass + " does not match one of the acceptable names " + candidates);
            } else if (!ensureImplementationClassExists(implementationClass)) {
                errors.put(pluginId, "Implementation class " + implementationClass + " does not exist!");
            }
        }

        throwExceptionIfNeeded(errors);
    }

    static List<String> getClassCandidates(String pluginId) {
        List<String> candidates = new ArrayList<>();

        String[] pluginIdParts = pluginId.split("\\.");
        String previousCandidate = "Plugin";
        if (pluginId.toLowerCase().endsWith(previousCandidate.toLowerCase())) {
            previousCandidate = "";
        }
        for (int i = pluginIdParts.length - 1; i >= 0; i--) {
            String candidate = StringUtil.capitalize(processPart(pluginIdParts[i])) + previousCandidate;
            candidates.add(candidate);
            previousCandidate = candidate;
        }
        return candidates;
    }

    private static String processPart(String pluginIdPart) {
        StringBuilder sb = new StringBuilder();
        String[] parts = pluginIdPart.split("-");
        for (String part: parts) {
            sb.append(StringUtil.capitalize(part));
        }
        return sb.toString();
    }

    private boolean ensureImplementationClassExists(String implementationClass) {
        String pathToClass = implementationClass.replace('.', File.separatorChar);

        for (File sourceDir : sourceDirs) {
            File[] files = getFileCandidates(sourceDir.getAbsolutePath() + File.separator + pathToClass);
            for (File file: files) {
                if (file.exists()) {
                    LOG.info("implementation class " + implementationClass + " exists!");
                    return true;
                }
            }
        }

        return false;
    }

    private File[] getFileCandidates(String pathToClass) {
        File[] includes = new File[PLUGIN_EXTENSIONS.length];
        for (int i = 0; i < PLUGIN_EXTENSIONS.length; i++) {
            includes[i] = new File(pathToClass + PLUGIN_EXTENSIONS[i]);
        }
        return includes;
    }


    private void throwExceptionIfNeeded(Map<String, String> errors) {
        if (errors.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("plugin validation failed for plugin(s):");
            for (String pluginId : errors.keySet()) {
                sb.append("\n\t");
                sb.append("'");
                sb.append(pluginId);
                sb.append("': ");
                sb.append(errors.get(pluginId));
            }
            throw new GradleException(sb.toString());
        }
    }

}
