package org.mockito.release.notes.improvements;

import org.mockito.release.notes.model.Improvement;

import java.util.Collection;

/**
 * Simple POJO that contains all the information of an improvement
 */
public class DefaultImprovement implements Improvement {

    private final long id;
    private final String title;
    private final String url;
    private final Collection<String> labels;

    public DefaultImprovement(long id, String title, String url, Collection<String> labels) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.labels = labels;
    }

    @Override
    public long getId() {
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
}
