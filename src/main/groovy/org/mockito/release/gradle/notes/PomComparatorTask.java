package org.mockito.release.gradle.notes;

import org.gradle.api.Task;

import java.io.File;

public interface PomComparatorTask extends Task {

    String getRemotePomUrl();

    void setRemotePomUrl(String remotePomUrl);

    File getLocalPom();

    void setLocalPom(File localPom);

    File getResultsFile();

    void setResultsFile(File resultsFile);
}