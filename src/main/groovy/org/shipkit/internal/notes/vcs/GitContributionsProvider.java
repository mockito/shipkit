package org.shipkit.internal.notes.vcs;

import org.shipkit.internal.notes.model.Commit;
import org.shipkit.internal.notes.model.ContributionSet;
import org.shipkit.internal.notes.util.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;

class GitContributionsProvider implements ContributionsProvider {

    private static final Logger LOG = LoggerFactory.getLogger(GitContributionsProvider.class);
    private final GitLogProvider logProvider;
    private final Predicate<Commit> ignoredCommit;

    GitContributionsProvider(GitLogProvider logProvider, Predicate<Commit> ignoredCommit) {
        this.logProvider = logProvider;
        this.ignoredCommit = ignoredCommit;
    }

    public ContributionSet getContributionsBetween(String fromRev, String toRev) {
        LOG.info("Fetching {} from the repo", fromRev);

        Collection<Commit> commits = getCommits(fromRev, toRev);

        DefaultContributionSet contributions = new DefaultContributionSet();
        for (Commit commit : commits) {
            if (!ignoredCommit.isTrue(commit)) {
                contributions.add(commit);
            }
        }
        return contributions;
    }

    private Collection<Commit> getCommits(String fromRev, String toRev) {
        LOG.info("Loading all commits between {} and {}", fromRev, toRev);

        LinkedList<Commit> commits = new LinkedList<Commit>();
        String commitToken = "@@commit@@";
        String infoToken = "@@info@@";
        // %H: commit hash
        // %ae: author email
        // %an: author name
        // %B: raw body (unwrapped subject and body)
        // %N: commit notes
        String log = logProvider.getLog(fromRev, toRev, "--pretty=format:%H" + infoToken + "%ae" + infoToken + "%an" + infoToken + "%B%N" + commitToken);

        for (String entry : log.split(commitToken)) {
            String[] entryParts = entry.split(infoToken);
            if (entryParts.length == 4) {
                String commitId = entryParts[0].trim();
                String email = entryParts[1].trim();
                String author = entryParts[2].trim();
                String message = entryParts[3].trim();
                LOG.info("Loaded commit - email: {}, author: {}, message (trimmed): {}", email, author, message.replaceAll("\n.*", ""));
                commits.add(new GitCommit(commitId, email, author, message));
            }
        }
        return commits;
    }
}
