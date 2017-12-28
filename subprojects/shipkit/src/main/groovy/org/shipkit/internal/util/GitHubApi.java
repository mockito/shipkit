package org.shipkit.internal.util;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.internal.notes.util.IOUtil;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Wrapper for making REST requests to GitHub API
 */
public class GitHubApi {

    private static final Logger LOG = Logging.getLogger(GitHubApi.class);

    private final String gitHubApiUrl;
    private final String authToken;

    public GitHubApi(String gitHubApiUrl, String authToken) {
        this.gitHubApiUrl = gitHubApiUrl;
        this.authToken = authToken;
    }

    public String post(String relativeUrl, String body) throws IOException {
        URL url = new URL(gitHubApiUrl + relativeUrl);

        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "token " + authToken);

        DataOutputStream wr = null;
        try {
            wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(body);
            wr.flush();
        } finally {
            if (wr != null) {
                wr.close();
            }
        }

        return call("POST", conn);
    }

    public String get(String relativeUrl) throws IOException {
        URL url = new URL(gitHubApiUrl + relativeUrl);

        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "token " + authToken);

        return call("GET", conn);
    }

    private String call(String method, HttpsURLConnection conn) throws IOException {
        LOG.info("  Calling {} {}. Turn on debug logging to see response headers.", method, conn.getURL());

        if (conn.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
            return IOUtil.readFully(conn.getInputStream());
        } else {
            String errorMessage =
                String.format("%s %s failed, response code = %s, response body:\n%s",
                    method, conn.getURL(), conn.getResponseCode(), IOUtil.readFully(conn.getErrorStream()));
            throw new IOException(errorMessage);
        }
    }
}
