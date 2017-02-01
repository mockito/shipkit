package org.mockito.release.notes.improvements;

import java.util.Collection;

/**
 * Simple POJO that contains all the information of an improvement
 */
public class Improvement {

    private final long id; //TODO SF String
    private final String title;
    private final String url;
    private final Collection<String> labels;

    public Improvement(long id, String title, String url, Collection<String> labels) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.labels = labels;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public Collection<String> getLabels() {
        return labels;
    }
}
