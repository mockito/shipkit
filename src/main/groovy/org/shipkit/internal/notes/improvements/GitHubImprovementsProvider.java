package org.shipkit.internal.notes.improvements;

import org.shipkit.internal.notes.model.ContributionSet;
import org.shipkit.internal.notes.model.Improvement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

class GitHubImprovementsProvider implements ImprovementsProvider {

    private static final Logger LOG = LoggerFactory.getLogger(GitHubImprovementsProvider.class);
    private final String apiUrl;
    private final String readOnlyAuthToken;
    private final String repository;

    GitHubImprovementsProvider(String apiUrl, String repository, String readOnlyAuthToken) {
        this.apiUrl = apiUrl;
        this.repository = repository;
        this.readOnlyAuthToken = readOnlyAuthToken;
    }

    public Collection<Improvement> getImprovements(ContributionSet contributions, Collection<String> labels, boolean onlyPullRequests) {
        LOG.info("Parsing {} commits with {} tickets", contributions.getAllCommits().size(), contributions.getAllTickets().size());
        return new GitHubTicketFetcher().fetchTickets(apiUrl, repository, readOnlyAuthToken, contributions.getAllTickets(), labels, onlyPullRequests);
    }
}
