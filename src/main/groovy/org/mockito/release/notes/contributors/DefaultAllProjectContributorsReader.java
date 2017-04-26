package org.mockito.release.notes.contributors;

import java.io.File;

public class DefaultAllProjectContributorsReader implements AllProjectContributorsReader {

    @Override
    public ProjectContributorsSet loadAllContributors(String filePath) {
        return new AllContributorsSerializer(new File(filePath)).deserialize();
    }

}
