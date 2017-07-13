package org.shipkit.internal.gradle.init.tasks;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.gradle.init.InitTravisTask;
import org.shipkit.internal.notes.util.IOUtil;

import java.io.InputStream;

public class InitTravis {

    private final static Logger LOG = Logging.getLogger(InitTravisTask.class);

    public void initTravis(InitTravisTask task) {
        if (task.getOutputFile().exists()) {
            LOG.lifecycle("  {} - file exists, skipping generation of '{}'.", task.getPath(), task.getOutputFile().getName());
            return;
        }
        InputStream resource = this.getClass().getClassLoader().getResourceAsStream("template.travis.yml");
        String template = IOUtil.readFully(resource);
        IOUtil.writeFile(task.getOutputFile(), template);
        LOG.lifecycle("  {} - generated default '{}', don't forget to check it in to your source control!",
            task.getPath(), task.getOutputFile().getName());
    }
}
