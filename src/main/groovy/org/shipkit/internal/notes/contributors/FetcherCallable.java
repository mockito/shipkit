package org.shipkit.internal.notes.contributors;

import org.shipkit.internal.notes.util.Function;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Basic function which applies values from a {@link List} of {@link V} to a given {@link Function}.
 */
class FetcherCallable<V, R> implements Callable<Set<R>> {

    private final List<V> list;
    private final Function<V, R> function;

    public FetcherCallable(List<V> list, Function<V, R> function) {
        this.list = list;
        this.function = function;
    }

    @Override
    public Set<R> call() throws Exception {
        Set<R> result = new HashSet<R>();
        for (V v : list) {
            result.add(function.apply(v));
        }
        return result;
    }
}
