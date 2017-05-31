package org.shipkit.internal.notes.improvements;

import java.util.Collection;

/**
 * Utility to provide comma separated Strings.
 * When we move to Java8, this class can be removed.
 * This utility is implemented in 3rd party libraries but I don't want a dependency for the sake of this method
 */
class CommaSeparated {

    static String commaSeparated(Collection<String> collection) {
        if (collection.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (String s : collection) {
            sb.append(",").append(s);
        }
        return sb.substring(1);
    }
}
