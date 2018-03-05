package org.shipkit.internal.util;

import java.util.*;

/**
 * Basic multi-map that contains multiple values per key
 */
public class MultiMap<K, V> {

    private final Map<K, Collection<V>> data = new LinkedHashMap<>();

    /**
     * If the key does not exist, null is returned
     */
    public Collection<V> get(K key) {
        return data.get(key);
    }

    public void put(K key, V value) {
        Collection<V> elements = get(key);
        if (elements == null) {
            elements = new LinkedHashSet<>();
        }
        elements.add(value);
        data.put(key, elements);
    }

    public Set<K> keySet() {
        return data.keySet();
    }

    public int size() {
        return data.size();
    }
}
