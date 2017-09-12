package org.shipkit.internal.util;

import org.shipkit.internal.gradle.util.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RepositoryNameUtil {

    /**
     * Formats repositoryName to camel case version of it with the first letter capitalized. Eg.
     * "mockito/shipkit" -> "MockitoShipkit"
     * "mockito/shipkit-example" -> "MockitoShipkitExample"
     * @param repositoryName GitHub repo name in format "org/repo", eg. "mockito/shipkit"
     */
    public static String repositoryNameToCapitalizedCamelCase(String repositoryName) {
        return StringUtil.capitalize(repositoryNameToCamelCase(repositoryName));
    }


    /**
     * Formats repositoryName to camel case version of it. Eg.
     * "mockito/shipkit" -> "mockitoShipkit"
     * "mockito/shipkit-example" -> "mockitoShipkitExample"
     * @param repositoryName GitHub repo name in format "org/repo", eg. "mockito/shipkit"
     */
    public static String repositoryNameToCamelCase(String repositoryName) {
        Matcher matcher = Pattern.compile("[/_-]([a-z])").matcher(repositoryName);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(result, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * Extracts only repo part of the GitHub repo URL, eg.
     * "https://github.com/mockito/shipkit" -> "mockito/shipkit"
     * @param gitHubRepoUrl, full GitHub repo url, eg. "https://github.com/mockito/shipkit"
     */
    public static String extractRepoNameFromGitHubUrl(String gitHubRepoUrl) {
        String trimmed = gitHubRepoUrl.trim();

        String[] urlParts = trimmed.split("/");
        int last = urlParts.length - 1;
        return urlParts[last - 1] + "/" + urlParts[last];
    }
}
