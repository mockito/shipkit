package org.shipkit.internal.util;

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

    private final String gitHubApiUrl;
    private final String authToken;

    public GitHubApi(String gitHubApiUrl, String authToken){
        this.gitHubApiUrl = gitHubApiUrl;
        this.authToken = authToken;
    }

    public String post(String relativeUrl, String body) throws IOException {
        URL url = new URL(gitHubApiUrl + relativeUrl + "?access_token=" + authToken);

        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");

        DataOutputStream wr = null;
        try {
            wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(body);
            wr.flush();
        } finally {
            if(wr != null){
                wr.close();
            }
        }

        if (conn.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
            return IOUtil.readFully(conn.getInputStream());
        } else {
            String errorMessage =
                String.format("POST %s failed, response code = %s, response body:\n%s",
                maskUrl(url), conn.getResponseCode(), IOUtil.readFully(conn.getErrorStream()));
            throw new IOException(errorMessage);
        }
    }

    private String maskUrl(URL url){
        return url.toExternalForm().replace(authToken, "[SECRET]");
    }
}
