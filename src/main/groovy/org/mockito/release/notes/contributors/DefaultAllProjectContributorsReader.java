package org.mockito.release.notes.contributors;

import org.mockito.release.notes.util.IOUtil;

import java.io.File;

public class DefaultAllProjectContributorsReader implements AllProjectContributorsReader {

    @Override
    public ProjectContributorsSet loadAllContributors(String filePath) {
        final File file = new File(filePath);
        final String fileContent = IOUtil.readFully(file);
        return new AllContributorsSerializer().deserialize(fileContent);
    }

}
