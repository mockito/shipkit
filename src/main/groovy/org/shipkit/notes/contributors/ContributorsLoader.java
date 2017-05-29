package org.shipkit.notes.contributors;

public class ContributorsLoader {

    public static ContributorsReader getContributorsReader() {
        return new DefaultContributorsReader();
    }
}
