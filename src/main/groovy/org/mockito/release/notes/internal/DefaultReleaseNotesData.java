package org.mockito.release.notes.internal;

import org.json.simple.Jsoner;
import org.mockito.release.notes.model.ContributionSet;
import org.mockito.release.notes.model.Improvement;
import org.mockito.release.notes.model.ReleaseNotesData;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

public class DefaultReleaseNotesData implements ReleaseNotesData {

    private static final String JSON_FORMAT = "{ \"version\": \"%s\", \"date\": \"%s\", \"contributions\": %s, " +
            "\"improvements\": [%s], \"previousVersionTag\": \"%s\" , \"thisVersionTag\": \"%s\" }";

    private final String version;
    private final Date date;
    private final ContributionSet contributions;
    private final Collection<Improvement> improvements;
    private final String previousVersionTag;
    private final String thisVersionTag;

    public DefaultReleaseNotesData(String version, Date date, ContributionSet contributions,
                                   Collection<Improvement> improvements, String previousVersionTag, String thisVersionTag) {

        this.version = version;
        this.date = date;
        this.contributions = contributions;
        this.improvements = improvements;
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
    public String toJson() {
        final StringBuilder improvementsBuilder = new StringBuilder();
        final Iterator<Improvement> iterator = improvements.iterator();
        while (iterator.hasNext()) {
            improvementsBuilder.append(iterator.next().toJson());
            if (iterator.hasNext()) {
                improvementsBuilder.append(",");
            }
        }
        return String.format(JSON_FORMAT,
                Jsoner.escape(version),
                Jsoner.escape(String.valueOf(date.getTime())),
                contributions.toJson(),
                improvementsBuilder.toString(),
                Jsoner.escape(previousVersionTag),
                Jsoner.escape(thisVersionTag)
        );
    }

    @Override
    public void toJson(Writer writable) throws IOException {
        writable.append(toJson());
    }
}
