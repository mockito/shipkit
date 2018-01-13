package org.shipkit.internal.gradle.notes.tasks;

import org.shipkit.internal.notes.contributors.DefaultProjectContributorsSet;
import org.shipkit.internal.notes.contributors.ProjectContributorsSet;
import org.shipkit.internal.notes.model.Contributor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ContributorsMapBuilder {

    private Collection<String> contributorsFromConfiguration = Collections.emptyList();
    private ProjectContributorsSet contributorsFromGitHub = new DefaultProjectContributorsSet();
    private Collection<String> developers = Collections.emptyList();
    private final String githubUrl;

    public ContributorsMapBuilder(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public ContributorsMapBuilder withContributorsFromConfiguration(Collection<String> contributorsFromConfiguration) {
        this.contributorsFromConfiguration = contributorsFromConfiguration;
        return this;
    }

    public ContributorsMapBuilder withContributorsFromGitHub(ProjectContributorsSet contributorsFromGitHub) {
        this.contributorsFromGitHub = contributorsFromGitHub;
        return this;
    }

    public ContributorsMapBuilder withDevelopers(Collection<String> developers) {
        this.developers = developers;
        return this;
    }

    public Map<String, Contributor> build() {
        Map<String, Contributor> out = new HashMap<String, Contributor>();
        out.putAll(new StringToDefaultContributorTransformer(githubUrl).transform(contributorsFromConfiguration));
        out.putAll(new ProjectContributorToContributorTransformer().transform(contributorsFromGitHub.getAllContributors()));
        out.putAll(new StringToDefaultContributorTransformer(githubUrl).transform(developers));
        return out;
    }
}
