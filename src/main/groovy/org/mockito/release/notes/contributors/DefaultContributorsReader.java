package org.mockito.release.notes.contributors;

public class DefaultContributorsReader implements ContributorsReader {

    @Override
    public ContributorsSet loadContributors(String filePath, String fromRev, String toRevision) {
        ContributorsSerializer serializer = new ContributorsSerializer(filePath);
        return serializer.desrialize();
    }
}
