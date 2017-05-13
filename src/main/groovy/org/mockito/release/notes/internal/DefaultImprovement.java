package org.mockito.release.notes.internal;

import org.mockito.release.notes.model.Improvement;

import java.util.Collection;

/**
 * Simple POJO that contains all the information of an improvement
 */
public class DefaultImprovement implements Improvement {

    private final Long id;
    private final String title;
    private final String url;
    private final Collection<String> labels;
    private final boolean isPullRequest;

    public DefaultImprovement(Long id, String title, String url, Collection<String> labels, boolean isPullRequest) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.labels = labels;
        this.isPullRequest = isPullRequest;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public Collection<String> getLabels() {
        return labels;
    }

    @Override
    public boolean isPullRequest() {
        return isPullRequest;
    }

    @Override
    public String toString() {
        return "DefaultImprovement{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", labels=" + labels +
                ", isPullRequest=" + isPullRequest +
                '}';
    }
}
