package org.jhotdraw8.graph;

public interface IntAttributedIndexedDirectedGraph extends IndexedDirectedGraph {
    /**
     * Returns the specified successor (next) arrow data of the specified vertex.
     *
     * @param vertex a vertex
     * @param index  index of next vertex
     * @return the arrow data
     */
    int getNextArrowAsInt(int vertex, int index);

    /**
     * Returns the data of the specified vertex.
     *
     * @param vertex a vertex
     * @return the vertex data
     */
    int getVertexAsInt(int vertex);
}
