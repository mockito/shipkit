package org.mockito.release.internal.gradle.util;

import com.jfrog.bintray.gradle.BintrayExtension;

import java.text.MessageFormat;

/**
 * Bintray specific utilities
 */
public class BintrayUtil {

    /**
     * Constructs markdown link to bintray repo.
     * Useful to print in release notes.
     *
     * @param bintray
     * @return markdown link
     */
    public static String getMarkdownRepoLink(BintrayExtension bintray) {
        String repo = bintray.getPkg().getRepo();
        String pkg = bintray.getPkg().getName();
        String org = bintray.getPkg().getUserOrg();
        if (org == null) {
            org = bintray.getUser();
        }
        return MessageFormat.format("[{1}/{2}](https://bintray.com/{0}/{1}/{2})", org, repo, pkg);
    }
}
