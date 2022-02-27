/*
 * @(#)IntDirectedGraph.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.AbstractIntEnumeratorSpliterator;
import org.jhotdraw8.collection.IntEnumeratorSpliterator;

/**
 * Provides indexed read access to a directed graph {@code G = (V, A) }.
 * <ul>
 * <li>{@code G} is a tuple {@code (V, A) }.</li>
 * <li>{@code V} is the set of vertices with elements {@code v_i ∈ V. i ∈ {0, ..., vertexCount - 1} }.</li>
 * <li>{@code A} is the set of ordered pairs with elements {@code  (v_i, v_j)_k ∈ A. i,j ∈ {0, ..., vertexCount - 1}. k ∈ {0, ..., arrowCount - 1} }.</li>
 * </ul>
 * <p>
 * The API of this class provides access to the following data:
 * <ul>
 * <li>The vertex count {@code vertexCount}.</li>
 * <li>The arrow count {@code arrowCount}.</li>
 * <li>The index {@code i} of each vertex {@code v_i ∈ V}.</li>
 * <li>The index {@code k} of each arrow {@code a_k ∈ A}.</li>
 * <li>The next count {@code nextCount_i} of the vertex with index {@code i}.</li>
 * <li>The index of the {@code k}-th next vertex of the vertex with index {@code i}, and with {@code k ∈ {0, ..., nextCount_i - 1}}.</li>
 * </ul>
 *
 * @author Werner Randelshofer
 */
public interface IndexedDirectedGraph {

    /**
     * Returns the number of arrows.
     *
     * @return arrow count
     */
    int getArrowCount();

    /**
     * Returns the k-th next vertex of v.
     *
     * @param vidx a vertex index
     * @param k    the index of the desired next vertex, {@code k ∈ {0, ..., getNextCount(v) -1 }}.
     * @return the index of the k-th next vertex of v.
     */
    int getNextAsInt(int vidx, int k);

    default int getNextArrowAsInt(int vertex, int index) {
        return 0;
    }

    default int getPrevArrowAsInt(int vertex, int index) {
        return 0;
    }

    /**
     * Returns the number of next vertices of v.
     *
     * @param vidx a vertex
     * @return the number of next vertices of v.
     */
    int getNextCount(int vidx);

    /**
     * Returns the number of vertices {@code V}.
     *
     * @return vertex count
     */
    int getVertexCount();

    /**
     * Returns the index of vertex b.
     *
     * @param vidx a vertex
     * @param uidx another vertex
     * @return index of vertex b. Returns a value {@literal < 0}
     * if b is not a next vertex of a.
     */
    default int findIndexOfNextAsInt(int vidx, int uidx) {
        for (int i = 0, n = getNextCount(vidx); i < n; i++) {
            if (uidx == getNextAsInt(vidx, i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns true if b is a next vertex of a.
     *
     * @param vidx a vertex
     * @param uidx another vertex
     * @return true if b is a next vertex of a.
     */
    default boolean isNext(int vidx, int uidx) {
        return findIndexOfNextAsInt(vidx, uidx) >= 0;
    }

    /**
     * Returns the direct successor vertices of the specified vertex.
     *
     * @param vidx a vertex index
     * @return a collection view on the direct successor vertices of vertex
     */
    default @NonNull IntEnumeratorSpliterator nextVerticesSpliterator(int vidx) {
        class MySpliterator extends AbstractIntEnumeratorSpliterator {
            private int index;
            private final int limit;
            private final int vidx;

            public MySpliterator(int vidx, int lo, int hi) {
                super(hi - lo, ORDERED | NONNULL | SIZED | SUBSIZED);
                limit = hi;
                index = lo;
                this.vidx = vidx;
            }

            @Override
            public boolean moveNext() {
                if (index < limit) {
                    current = getNextAsInt(vidx, index++);
                    return true;
                }
                return false;
            }

            public @Nullable MySpliterator trySplit() {
                int hi = limit, lo = index, mid = (lo + hi) >>> 1;
                return (lo >= mid) ? null : // divide range in half unless too small
                        new MySpliterator(vidx, lo, index = mid);
            }

        }
        return new MySpliterator(vidx, 0, getNextCount(vidx));
    }
}
