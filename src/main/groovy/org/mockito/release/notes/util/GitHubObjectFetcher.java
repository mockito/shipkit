package org.mockito.release.notes.util;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.json.simple.DeserializationException;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;
import org.mockito.release.notes.internal.DateFormat;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

/**
 * This class contains a standerd operation for fetching single page for GitHub.
 */
public class GitHubObjectFetcher {

    private static final Logger LOG = Logging.getLogger(GitHubObjectFetcher.class);


    private final String pageUrl;
    private final String authToken;

    public GitHubObjectFetcher(String pageUrl, String authToken) {
        this.pageUrl = pageUrl;
        this.authToken = authToken;
    }

    public JsonObject getPage() throws IOException, DeserializationException {
        URL url = new URL(String.format("%s%s%s", pageUrl, "?access_token=", authToken));
        LOG.info("GitHub API querying page {}", url);

        URLConnection urlConnection = url.openConnection();

        Date resetInEpochSeconds = DateFormat.parseDateInEpochSeconds(urlConnection.getHeaderField("X-RateLimit-Reset"));
        String resetInLocalTime = DateFormat.formatDateToLocalTime(resetInEpochSeconds);

        LOG.info("GitHub API rate info => Remaining : {}, Limit : {}, Reset at: {}",
                urlConnection.getHeaderField("X-RateLimit-Remaining"),
                urlConnection.getHeaderField("X-RateLimit-Limit"),
                resetInLocalTime);

        return parseJsonFrom(urlConnection);
    }

    private JsonObject parseJsonFrom(URLConnection urlConnection) throws IOException, DeserializationException {
        InputStream response = urlConnection.getInputStream();

        String content = IOUtil.readFully(response);
        LOG.info("GitHub API responded successfully.");

        return (JsonObject) Jsoner.deserialize(content);
    }
}
