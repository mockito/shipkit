package org.shipkit.internal.notes.contributors;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * Basic function which applies values from a {@link List} of {@link V} to a given {@link Function}.
 */
// TODO rename (maybe to ConsumingCallable) and move to util
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
        V v = getFromList();
        while (v != null) {
            result.add(function.apply(v));
            v = getFromList();
        }
        return result;
    }

    private V getFromList() {
        V v = null;
        synchronized (list) {
            int size = list.size();
            if (size > 0) {
                v = list.remove(size - 1);
            }
        }
        return v;
    }
}
