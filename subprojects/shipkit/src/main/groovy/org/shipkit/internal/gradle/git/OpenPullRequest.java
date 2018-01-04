package org.shipkit.internal.gradle.git;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class OpenPullRequest {
    private String sha;
    private String ref;
    private String url;

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OpenPullRequest that = (OpenPullRequest) o;

        return new EqualsBuilder()
            .append(sha, that.sha)
            .append(ref, that.ref)
            .append(url, that.url)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(sha)
            .append(ref)
            .append(url)
            .toHashCode();
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
