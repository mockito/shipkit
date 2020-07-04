package org.shipkit.internal.notes.util;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.json.simple.DeserializationException;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;
import org.shipkit.internal.util.DateUtil;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

/**
 * This class contains a standard operation for fetching single page for GitHub.
 */
public class GitHubObjectFetcher {

    private static final Logger LOG = Logging.getLogger(GitHubObjectFetcher.class);
    //TODO GitHubObjectFetcher and GitHubListFetcher can probably be merged into one, there's code duplicated

    private final String authToken;

    public GitHubObjectFetcher(String authToken) {
        this.authToken = authToken;
    }

    public JsonObject getPage(String pageUrl) throws IOException, DeserializationException {
        URL url = new URL(pageUrl);
        LOG.info("GitHub API querying page {}", url);
        LOG.lifecycle("GET {}", url);
        URLConnection urlConnection = url.openConnection();
        urlConnection.setRequestProperty("Authorization", "token " + authToken);

        String resetInLocalTime = resetLimitInLocalTimeOrEmpty(urlConnection);

        LOG.info("GitHub API rate info => Remaining : {}, Limit : {}, Reset at: {}",
                urlConnection.getHeaderField("X-RateLimit-Remaining"),
                urlConnection.getHeaderField("X-RateLimit-Limit"),
                resetInLocalTime);

        return parseJsonFrom(urlConnection);
    }

    private String resetLimitInLocalTimeOrEmpty(URLConnection urlConnection) {
        String rateLimitReset = urlConnection.getHeaderField("X-RateLimit-Reset");
        if (rateLimitReset == null) {
            return "";
        }
        Date resetInEpochSeconds = DateUtil.parseDateInEpochSeconds(rateLimitReset);
        return DateUtil.formatDateToLocalTime(resetInEpochSeconds);
    }

    private JsonObject parseJsonFrom(URLConnection urlConnection) throws IOException, DeserializationException {
        InputStream response = urlConnection.getInputStream();

        String content = IOUtil.readFully(response);
        LOG.info("GitHub API responded successfully.");

        return (JsonObject) Jsoner.deserialize(content);
    }
}
