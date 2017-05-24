package org.mockito.release.notes.internal;

import org.json.simple.Jsoner;
import org.mockito.release.notes.model.Improvement;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;

/**
 * Simple POJO that contains all the information of an improvement
 */
public class DefaultImprovement implements Improvement {

    private static final String JSON_FORMAT = "{ \"id\": \"%s\", \"title\": \"%s\", \"url\": \"%s\", \"labels\": [%s], \"isPullRequest\": %s }";

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

    @Override
    public String toJson() {
        final StringBuilder labelsBuilder = new StringBuilder();
        final Iterator<String> iterator = labels.iterator();
        while (iterator.hasNext()) {
            labelsBuilder.append("\"" + Jsoner.escape(iterator.next()) + "\"");
            if (iterator.hasNext()) {
                labelsBuilder.append(",");
            }
        }
        return String.format(JSON_FORMAT,
                id.toString(),
                Jsoner.escape(title),
                Jsoner.escape(url),
                labelsBuilder.toString(),
                String.valueOf(isPullRequest));
    }

    @Override
    public void toJson(Writer writable) throws IOException {
        writable.append(toJson());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DefaultImprovement that = (DefaultImprovement) o;

        if (isPullRequest != that.isPullRequest) {
            return false;
        }
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (title != null ? !title.equals(that.title) : that.title != null) {
            return false;
        }
        if (url != null ? !url.equals(that.url) : that.url != null) {
            return false;
        }
        return labels != null ? labels.equals(that.labels) : that.labels == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (labels != null ? labels.hashCode() : 0);
        result = 31 * result + (isPullRequest ? 1 : 0);
        return result;
    }
}
