package org.mockito.release.notes.vcs;

import org.mockito.release.notes.model.ContributionSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GitContributionsProvider implements ContributionsProvider {

    private static final Logger LOG = LoggerFactory.getLogger(GitContributionsProvider.class);
    private final GitLogProvider logProvider;
    private final IgnoredCommit ignoredCommit;

    GitContributionsProvider(GitLogProvider logProvider, IgnoredCommit ignoredCommit) {
        this.logProvider = logProvider;
        this.ignoredCommit = ignoredCommit;
    }

    public ContributionSet getContributionsBetween(String fromRev, String toRev) {
        LOG.info("Fetching {} from the repo", fromRev);

        LOG.info("Loading all commits between {} and {}", fromRev, toRev);

        String commitToken = "@@commit@@";
        String infoToken = "@@info@@";
        // %H: commit hash
        // %ae: author email
        // %an: author name
        // %B: raw body (unwrapped subject and body)
        // %N: commit notes
        String log = logProvider.getLog(fromRev, toRev, "--pretty=format:%H" + infoToken + "%ae" + infoToken + "%an" + infoToken + "%B%N" + commitToken);

        DefaultContributionSet contributions = new DefaultContributionSet(ignoredCommit);

        for (String entry : log.split(commitToken)) {
            String[] entryParts = entry.split(infoToken);
            if (entryParts.length == 4) {
                String commitId = entryParts[0].trim();
                String email = entryParts[1].trim();
                String author = entryParts[2].trim();
                String message = entryParts[3].trim();
                LOG.info("Loaded commit - email: {}, author: {}, message (trimmed): {}", email, author, message.replaceAll("\n.*", ""));
                contributions.add(new GitCommit(commitId, email, author, message));
            }
        }

        return contributions;
    }
}
