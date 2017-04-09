package org.mockito.release.notes.contributors;

public class ContributorsLoader {

    public static ContributorsReader getContributorsReader() {
        return new DefaultContributorsReader();
    }
}
