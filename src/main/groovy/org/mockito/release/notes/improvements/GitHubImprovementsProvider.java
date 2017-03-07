package org.mockito.release.notes.improvements;

import org.mockito.release.notes.model.ContributionSet;
import org.mockito.release.notes.model.Improvement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

class GitHubImprovementsProvider implements ImprovementsProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubImprovementsProvider.class);
    private final String authToken;
    private final String repository;

    GitHubImprovementsProvider(String repository, String authToken) {
        this.repository = repository;
        this.authToken = authToken;
    }

    public Collection<Improvement> getImprovements(ContributionSet contributions, Collection<String> labels, boolean onlyPullRequests) {
        LOGGER.info("Parsing {} commits with {} tickets", contributions.getAllCommits().size(), contributions.getAllTickets().size());
        return new GitHubTicketFetcher().fetchTickets(repository, authToken, contributions.getAllTickets(), labels, onlyPullRequests);
    }
}
