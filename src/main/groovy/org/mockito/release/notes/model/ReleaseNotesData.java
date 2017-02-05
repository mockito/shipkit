package org.mockito.release.notes.model;

import org.mockito.release.notes.improvements.Improvement;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

public interface ReleaseNotesData {

    String getVersion();
    Date getDate();
    ContributionSet getContributions();
    Collection<Improvement> getImprovements();
    Map<String, String> getLabels();

}
