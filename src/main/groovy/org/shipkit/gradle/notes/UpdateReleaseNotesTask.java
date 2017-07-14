package org.shipkit.gradle.notes;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.gradle.ReleaseConfiguration;
import org.shipkit.internal.gradle.util.FileUtil;
import org.shipkit.internal.gradle.util.ReleaseNotesSerializer;
import org.shipkit.internal.gradle.util.team.TeamMember;
import org.shipkit.internal.gradle.util.team.TeamParser;
import org.shipkit.internal.notes.contributors.AllContributorsSerializer;
import org.shipkit.internal.notes.contributors.DefaultContributor;
import org.shipkit.internal.notes.contributors.DefaultProjectContributorsSet;
import org.shipkit.internal.notes.contributors.ProjectContributorsSet;
import org.shipkit.internal.notes.format.ReleaseNotesFormatters;
import org.shipkit.internal.notes.model.Contributor;
import org.shipkit.internal.notes.model.ProjectContributor;
import org.shipkit.internal.notes.model.ReleaseNotesData;
import org.shipkit.internal.notes.util.IOUtil;
import org.shipkit.internal.util.ExposedForTesting;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Generates incremental, detailed release notes text.
 * that can be appended to the release notes file or displayed in console as a preview.
 */
public class UpdateReleaseNotesTask extends DefaultTask {

    private static final Logger LOG = Logging.getLogger(UpdateReleaseNotesTask.class);

    private String previousVersion;
    private File releaseNotesFile;
    private String gitHubUrl;
    private String gitHubRepository;
    private Map<String, String> gitHubLabelMapping = new LinkedHashMap<String, String>();
    private String publicationRepository;
    private File releaseNotesData;
    private Collection<String> developers;
    private Collection<String> contributors;
    private File contributorsDataFile;
    private boolean emphasizeVersion;
    private String version;
    private String tagPrefix;
    private boolean previewMode;

    private IncrementalNotesGenerator incrementalNotesGenerator = new IncrementalNotesGenerator();

    /**
     * Generates incremental release notes and appends it to the top of release notes file.
     * Run with -Ppreview if you only want to see preview of generated release notes, without appending them to the file.
     */
    @TaskAction
    public void updateReleaseNotes() {
        String newContent = getNewContent();
        if (previewMode){
            LOG.lifecycle("  Preview of release notes update:\n" +
                    "  ----------------\n" + newContent + "----------------");
        } else{
            FileUtil.appendToTop(newContent, getReleaseNotesFile());
            LOG.lifecycle("  Successfully updated release notes!");
        }
    }

    /**
     * @return true if task is configured to generate only preview of release notes (without appending them to the file), and false otherwise
     */
    public boolean isPreviewMode() {
        return previewMode;
    }

    /**
     * See {@link #isPreviewMode()} ()}
     */
    public void setPreviewMode(boolean previewMode) {
        this.previewMode = previewMode;
    }

    /**
     * Release notes file this task operates on.
     */
    public File getReleaseNotesFile() {
        return releaseNotesFile;
    }

    /**
     * See {@link #getReleaseNotesFile()}
     */
    public void setReleaseNotesFile(File releaseNotesFile) {
        this.releaseNotesFile = releaseNotesFile;
    }

    /**
     * The version we are generating the release notes for.
     */
    @Input
    public String getVersion() {
        return version;
    }

    /**
     * See {@link #getVersion()}
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * See {@link ReleaseConfiguration.Git#getTagPrefix()}
     */
    @Input
    public String getTagPrefix() {
        return tagPrefix;
    }

    /**
     * See {@link #getTagPrefix()}
     */
    public void setTagPrefix(String tagPrefix) {
        this.tagPrefix = tagPrefix;
    }

    /**
     * GitHub URL address, for example: https://github.com
     * See {@link ReleaseConfiguration.GitHub#getUrl()}
     */
    @Input
    public String getGitHubUrl() {
        return gitHubUrl;
    }

    /**
     * See {@link #getGitHubUrl()}
     */
    public void setGitHubUrl(String gitHubUrl) {
        this.gitHubUrl = gitHubUrl;
    }

    /**
     * Name of the GitHub repository in format "user|org/repository",
     * for example: "mockito/mockito"
     */
    @Input
    public String getGitHubRepository() {
        return gitHubRepository;
    }

    /**
     * See {@link #getGitHubRepository()}
     */
    public void setGitHubRepository(String gitHubRepository) {
        this.gitHubRepository = gitHubRepository;
    }

    /**
     * Issue tracker label mappings.
     * The mapping of "GitHub label" to human readable and presentable name.
     * The order of labels is important and will influence the order
     * in which groups of issues are generated in release notes.
     * Examples: ['java-9': 'Java 9 support', 'BDD': 'Behavior-Driven Development support']
     */
    @Input
    @Optional
    public Map<String, String> getGitHubLabelMapping() {
        return gitHubLabelMapping;
    }

    /**
     * See {@link #getGitHubLabelMapping()}
     */
    public void setGitHubLabelMapping(Map<String, String> gitHubLabelMapping) {
        this.gitHubLabelMapping = gitHubLabelMapping;
    }

    /**
     * The target repository where the publications / binaries are published to.
     * Shown in the release notes.
     */
    @Input
    public String getPublicationRepository() {
        return publicationRepository;
    }

    /**
     * See {@link #getPublicationRepository()}
     */
    public void setPublicationRepository(String publicationRepository) {
        this.publicationRepository = publicationRepository;
    }

    /**
     * Previous released version we generate the release notes from.
     */
    @Input
    @Optional
    public String getPreviousVersion() {
        return previousVersion;
    }

    /**
     * See {@link #getPreviousVersion()}
     */
    public void setPreviousVersion(String previousVersion) {
        this.previousVersion = previousVersion;
    }

    /**
     * Input to the release notes generation,
     * serialized release notes data objects of type {@link ReleaseNotesData}.
     * They are used to generate formatted release notes.
     * The data file is generate by {@link FetchReleaseNotesTask}.
     */
    @InputFile
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
     * Developers as configured in {@link ReleaseConfiguration.Team#getDevelopers()}
     */
    @Input public Collection<String> getDevelopers() {
        return developers;
    }

    /**
     * See {@link #getDevelopers()}
     */
    public void setDevelopers(Collection<String> developers) {
        this.developers = developers;
    }

    /**
     * Contributors as configured in {@link ReleaseConfiguration.Team#getContributors()}
     */
    @Input public Collection<String> getContributors() {
        return contributors;
    }

    /**
     * See {@link #getContributors()}
     */
    public void setContributors(Collection<String> contributors) {
        this.contributors = contributors;
    }

    /**
     * {@link #getContributorsDataFile()}
     */

    public void setContributorsDataFile(File contributorsDataFile) {
        this.contributorsDataFile = contributorsDataFile;
    }

    /**
     * File name from reads contributors from GitHub
     */
    @InputFile public File getContributorsDataFile() {
        return contributorsDataFile;
    }

    /**
     * {@link #isEmphasizeVersion()}
     */
    public void setEmphasizeVersion(boolean emphasizeVersion) {
        this.emphasizeVersion = emphasizeVersion;
    }

    /**
     * Should current version be emphasized in release notes
     */
    public boolean isEmphasizeVersion() {
        return emphasizeVersion;
    }

    private void assertConfigured() {
        if(!previewMode) { // releaseNotesFile is not needed in preview mode
            if (releaseNotesFile == null) {
                throw new GradleException("'" + this.getPath() + ".releaseNotesFile' must be configured.");
            }
            if(releaseNotesFile.exists() && !releaseNotesFile.isFile()){
                throw new GradleException("'" + this.getPath() + ".releaseNotesFile' must be a file.");
            }
            //TODO why do we need to create the file here? this method suppose to only validate
            if(!releaseNotesFile.exists()){
                try {
                    IOUtil.createParentDirectory(releaseNotesFile);
                    releaseNotesFile.createNewFile();
                } catch (Exception e) {
                    throw new GradleException("Failed to create file " + releaseNotesFile.getAbsolutePath() + ". You can create an empty file by yourself and restart the task." , e);
                }
            }
        }

    }

    /**
     * Generates new incremental content of the release notes.
     */
    String getNewContent() {
        assertConfigured();
        return incrementalNotesGenerator.generateNewContent();
    }

    @ExposedForTesting
    void setIncrementalNotesGenerator(IncrementalNotesGenerator incrementalNotesGenerator){
        this.incrementalNotesGenerator = incrementalNotesGenerator;
    }

    //TODO SF deduplicate and unit test
    static Map<String, Contributor> contributorsMap(Collection<String> contributorsFromConfiguration,
                                                    ProjectContributorsSet contributorsFromGitHub,
                                                    Collection<String> developers) {
        Map<String, Contributor> out = new HashMap<String, Contributor>();
        for (String contributor : contributorsFromConfiguration) {
            TeamMember member = TeamParser.parsePerson(contributor);
            out.put(member.name, new DefaultContributor(member.name, member.gitHubUser,
                    "http://github.com/" + member.gitHubUser));
        }
        for (ProjectContributor projectContributor : contributorsFromGitHub.getAllContributors()) {
            out.put(projectContributor.getName(), projectContributor);
        }
        for (String developer : developers) {
            TeamMember member = TeamParser.parsePerson(developer);
            out.put(member.name, new DefaultContributor(member.name, member.gitHubUser,
                    "http://github.com/" + member.gitHubUser));
        }
        return out;
    }

    class IncrementalNotesGenerator {
        public String generateNewContent() {
            LOG.lifecycle("  Building new release notes based on {}", releaseNotesFile);

            Collection<ReleaseNotesData> data = new ReleaseNotesSerializer().deserialize(IOUtil.readFully(releaseNotesData));

            String vcsCommitTemplate = getVcsCommitTemplate();

            ProjectContributorsSet contributorsFromGitHub;
            if(!contributors.isEmpty()) {
                // if contributors are defined in shipkit.team.contributors don't deserialize them from file
                contributorsFromGitHub = new DefaultProjectContributorsSet();
            } else {
                LOG.info("  Read project contributors from file " + contributorsDataFile.getAbsolutePath());
                contributorsFromGitHub = new AllContributorsSerializer().deserialize(IOUtil.readFully(contributorsDataFile));
            }

            Map<String, Contributor> contributorsMap = contributorsMap(contributors, contributorsFromGitHub, developers);
            String notes = ReleaseNotesFormatters.detailedFormatter(
                    "", gitHubLabelMapping, vcsCommitTemplate, publicationRepository, contributorsMap, emphasizeVersion)
                    .formatReleaseNotes(data);

            return notes + "\n\n";
        }
    }

    private String getVcsCommitTemplate() {
        if(previousVersion != null) {
            return gitHubUrl + "/" + gitHubRepository + "/compare/"
                    + tagPrefix + previousVersion + "..." + tagPrefix + version;
        } else{
            return "";
        }
    }
}
