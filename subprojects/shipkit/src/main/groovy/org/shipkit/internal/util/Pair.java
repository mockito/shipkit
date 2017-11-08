package org.shipkit.internal.util;

import static org.shipkit.internal.util.ArgumentValidation.notNull;

public class Pair<L, R> {
    private final L left;
    private final R right;

    private Pair(L left, R right) {
        notNull(left, "left", right, "right");
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }

    public static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<L, R>(left, right);
    }

    @Override
    public String toString() {
        return "Pair{left=" + left + ", right=" + right + '}';
    }
}
