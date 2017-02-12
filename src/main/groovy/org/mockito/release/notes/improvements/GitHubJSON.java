package org.mockito.release.notes.improvements;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.mockito.release.notes.internal.DefaultImprovement;
import org.mockito.release.notes.model.Improvement;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Provides means to parse JSONObjects returned from calling GitHub API.
 */
class GitHubJSON {

    static Improvement toImprovement(JSONObject issue) {
        Long id = (Long) issue.get("number");
        String issueUrl = (String) issue.get("html_url");
        String title = (String) issue.get("title");
        Collection<String> labels = extractLabels(issue);

        return new DefaultImprovement(id, title, issueUrl, labels);
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
