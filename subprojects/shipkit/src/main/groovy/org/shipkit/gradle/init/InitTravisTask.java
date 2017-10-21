package org.shipkit.gradle.init;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.gradle.init.tasks.InitTravis;

import java.io.File;

/**
 * Creates default '.travis.yml' file for shipping with Shipkit
 */
public class InitTravisTask extends DefaultTask {

    private File outputFile;

    @TaskAction public void initTravis() {
        new InitTravis().initTravis(this);
    }

    /**
     * Where the Travis file is generated to. Typically, it's ".travis.yml" in root directory of the project.
     */
    public File getOutputFile() {
        return outputFile;
    }

    /**
     * See {@link #getOutputFile()}
     */
    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }
}
