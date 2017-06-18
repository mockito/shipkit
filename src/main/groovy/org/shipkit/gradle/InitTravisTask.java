package org.shipkit.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.notes.util.IOUtil;

import java.io.File;
import java.io.InputStream;

/**
 * Creates default '.travis.yml' file for shipping with Shipkit
 */
public class InitTravisTask extends DefaultTask {

    private final static Logger LOG = Logging.getLogger(InitTravisTask.class);

    @OutputFile private File outputFile;

    @TaskAction public void initTravis() {
        if (outputFile.exists()) {
            LOG.lifecycle("  {} - file exists, skipping generation of '{}'.", this.getPath(), outputFile.getName());
            return;
        }
        InputStream resource = this.getClass().getClassLoader().getResourceAsStream("template.travis.yml");
        String template = IOUtil.readFully(resource);
        IOUtil.writeFile(outputFile, template);
        LOG.lifecycle("  {} - generated default '{}', don't forget to check it in to your source control!", this.getPath(), outputFile.getName());
    }

    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }
}
