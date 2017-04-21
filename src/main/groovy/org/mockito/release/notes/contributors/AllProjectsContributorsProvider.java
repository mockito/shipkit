package org.mockito.release.notes.contributors;

public class AllProjectsContributorsProvider {

    public static AllProjectContributorsReader getAllProjectContributorsReader() {
        return new DefaultAllProjectContributorsReader();
    }
}
