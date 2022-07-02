package dev.palindrom615.classrank

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.stream.Collectors

class StochasticGraph<N> {
    data class Edge<N>(val from: N, val to: N, val weight: Double)

    private val nodes: MutableSet<N> = ConcurrentHashMap.newKeySet()
    private val nodeEdgesMap: ConcurrentMap<N, MutableSet<Edge<N>>> = ConcurrentHashMap()

    fun nodes(): Set<N> {
        return nodes
    }

    fun addNode(from: N, tos: Collection<N>) {
        nodes.add(from)
        nodes.addAll(tos)
        nodeEdgesMap.put(from, ConcurrentHashMap.newKeySet())
        nodeEdgesMap[from]!!.addAll(tos.map { to -> Edge(from, to, 1.0 / tos.size) })
    }

    fun outDegreeOf(from: N): Int {
        return nodeEdgesMap.getOrDefault(from, emptySet()).size
    }

    fun danglingNodes(): Set<N> {
        return nodes.stream().filter { n -> outDegreeOf(n) == 0 }.collect(Collectors.toSet())
    }

    fun edgesOf(from: N): Set<Edge<N>> {
        return nodeEdgesMap.getOrDefault(from, emptySet())
    }

    fun size(): Int {
        return nodes.size
    }
}