package org.mockito.release.gradle;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * TODO javadoc
 */
public class ReleaseConfiguration {

    private final GitHub gitHub = new GitHub();
    private final ReleaseNotes releaseNotes = new ReleaseNotes();
    private final Git git = new Git();
    private final Library library = new Library();
    private final Bintray bintray = new Bintray();

    private boolean dryRun;

    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public GitHub getGitHub() {
        return gitHub;
    }

    public ReleaseNotes getReleaseNotes() {
        return releaseNotes;
    }

    public Git getGit() {
        return git;
    }

    public Library getLibrary() {
        return library;
    }

    public Bintray getBintray() {
        return bintray;
    }

    public static class GitHub {

        private String repository;
        private String user;
        private String readOnlyAuthToken;

        public String getRepository() {
            return repository;
        }

        public void setRepository(String repository) {
            this.repository = repository;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getReadOnlyAuthToken() {
            return readOnlyAuthToken;
        }

        public void setReadOnlyAuthToken(String readOnlyAuthToken) {
            this.readOnlyAuthToken = readOnlyAuthToken;
        }
    }

    public static class ReleaseNotes {
        private File file;
        private File notableFile;
        private Map<String, String> labelMapping = new LinkedHashMap<String, String>();

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public File getNotableFile() {
            return notableFile;
        }

        public void setNotableFile(File notableFile) {
            this.notableFile = notableFile;
        }

        public Map<String, String> getLabelMapping() {
            return labelMapping;
        }

        public void setLabelMapping(Map<String, String> labelMapping) {
            this.labelMapping = labelMapping;
        }
    }

    public static class Git {
        private String user;
        private String email;
        private String releasableBranchRegex;

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getReleasableBranchRegex() {
            return releasableBranchRegex;
        }

        public void setReleasableBranchRegex(String releasableBranchRegex) {
            this.releasableBranchRegex = releasableBranchRegex;
        }
    }

    public static class Library {
        private Collection<String> developers = new LinkedList<String>();
        private Collection<String> contributors = new LinkedList<String>();

        public Collection<String> getDevelopers() {
            return developers;
        }

        public void setDevelopers(Collection<String> developers) {
            this.developers = developers;
        }

        public Collection<String> getContributors() {
            return contributors;
        }

        public void setContributors(Collection<String> contributors) {
            this.contributors = contributors;
        }
    }

    public static class Bintray {
        private String apiKey;

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }
    }
}