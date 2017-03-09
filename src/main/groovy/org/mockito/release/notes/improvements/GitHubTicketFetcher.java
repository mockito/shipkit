package org.mockito.release.notes.improvements;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mockito.release.notes.model.Improvement;
import org.mockito.release.notes.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

class GitHubTicketFetcher {

    private static final Logger LOG = LoggerFactory.getLogger(GitHubTicketFetcher.class);

    Collection<Improvement> fetchTickets(String repository, String authToken, Collection<String> ticketIds, Collection<String> labels,
                                         boolean onlyPullRequests) {
        List<Improvement> out = new LinkedList<Improvement>();
        if (ticketIds.isEmpty()) {
            return out;
        }
        LOG.info("Querying GitHub API for {} tickets", ticketIds.size());

        Queue<Long> tickets = queuedTicketNumbers(ticketIds);

        try {
            GitHubIssues issues = GitHubIssues.forRepo(repository, authToken)
                    .state("closed")
                    .labels(CommaSeparated.commaSeparated(labels))
                    .filter("all")
                    .direction("desc")
                    .browse();

            while (!tickets.isEmpty() && issues.hasNextPage()) {
                List<JSONObject> page = issues.nextPage();

                out.addAll(extractImprovements(
                        dropTicketsAboveMaxInPage(tickets, page),
                        page, onlyPullRequests));
            }
        } catch (Exception e) {
            throw new RuntimeException("Problems fetching " + ticketIds.size() + " from GitHub", e);
        }
        return out;
    }

    private Queue<Long> dropTicketsAboveMaxInPage(Queue<Long> tickets, List<JSONObject> page) {
        if (page.isEmpty()) {
            return tickets;
        }
        Long highestId = (Long) page.get(0).get("number");
        while (!tickets.isEmpty() && tickets.peek() > highestId) {
            tickets.poll();
        }
        return tickets;
    }

    private Queue<Long> queuedTicketNumbers(Collection<String> ticketIds) {
        List<Long> tickets = new ArrayList<Long>();
        for (String id : ticketIds) {
            tickets.add(Long.parseLong(id));
        }
        Collections.sort(tickets);
        PriorityQueue<Long> longs = new PriorityQueue<Long>(tickets.size(), Collections.reverseOrder());
        longs.addAll(tickets);
        return longs;
    }

    private static List<Improvement> extractImprovements(Collection<Long> tickets, List<JSONObject> issues,
                                                         boolean onlyPullRequests) {
        if(tickets.isEmpty()) {
            return Collections.emptyList();
        }

        ArrayList<Improvement> pagedImprovements = new ArrayList<Improvement>();
        for (JSONObject issue : issues) {
            Improvement i = GitHubJSON.toImprovement(issue);
            if (tickets.remove(i.getId())) {
                if (!onlyPullRequests || i.isPullRequest()) {
                    pagedImprovements.add(i);
                }

                if (tickets.isEmpty()) {
                    return pagedImprovements;
                }
            }
        }
        return pagedImprovements;
    }

    private static class GitHubIssues {
        public static final String RELATIVE_LINK_NOT_FOUND = "none";
        private String nextPageUrl;

        private GitHubIssues(String repository, String authToken, String state, String filter, String labels, String direction) {
            // see API doc : https://developer.github.com/v3/issues/
            nextPageUrl = "https://api.github.com/repos/" + repository + "/issues?access_token=" + authToken
                    + (state == null? "" : "&state=" + state)
                    + (filter == null? "" : "&filter=" + filter)
                    + "&labels=" + labels
                    + (direction == null ? "" : "&direction=" + direction)
                    + "&page=1";
        }

        boolean hasNextPage() {
            return !RELATIVE_LINK_NOT_FOUND.equals(nextPageUrl);
        }

        List<JSONObject> nextPage() throws IOException {
            if(RELATIVE_LINK_NOT_FOUND.equals(nextPageUrl)) {
                throw new IllegalStateException("GitHub API no more issues to fetch");
            }
            URL url = new URL(nextPageUrl);
            LOG.info("GitHub API querying issue page {}", queryParamValue(url, "page"));
            URLConnection urlConnection = url.openConnection();

            LOG.info("GitHub API rate info => Remaining : {}, Limit : {}",
                    urlConnection.getHeaderField("X-RateLimit-Remaining"),
                    urlConnection.getHeaderField("X-RateLimit-Limit")
            );
            nextPageUrl = extractRelativeLink(urlConnection.getHeaderField("Link"));

            return parseJsonFrom(urlConnection);
        }

        private String queryParamValue(URL url, String page) {
            String query = url.getQuery();
            for (String param : query.split("&")) {
                if(param.startsWith(page)) {
                    return param.substring(param.indexOf('=') + 1, param.length());
                }
            }
            return "N/A";
        }

        private List<JSONObject> parseJsonFrom(URLConnection urlConnection) throws IOException {
            InputStream response = urlConnection.getInputStream();

            String content = IOUtil.readFully(response);
            LOG.info("GitHub API responded successfully.");
            @SuppressWarnings("unchecked")
            List<JSONObject> issues = (List<JSONObject>) JSONValue.parse(content);
            LOG.info("GitHub API returned {} issues.", issues.size());
            return issues;
        }

        private String extractRelativeLink(String linkHeader) {
            if (linkHeader == null) {
                return RELATIVE_LINK_NOT_FOUND;
            }

            // See GitHub API doc : https://developer.github.com/guides/traversing-with-pagination/
            // Link: <https://api.github.com/repositories/6207167/issues?access_token=a0a4c0f41c200f7c653323014d6a72a127764e17&state=closed&filter=all&page=2>; rel="next",
            //       <https://api.github.com/repositories/62207167/issues?access_token=a0a4c0f41c200f7c653323014d6a72a127764e17&state=closed&filter=all&page=4>; rel="last"
            for (String linkRel : linkHeader.split(",")) {
                if (linkRel.contains("rel=\"next\"")) {
                    return linkRel.substring(
                            linkRel.indexOf("http"),
                            linkRel.indexOf(">; rel=\"next\""));
                }
            }
            return RELATIVE_LINK_NOT_FOUND;
        }

        static GitHubIssuesBuilder forRepo(String repository, String authToken) {
            return new GitHubIssuesBuilder(repository, authToken);
        }

        private static class GitHubIssuesBuilder {
            private final String authToken;
            private final String repository;
            private String state;
            private String filter;
            private String direction;
            private String labels;

            GitHubIssuesBuilder(String repository, String authToken) {
                this.repository = repository;
                this.authToken = authToken;
            }

            GitHubIssuesBuilder state(String state) {
                this.state = state;
                return this;
            }

            GitHubIssuesBuilder filter(String filter) {
                this.filter = filter;
                return this;
            }

            GitHubIssuesBuilder direction(String direction) {
                this.direction = direction;
                return this;
            }

            /**
             * Only list issues with given labels, comma separated list.
             * Empty string is ok and means that we are interested in all issues, regardless of the label.
             */
            public GitHubIssuesBuilder labels(String labels) {
                this.labels = labels;
                return this;
            }

            GitHubIssues browse() {
                return new GitHubIssues(repository, authToken, state, filter, labels, direction);
            }
        }
    }
}
