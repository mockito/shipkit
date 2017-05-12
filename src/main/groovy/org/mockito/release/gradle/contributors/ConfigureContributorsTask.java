package org.mockito.release.gradle.contributors;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;
import org.mockito.release.gradle.ReleaseConfiguration;
import org.mockito.release.notes.contributors.AllContributorsSerializer;
import org.mockito.release.notes.contributors.ProjectContributorsSet;
import org.mockito.release.notes.model.ProjectContributor;
import org.mockito.release.notes.util.IOUtil;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.release.internal.gradle.util.StringUtil.isEmpty;

/**
 * Configures {@link ReleaseConfiguration.Team#getContributors()}
 * based on serialized contributors data.
 * See also {@link org.mockito.release.internal.gradle.ContributorsPlugin}
 */
public class ConfigureContributorsTask extends DefaultTask {

    private final static Logger LOG = Logging.getLogger(ConfigureContributorsTask.class);

    @InputFile private File contributorsData;
    private ReleaseConfiguration releaseConfiguration;

    /**
     * Serialized contributors data used to populate {@link ReleaseConfiguration.Team#getContributors()}
     */
    public File getContributorsData() {
        return contributorsData;
    }

    /**
     * See {@link #getContributorsData()}
     */
    public void setContributorsData(File contributorsData) {
        this.contributorsData = contributorsData;
    }

    /**
     * Release configuration object to populate with contributors from {@link #getContributorsData()}
     */
    public ReleaseConfiguration getReleaseConfiguration() {
        return releaseConfiguration;
    }

    /**
     * See {@link #getReleaseConfiguration()}
     */
    public void setReleaseConfiguration(ReleaseConfiguration releaseConfiguration) {
        this.releaseConfiguration = releaseConfiguration;
    }

    @TaskAction public void configure() {
        ProjectContributorsSet all = new AllContributorsSerializer().deserialize(IOUtil.readFully(contributorsData));
        List<String> contributors = new LinkedList<String>();
        for (ProjectContributor c : all.getAllContributors()) {
            contributors.add(c.getLogin() + ":" + (isEmpty(c.getName())? c.getLogin() : c.getName()));
        }

        LOG.lifecycle("  Configuring {} contributors into 'releasing.team.contributors' setting.",
                contributors.size());
        releaseConfiguration.getTeam().setContributors(contributors);
    }
}
