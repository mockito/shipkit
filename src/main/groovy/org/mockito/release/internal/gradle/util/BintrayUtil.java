package org.mockito.release.internal.gradle.util;

import com.jfrog.bintray.gradle.BintrayExtension;

import java.text.MessageFormat;

/**
 * Bintray specific utilities
 */
public class BintrayUtil {
    /**
     * Constructs link to bintray repository.
     *
     * @param bintray
     * @return repository link
     */
    public static String getRepoLink(BintrayExtension bintray) {
        String repo = bintray.getPkg().getRepo();
        String pkg = bintray.getPkg().getName();
        String org = bintray.getPkg().getUserOrg();
        if (org == null) {
            org = bintray.getUser();
        }
        return MessageFormat.format("https://bintray.com/{0}/{1}/{2}", org, repo, pkg);
    }
}
