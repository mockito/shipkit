package org.shipkit.internal.notes.util;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.json.simple.DeserializationException;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;
import org.shipkit.internal.util.DateUtil;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;

/**
 * This class contains standard operations for skim over GitHub API responses.
 */
public class GitHubListFetcher {

    private static final Logger LOG = Logging.getLogger(GitHubListFetcher.class);

    private static final String RELATIVE_LINK_NOT_FOUND = "none";
    private final String readOnlyAuthToken;
    private String nextPageUrl;

    public GitHubListFetcher(String nextPageUrl, String readOnlyAuthToken) {
        this.nextPageUrl = nextPageUrl;
        this.readOnlyAuthToken = readOnlyAuthToken;
    }

    public boolean hasNextPage() {
        return !RELATIVE_LINK_NOT_FOUND.equals(nextPageUrl);
    }

    public List<JsonObject> nextPage() throws IOException, DeserializationException {
        if (RELATIVE_LINK_NOT_FOUND.equals(nextPageUrl)) {
            throw new IllegalStateException("GitHub API no more issues to fetch");
        }
        URL url = new URL(nextPageUrl);
        LOG.info("GitHub API querying page {}", queryParamValue(url, "page"));
        LOG.lifecycle("GET " + nextPageUrl);
        URLConnection urlConnection = url.openConnection();
        urlConnection.setRequestProperty("Authorization", "token " + readOnlyAuthToken);
        LOG.info("Established connection to GitHub API");

        String resetInLocalTime = resetLimitInLocalTimeOrEmpty(urlConnection);

        LOG.info("GitHub API rate info => Remaining : {}, Limit : {}, Reset at: {}",
                urlConnection.getHeaderField("X-RateLimit-Remaining"),
                urlConnection.getHeaderField("X-RateLimit-Limit"),
                resetInLocalTime);
        nextPageUrl = extractRelativeLink(urlConnection.getHeaderField("Link"), "next");

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

    private String queryParamValue(URL url, String page) {
        String query = url.getQuery();
        for (String param : query.split("&")) {
            if (param.startsWith(page)) {
                return param.substring(param.indexOf('=') + 1, param.length());
            }
        }
        return "N/A";
    }

    private List<JsonObject> parseJsonFrom(URLConnection urlConnection) throws IOException, DeserializationException {
        InputStream response = urlConnection.getInputStream();

        LOG.info("Reading remote stream from GitHub API");
        String content = IOUtil.readFully(response);
        LOG.info("GitHub API responded successfully.");
        @SuppressWarnings("unchecked")
        List<JsonObject> issues = (List<JsonObject>) Jsoner.deserialize(content);
        LOG.info("GitHub API returned {} Json objects.", issues.size());
        return issues;
    }


    private String extractRelativeLink(String linkHeader, final String relativeType) {
        if (linkHeader == null) {
            return RELATIVE_LINK_NOT_FOUND;
        }

        // See GitHub API doc : https://developer.github.com/guides/traversing-with-pagination/
        // Link: <https://api.github.com/repositories/6207167/issues?access_token=a0a4c0f41c200f7c653323014d6a72a127764e17&state=closed&filter=all&page=2>; rel="next",
        //       <https://api.github.com/repositories/62207167/issues?access_token=a0a4c0f41c200f7c653323014d6a72a127764e17&state=closed&filter=all&page=4>; rel="last"
        for (String linkRel : linkHeader.split(",")) {
            if (linkRel.contains("rel=\"" + relativeType + "\"")) {
                return linkRel.substring(
                        linkRel.indexOf("http"),
                        linkRel.indexOf(">; rel=\"" + relativeType + "\""));
            }
        }
        return RELATIVE_LINK_NOT_FOUND;
    }
}
