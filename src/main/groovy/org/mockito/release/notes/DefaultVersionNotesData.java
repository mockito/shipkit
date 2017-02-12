package org.mockito.release.notes;

import org.mockito.release.notes.model.ContributionSet;
import org.mockito.release.notes.model.Improvement;
import org.mockito.release.notes.model.VersionNotesData;

import java.util.Collection;
import java.util.Date;

//TODO SF move to internal package
public class DefaultVersionNotesData implements VersionNotesData {
    private final String version;
    private final Date date;
    private final ContributionSet contributions;
    private final Collection<Improvement> improvements;

    public DefaultVersionNotesData(String version, Date date, ContributionSet contributions,
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
