package org.shipkit.internal.gradle.init.tasks;

import org.shipkit.gradle.init.InitTravisTask;
import org.shipkit.internal.notes.util.IOUtil;

import java.io.InputStream;

public class InitTravis {

    public void initTravis(InitTravisTask task) {
        String relativePath = task.getProject().getRootProject().relativePath(task.getOutputFile());
        if (task.getOutputFile().exists()) {
            InitMessages.skipping(relativePath);
            return;
        }
        InputStream resource = this.getClass().getClassLoader().getResourceAsStream("template.travis.yml");
        String template = IOUtil.readFully(resource);
        IOUtil.writeFile(task.getOutputFile(), template);
        InitMessages.generated(relativePath);
    }
}
