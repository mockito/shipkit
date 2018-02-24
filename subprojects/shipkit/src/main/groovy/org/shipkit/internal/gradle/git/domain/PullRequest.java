package org.shipkit.internal.gradle.git.domain;

/**
 * This class is representing Pull Request from GitHub. In spite of simplicity, only necessary fields are exposed from API.
 */
public class PullRequest {
    private String sha;
    private String ref;
    private String url;
    private int number;

    /**
     * Sha of the most recent commit in pull request
     */
    public String getSha() {
        return sha;
    }

    /**
     * See {@link #getSha()}
     */
    public void setSha(String sha) {
        this.sha = sha;
    }

    /**
     * Name of branch pull request was created from
     */
    public String getRef() {
        return ref;
    }

    /**
     * See {@link #getRef()}
     */
    public PullRequest setRef(String ref) {
        this.ref = ref;
        return this;
    }

    /**
     * Url pointing to the pull request in GitHub repository
     */
    public String getUrl() {
        return url;
    }

    /**
     * See {@link #getUrl()}
     */
    public void setUrl(String url) {
        this.url = url;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
