package org.shipkit.notes.improvements;

import org.shipkit.notes.model.ContributionSet;
import org.shipkit.notes.model.Improvement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

class GitHubImprovementsProvider implements ImprovementsProvider {

    private static final Logger LOG = LoggerFactory.getLogger(GitHubImprovementsProvider.class);
    private final String readOnlyAuthToken;
    private final String repository;

    GitHubImprovementsProvider(String repository, String readOnlyAuthToken) {
        this.repository = repository;
        this.readOnlyAuthToken = readOnlyAuthToken;
    }

    public Collection<Improvement> getImprovements(ContributionSet contributions, Collection<String> labels, boolean onlyPullRequests) {
        LOG.info("Parsing {} commits with {} tickets", contributions.getAllCommits().size(), contributions.getAllTickets().size());
        return new GitHubTicketFetcher().fetchTickets(repository, readOnlyAuthToken, contributions.getAllTickets(), labels, onlyPullRequests);
    }
}
