package org.shipkit.internal.gradle.release;

public interface CiProvider {

    String getName();

    String getCommitMessage();

    boolean isPullRequest();

    String getBranch();

    String getBranchDescription();
}
