package org.mockito.release.notes;

import org.mockito.release.notes.improvements.DefaultImprovement;
import org.mockito.release.notes.model.ContributionSet;
import org.mockito.release.notes.model.Improvement;
import org.mockito.release.notes.model.ReleaseNotesData;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

//TODO SF move to internal package
public class DefaultReleaseNotesData implements ReleaseNotesData {
    private final String version;
    private final Date date;
    private final ContributionSet contributions;
    private final Collection<Improvement> improvements;

    public DefaultReleaseNotesData(String version, Date date, ContributionSet contributions,
                                   Collection<Improvement> improvements) {

        this.version = version;
        this.date = date;
        this.contributions = contributions;
        this.improvements = improvements;
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
}
