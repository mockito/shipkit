package org.mockito.release.internal.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.mockito.release.exec.Exec;
import org.mockito.release.exec.ProcessRunner;
import org.mockito.release.gradle.ReleaseConfiguration;
import org.mockito.release.internal.gradle.util.ReleaseNotesSerializer;
import org.mockito.release.notes.contributors.Contributors;
import org.mockito.release.notes.contributors.ContributorsSerializer;
import org.mockito.release.notes.contributors.ContributorsSet;
import org.mockito.release.notes.contributors.GitHubContributorsProvider;
import org.mockito.release.notes.model.Contribution;
import org.mockito.release.notes.model.ReleaseNotesData;
import org.mockito.release.notes.vcs.RevisionProvider;
import org.mockito.release.notes.vcs.Vcs;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Fetch info about contributors from GitHub and store it in file. It is used later in generation release notes.
 */
public class RecentContributorsFetcherTask extends DefaultTask {

    private static final Logger LOG = Logging.getLogger(RecentContributorsFetcherTask.class);

    @Input private String repository;
    @Input private String readOnlyAuthToken;
    @Input private String fromRevision;
    @Input private String toRevision;
    @InputFile private File releaseNotesData;

    @OutputFile private File outputFile;

    /**
     * Data file containing serialized {@link ReleaseNotesData}.
     */
    public File getReleaseNotesData() {
        return releaseNotesData;
    }

    /**
     * See {@link #getReleaseNotesData()}
     */
    public void setReleaseNotesData(File releaseNotesData) {
        this.releaseNotesData = releaseNotesData;
    }

    /**
     * See {@link ReleaseConfiguration.GitHub#getRepository()}
     */
    public String getRepository() {
        return repository;
    }

    /**
     * See {@link #getRepository()}
     */
    public void setRepository(String repository) {
        this.repository = repository;
    }

    /**
     * See {@link ReleaseConfiguration.GitHub#getReadOnlyAuthToken()}
     */
    public String getReadOnlyAuthToken() {
        return readOnlyAuthToken;
    }

    /**
     * See {@link #getReadOnlyAuthToken()}
     */
    public void setReadOnlyAuthToken(String readOnlyAuthToken) {
        this.readOnlyAuthToken = readOnlyAuthToken;
    }

    /**
     * Revision from which to iterate commits to find contributors
     * See also {@link #getToRevision()}
     */
    public String getFromRevision() {
        return fromRevision;
    }

    /**
     * See {@link #getFromRevision()}
     */
    public void setFromRevision(String fromRevision) {
        this.fromRevision = fromRevision;
    }

    /**
     * Revision to which to iterate commits to find contributors.
     * See also {@link #getFromRevision()}
     */
    public String getToRevision() {
        return toRevision;
    }

    /**
     * See {@link #getToRevision()}
     */
    public void setToRevision(String toRevision) {
        this.toRevision = toRevision;
    }

    /**
     * Output file to serialize contributors data
     */
    public File getOutputFile() {
        return outputFile;
    }

    /**
     * See {@link #getOutputFile()}
     */
    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    @TaskAction
    public void fetch() {
        LOG.lifecycle("  Fetching contributors information between revisions {}..{}", fromRevision, toRevision);
        ProcessRunner processRunner = Exec.getProcessRunner(getProject().getRootDir());

        RevisionProvider revisionProvider = Vcs.getRevisionProvider(processRunner);
        //GitHub commits API does not give us tag names, it only operates on the SHA
        //Therefore we need to map tag to SHA
        String fromRev = revisionProvider.getRevisionForTagOrRevision(fromRevision);

        Collection<ReleaseNotesData> data = new ReleaseNotesSerializer(releaseNotesData).deserialize();
        Collection<String> authorNames = getAuthorNames(data);

        GitHubContributorsProvider contributorsProvider = Contributors.getGitHubContributorsProvider(repository, readOnlyAuthToken);
        ContributorsSet contributors = contributorsProvider.mapContributorsToGitHubUser(authorNames, fromRev, toRevision);

        ContributorsSerializer contributorsSerializer = Contributors.getLastContributorsSerializer(outputFile);
        contributorsSerializer.serialize(contributors);
    }

    private static Collection<String> getAuthorNames(Collection<ReleaseNotesData> data) {
        Set<String> authors = new HashSet<String>();
        for (ReleaseNotesData d : data) {
            for (Contribution c : d.getContributions().getContributions()) {
                authors.add(c.getAuthorName());
            }
        }
        return authors;
    }
}
