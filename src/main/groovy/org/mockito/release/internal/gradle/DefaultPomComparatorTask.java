package org.mockito.release.internal.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.mockito.release.gradle.notes.PomComparatorTask;

import java.io.File;

import static org.mockito.release.internal.gradle.FileUtil.readFile;

public class DefaultPomComparatorTask extends DefaultTask implements PomComparatorTask {

    private String remotePomUrl;
    private File localPom;
    private File resultsFile;

    @Input
    public String getRemotePomUrl() {
        return remotePomUrl;
    }

    public void setRemotePomUrl(String remotePomUrl) {
        this.remotePomUrl = remotePomUrl;
    }

    @Override
    @InputFile
    public File getLocalPom() {
        return localPom;
    }

    @Override
    public void setLocalPom(File localPom) {
        this.localPom = localPom;
    }

    @Override
    @OutputFile
    public File getResultsFile() {
        return resultsFile;
    }

    @Override
    public void setResultsFile(File resultsFile) {
        this.resultsFile = resultsFile;
    }

    @TaskAction public void comparePublications() throws Exception {
        String targetArtifact = getRemotePomUrl();
        getLogger().lifecycle("{} - about to compare current pom.xml with {}", getPath(), targetArtifact);

        Dependency dependency = getProject().getDependencies().create(targetArtifact);
        Configuration configuration = getProject().getConfigurations().detachedConfiguration(dependency);
//        DependencySubstitutions s = configuration.getResolutionStrategy().getDependencySubstitution();
//        s.substitute(s.project(":api")).with(s.module(""));
        File resolvedPom = configuration.getSingleFile();

        //


        diffPoms(resolvedPom, localPom, getResultsFile());
    }

    //TODO unit test
    static void diffPoms(File resolvedPom, File localPom, File resultsFile) {
        String resolvedPomContent = readFile(resolvedPom);
        String localPomContent = readFile(localPom);
        boolean pomsEqual = replaceVersion(resolvedPomContent).equals(replaceVersion(localPomContent));

        if (pomsEqual) {
            FileUtil.writeFile(resultsFile, "");
        } else {
            FileUtil.writeFile(resultsFile, "Pom files are different!!!\n" +
                "------------------------------------------------------------\n" +
                localPom.getAbsolutePath() + "\n" +
                "------------------------------------------------------------\n" +
                localPomContent + "\n" +
                "------------------------------------------------------------\n" +
                resolvedPom.getAbsolutePath() + "\n" +
                "------------------------------------------------------------\n" +
                resolvedPomContent + "\n" +
                "------------------------------------------------------------\n");
        }
    }

    //TODO unit test
    static String replaceVersion(String pom) {
        return pom.replaceFirst("<version>(.*)</version>", "<version>0.0.0</version>");
    }
}