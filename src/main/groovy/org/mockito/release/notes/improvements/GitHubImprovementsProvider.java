package org.mockito.release.notes.improvements;

import org.mockito.release.notes.model.ContributionSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

class GitHubImprovementsProvider implements ImprovementsProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubImprovementsProvider.class);
    private final String authToken;

    GitHubImprovementsProvider(String authToken) {
        this.authToken = authToken;
    }

    public Collection<Improvement> getImprovements(ContributionSet contributions, Map<String, String> labels) {
        LOGGER.info("Parsing {} commits with {} tickets", contributions.getAllCommits().size(), contributions.getAllTickets().size());
        return new GitHubTicketFetcher().fetchTickets(authToken, contributions.getAllTickets());
    }
}
