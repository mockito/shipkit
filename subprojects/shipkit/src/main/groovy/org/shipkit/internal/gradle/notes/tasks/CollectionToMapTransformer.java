package org.shipkit.internal.gradle.notes.tasks;

import org.shipkit.internal.notes.util.Function;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CollectionToMapTransformer<A extends Collection<B>, B, K,V> {

    private final Function<B, Map.Entry<K,V>> transformFunction;

    public CollectionToMapTransformer(Function<B, Map.Entry<K,V>> transformFunction) {
        this.transformFunction = transformFunction;
    }

    public Map<K,V> transform(A collection) {
        Map<K, V> contributorMap = new HashMap<K, V>();
        for (B b : collection) {
            Map.Entry<K,V> entry = transformFunction.apply(b);
            contributorMap.put(entry.getKey(), entry.getValue());
        }
        return contributorMap;
    }
}
