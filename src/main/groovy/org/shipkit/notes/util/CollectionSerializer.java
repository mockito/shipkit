package org.shipkit.notes.util;

import org.json.simple.JsonObject;
import org.json.simple.Jsonable;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class CollectionSerializer<T extends Jsonable> {
    public String serialize(Collection<T> collection) {
        StringBuilder stringBuilder = new StringBuilder();
        final Iterator<T> iterator = collection.iterator();
        while (iterator.hasNext()) {
            stringBuilder.append(iterator.next().toJson());
            if (iterator.hasNext()) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }

    public Collection<T> deserialize(Collection<JsonObject> collection) {
        Collection<T> result = new LinkedList<T>();
        final Iterator<JsonObject> commitsIterator = collection.iterator();
        while (commitsIterator.hasNext()) {
            commitsIterator.next();
        }
        return result;
    }
}
