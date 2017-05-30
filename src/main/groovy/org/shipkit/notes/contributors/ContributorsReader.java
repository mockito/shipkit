package org.shipkit.notes.contributors;

public interface ContributorsReader {
    ContributorsSet loadContributors(String filePath, String fromRev, String toRevision);
}
