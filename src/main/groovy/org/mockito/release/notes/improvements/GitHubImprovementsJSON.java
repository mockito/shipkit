package org.mockito.release.notes.improvements;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.mockito.release.notes.internal.DefaultImprovement;
import org.mockito.release.notes.model.Improvement;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Provides means to parse JSONObjects returned from calling GitHub API.
 */
class GitHubImprovementsJSON {

    /**
     * Parses GitHub JSONObject in accordance to the API (https://developer.github.com/v3/issues/)
     */
    static Improvement toImprovement(JSONObject issue) {
        Long id = (Long) issue.get("number");
        String issueUrl = (String) issue.get("html_url");
        String title = (String) issue.get("title");
        boolean isPullRequest = issue.get("pull_request") != null;
        Collection<String> labels = extractLabels(issue);

        return new DefaultImprovement(id, title, issueUrl, labels, isPullRequest);
    }

    private static Collection<String> extractLabels(JSONObject issue) {
        Set<String> out = new LinkedHashSet<String>();
        JSONArray labels = (JSONArray) issue.get("labels");
        for (Object o : labels.toArray()) {
            JSONObject label = (JSONObject) o;
            out.add((String) label.get("name"));
        }
        return out;
    }
}
