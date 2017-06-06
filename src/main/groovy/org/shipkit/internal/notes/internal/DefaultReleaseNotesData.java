package org.shipkit.internal.notes.internal;

import org.json.simple.Jsoner;
import org.shipkit.internal.notes.model.ContributionSet;
import org.shipkit.internal.notes.model.Improvement;
import org.shipkit.internal.notes.model.ReleaseNotesData;

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
                Jsoner.escape(previousVersionTag == null ? "" : previousVersionTag),
                Jsoner.escape(thisVersionTag)
        );
    }

    @Override
    public void toJson(Writer writable) throws IOException {
        writable.append(toJson());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DefaultReleaseNotesData that = (DefaultReleaseNotesData) o;

        if (version != null ? !version.equals(that.version) : that.version != null) {
            return false;
        }
        if (date != null ? !date.equals(that.date) : that.date != null) {
            return false;
        }
        if (contributions != null ? !contributions.equals(that.contributions) : that.contributions != null) {
            return false;
        }
        if (improvements != null ? !improvements.equals(that.improvements) : that.improvements != null) {
            return false;
        }
        if (previousVersionTag != null ? !previousVersionTag.equals(that.previousVersionTag) : that.previousVersionTag != null) {
            return false;
        }
        return thisVersionTag != null ? thisVersionTag.equals(that.thisVersionTag) : that.thisVersionTag == null;
    }

    @Override
    public int hashCode() {
        int result = version != null ? version.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (contributions != null ? contributions.hashCode() : 0);
        result = 31 * result + (improvements != null ? improvements.hashCode() : 0);
        result = 31 * result + (previousVersionTag != null ? previousVersionTag.hashCode() : 0);
        result = 31 * result + (thisVersionTag != null ? thisVersionTag.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DefaultReleaseNotesData{" +
                "version='" + version + '\'' +
                ", date=" + date +
                ", contributions=" + contributions +
                ", improvements=" + improvements +
                ", previousVersionTag='" + previousVersionTag + '\'' +
                ", thisVersionTag='" + thisVersionTag + '\'' +
                '}';
    }
}
