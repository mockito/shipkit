package org.mockito.release.notes.model;

import org.mockito.release.notes.improvements.DefaultImprovement;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

public interface ReleaseNotesData {

    String getVersion();
    Date getDate();
    ContributionSet getContributions();
    Collection<DefaultImprovement> getImprovements();
    Map<String, String> getLabels();

}
