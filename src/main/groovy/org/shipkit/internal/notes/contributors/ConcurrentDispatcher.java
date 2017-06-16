package org.shipkit.internal.notes.contributors;

import org.shipkit.internal.notes.util.Function;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ConcurrentDispatcher {

    private static final int N_THREADS = 4;

    public <R,T> Set<R> dispatch(Function<T, R> function, List<T> page) {
        Set<R> result = new HashSet<R>();
        ExecutorService executor = Executors.newFixedThreadPool(N_THREADS);
        List<T> synchronizedPageList = Collections.synchronizedList(page);

        List<Future<Set<R>>> futures = new ArrayList<Future<Set<R>>>();
        if (page.size() > 0) {
            for (int i = 0; i < N_THREADS; i++) {
                Callable<Set<R>> callable = new FetcherCallable<T, R>(synchronizedPageList, function);

                futures.add(executor.submit(callable));
            }
        }

        for (Future<Set<R>> future: futures) {
            try {
                result.addAll(future.get());
            } catch (Exception e) {
                throw new RuntimeException("Error occurred while fetching contributors!", e);
            }
        }
        return result;
    }

}
