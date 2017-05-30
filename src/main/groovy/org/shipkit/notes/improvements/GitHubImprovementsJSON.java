package org.shipkit.notes.improvements;

import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.shipkit.notes.internal.DefaultImprovement;
import org.shipkit.notes.model.Improvement;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Provides means to parse JsonObjects returned from calling GitHub API.
 */
class GitHubImprovementsJSON {

    /**
     * Parses GitHub JsonObject in accordance to the API (https://developer.github.com/v3/issues/)
     */
    static Improvement toImprovement(JsonObject issue) {
        BigDecimal id = (BigDecimal) issue.get("number");
        String issueUrl = (String) issue.get("html_url");
        String title = (String) issue.get("title");
        boolean isPullRequest = issue.get("pull_request") != null;
        Collection<String> labels = extractLabels(issue);

        return new DefaultImprovement(id.longValue(), title, issueUrl, labels, isPullRequest);
    }

    private static Collection<String> extractLabels(JsonObject issue) {
        Set<String> out = new LinkedHashSet<String>();
        JsonArray labels = (JsonArray) issue.get("labels");
        for (Object o : labels.toArray()) {
            JsonObject label = (JsonObject) o;
            out.add((String) label.get("name"));
        }
        return out;
    }
}
