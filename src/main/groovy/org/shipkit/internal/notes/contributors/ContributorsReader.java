package org.shipkit.internal.notes.contributors;

public interface ContributorsReader {
    ContributorsSet loadContributors(String filePath, String fromRev, String toRevision);
}
