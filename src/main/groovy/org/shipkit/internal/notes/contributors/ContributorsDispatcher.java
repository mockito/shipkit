package org.shipkit.internal.notes.contributors;


import org.json.simple.JsonObject;
import org.shipkit.internal.notes.model.ProjectContributor;
import org.shipkit.internal.notes.util.Function;
import org.shipkit.internal.notes.util.GitHubObjectFetcher;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ContributorsDispatcher {

    private static final int N_THREADS = 4;

    public Set<ProjectContributor> dispatch(List<JsonObject> page, String readOnlyAuthToken) {
        Set<ProjectContributor> result = new HashSet<ProjectContributor>();
        ExecutorService executor = Executors.newFixedThreadPool(N_THREADS);
        List<JsonObject> synchronizedPageList = Collections.synchronizedList(page);

        List<Future<Set<ProjectContributor>>> futures = new ArrayList<Future<Set<ProjectContributor>>>();
        if (page.size() > 0) {
            for (int i = 0; i < N_THREADS; i++) {
                GitHubObjectFetcher objectFetcher = new GitHubObjectFetcher(readOnlyAuthToken);
                Function<JsonObject, ProjectContributor> projectContributorFetcherFunction = new ProjectContributorFetcherFunction(objectFetcher);
                Callable<Set<ProjectContributor>> callable = new FetcherCallable<JsonObject, ProjectContributor>(synchronizedPageList, projectContributorFetcherFunction);

                futures.add(executor.submit(callable));
            }
        }

        for (Future<Set<ProjectContributor>> future: futures) {
            try {
                result.addAll(future.get());
            } catch (Exception e) {
                throw new RuntimeException("Error occurred while fetching contributors!", e);
            }
        }
        return result;
    }

}
