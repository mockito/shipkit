package org.shipkit.internal.gradle.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.*;

import java.io.File;
import java.util.Set;


public class PluginValidatorTask extends DefaultTask {

    @Optional
    @Input
    private SourceSet sourceSet;

    @TaskAction
    public void validate() {
        new PluginValidator(getProject()).validate(sourceSet);
    }

    public void setSourceSet(SourceSet sourceSet) {
        this.sourceSet = sourceSet;
    }

    public SourceSet getSourceSet() {
        return sourceSet;
    }
}
