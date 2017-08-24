package org.shipkit.internal.gradle.plugin.tasks;

import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.SourceSet;
import org.shipkit.internal.gradle.util.JavaPluginUtil;
import org.shipkit.internal.gradle.util.StringUtil;

import java.io.File;
import java.util.*;

import static org.shipkit.internal.gradle.plugin.tasks.PluginUtil.getImplementationClass;
import static org.shipkit.internal.gradle.plugin.tasks.PluginUtil.DOT_PROPERTIES;


public class PluginValidator {

    private static final Logger LOG = Logging.getLogger(PluginValidator.class);
    private static final String[] PLUGIN_EXTENSIONS = new String[]{".groovy", ".java"};

    private Set<File> sourceDirs;

    public void validate(Project project) {
        SourceSet sourceSet = JavaPluginUtil.getMainSourceSet(project);
        Set<File> gradleProperties = PluginUtil.discoverGradlePluginPropertyFiles(sourceSet);
        sourceDirs = sourceSet.getAllJava().getSrcDirs();
        ensureNamingConvention(gradleProperties);
    }

    private void ensureNamingConvention(Set<File> gradlePropertiesFiles) {
        StringBuilder sb = new StringBuilder();
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
                throw new RuntimeException("plugin-id: " + pluginId + " -> implementation class " + implementationClass + "  does not match one of the acceptable names " + candidates);
            }

            if (!ensureImplementationClassExists(implementationClass)) {
                throw new RuntimeException("Implementation class " + implementationClass + " does not exist!");
            }
        }
    }

    private List<String> getClassCandidates(String pluginId) {
        List<String> candidates = new ArrayList<String>();

        String[] candidate1 = pluginId.split("\\.|-");
        String previousCandidate = "Plugin";
        if (pluginId.toLowerCase().endsWith(previousCandidate.toLowerCase())) {
            previousCandidate = "";
        }
        for (int i = candidate1.length - 1; i >= 0; i--) {
            String candidate = StringUtil.capitalize(candidate1[i]) + previousCandidate;
            candidates.add(candidate);
            previousCandidate = candidate;
        }
        return candidates;
    }

    private boolean ensureImplementationClassExists(String implementationClass) {
        String pathToClass = implementationClass.replaceAll("\\.", File.separator);

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
        for(int i = 0; i < PLUGIN_EXTENSIONS.length; i++) {
            includes[i] = new File(pathToClass + PLUGIN_EXTENSIONS[i]);
        }
        return includes;
    }


    private void throwExceptionIfNeeded(Map<String, File> missingPropertiesFiles) {
        if (missingPropertiesFiles.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("no properties file found for plugin(s):");
            for (String pluginName : missingPropertiesFiles.keySet()) {
                sb.append("\n\t");
                sb.append("'");
                sb.append(pluginName);
                sb.append("' (");
                sb.append(missingPropertiesFiles.get(pluginName));
                sb.append(")");
            }
            throw new GradleException(sb.toString());
        }
    }

}
