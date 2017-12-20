package org.shipkit.internal.gradle.util

/**
 * Created by arturskowronski on 20/12/17.
 */
class BranchUtils {

    public static String getHeadBranch(String forkRepositoryName, String headBranch) {
        return getUserOfForkRepo(forkRepositoryName) + ":" + headBranch
    }

    private static String getUserOfForkRepo(String forkRepositoryName) {
        return forkRepositoryName.split("/")[0]
    }
}
