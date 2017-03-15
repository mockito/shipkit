package org.mockito.release.notes.internal;

import org.mockito.release.notes.contributors.ContributorsSet;
import org.mockito.release.notes.model.ContributionSet;
import org.mockito.release.notes.model.Improvement;
import org.mockito.release.notes.model.ReleaseNotesData;

import java.util.Collection;
import java.util.Date;

public class DefaultReleaseNotesData implements ReleaseNotesData {
    private final String version;
    private final Date date;
    private final ContributionSet contributions;
    private final Collection<Improvement> improvements;
    private final ContributorsSet contributors;
    private final String previousVersionTag;
    private final String thisVersionTag;

    public DefaultReleaseNotesData(String version, Date date, ContributionSet contributions,
                                   Collection<Improvement> improvements, ContributorsSet contributors, String previousVersionTag, String thisVersionTag) {

        this.version = version;
        this.date = date;
        this.contributions = contributions;
        this.improvements = improvements;
        this.contributors = contributors;
        this.previousVersionTag = previousVersionTag;
        this.thisVersionTag = thisVersionTag;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public ContributionSet getContributions() {
        return contributions;
    }

    @Override
    public Collection<Improvement> getImprovements() {
        return improvements;
    }

    @Override
    public String getVcsTag() {
        return thisVersionTag;
    }

    @Override
    public String getPreviousVersionVcsTag() {
        return previousVersionTag;
    }

    @Override
    public ContributorsSet getContributors() {
        return contributors;
    }
}
