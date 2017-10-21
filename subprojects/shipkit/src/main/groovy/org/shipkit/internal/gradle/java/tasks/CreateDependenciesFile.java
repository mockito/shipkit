package org.shipkit.internal.gradle.java.tasks;

import java.util.Arrays;
import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.gradle.util.StringUtil;
import org.shipkit.internal.notes.util.IOUtil;

import java.io.File;

public class CreateDependenciesFile extends DefaultTask {

    @Input
    private Configuration configuration;
    @OutputFile
    private File outputFile;

    @TaskAction public void createFile() {

        StringBuilder content = new StringBuilder();

        //TODO: remove sibling dependencies with the same version as the one being built
        for (ResolvedArtifact artifact : configuration.getResolvedConfiguration().getResolvedArtifacts()) {
            String artifactString = StringUtil.join(
                Arrays.asList(artifact.getName(), artifact.getClassifier(), artifact.getExtension(), artifact.getType()),
                ":"
            );
            ModuleVersionIdentifier dependency = artifact.getModuleVersion().getId();

            String dependencyString = StringUtil.join(
                Arrays.asList(dependency.getGroup(), dependency.getName(), dependency.getVersion()),
                ":"
            );

            content.append(artifactString).append("/").append(dependencyString).append("\n");
        }

        IOUtil.writeFile(outputFile, content.toString());
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }
}
