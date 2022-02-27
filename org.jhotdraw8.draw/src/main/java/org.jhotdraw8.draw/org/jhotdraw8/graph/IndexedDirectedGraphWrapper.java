package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.NonNull;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Wraps a {@link IndexedDirectedGraph} into a {@link DirectedGraph} API.
 */
public class IndexedDirectedGraphWrapper implements DirectedGraph<Integer, Integer> {
    private final @NonNull IndexedDirectedGraph graph;

    public IndexedDirectedGraphWrapper(@NonNull IndexedDirectedGraph graph) {
        this.graph = graph;
    }

    @Override
    public int getArrowCount() {
        return graph.getArrowCount();
    }

    @Override
    public Integer getVertex(int index) {
        return index;
    }

    @Override
    public @NonNull Integer getNext(@NonNull Integer vertex, int index) {
        return graph.getNextAsInt(vertex, index);
    }

    @Override
    public @NonNull Integer getNextArrow(@NonNull Integer vertex, int index) {
        return graph.getNextArrowAsInt(vertex, index);
    }

    @Override
    public int getNextCount(@NonNull Integer vertex) {
        return graph.getNextCount(vertex);
    }

    @Override
    public @NonNull Set<Integer> getVertices() {
        LinkedHashSet<Integer> set = new LinkedHashSet<>(graph.getVertexCount());
        for (int i = 0, n = graph.getVertexCount(); i < n; i++) set.add(i);
        return set;
    }
}
