package org.shipkit.notes.contributors;

import java.io.File;

public class DefaultContributorsReader implements ContributorsReader {

    @Override
    public ContributorsSet loadContributors(String filePath, String fromRev, String toRevision) {
        ContributorsSerializer serializer = new ContributorsSerializer(new File(filePath));
        return serializer.deserialize();
    }
}
