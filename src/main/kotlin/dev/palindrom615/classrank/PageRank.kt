package dev.palindrom615.classrank

import java.util.concurrent.Callable
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ExecutorService
import java.util.stream.Collectors

class PageRank(val graph: StochasticGraph<String>) {
    var classScoreVector: ConcurrentMap<String, Double>
    val personalizationWeightVector: ConcurrentMap<String, Double>
    val danglingWeightVector: ConcurrentMap<String, Double>

    init {
        val nodeNum = graph.size()
        val nodes = graph.nodes()
        classScoreVector = newOneVector(nodes, 1.0 / nodeNum)
        personalizationWeightVector = newOneVector(nodes, 1.0 / nodeNum)
        danglingWeightVector = newOneVector(nodes, 1.0 / nodeNum)
    }

    fun iterate(iter: Int, executorService: ExecutorService): Map<String, Double> {
        val danglingNodes = graph.danglingNodes()

        for (i in 0 until iter) {
            val classScoreVectorOld = classScoreVector
            classScoreVector = newOneVector(classScoreVectorOld.keys, 0.0)
            val danglesum = ALPHA * danglingNodes.mapNotNull { n -> classScoreVectorOld[n] }.sum()
            val jobs = graph.nodes().map { n ->
                Callable {
                    for (e in graph.edgesOf(n)) {
                        classScoreVector[e.to] =
                            classScoreVector[e.to]!! + ALPHA * classScoreVectorOld[n]!! * e.weight
                    }
                    classScoreVector[n] =
                        classScoreVector[n]!! + danglesum * danglingWeightVector[n]!! + (1.0 - ALPHA) * personalizationWeightVector[n]!!

                }
            }
            executorService.invokeAll(jobs)
        }
        return classScoreVector
    }

    companion object {
        const val ALPHA = 0.85

        private fun newOneVector(keys: Set<String>, v: Double): ConcurrentMap<String, Double> {
            return keys.stream().collect(Collectors.toConcurrentMap({ k -> k }, { k -> v }))
        }
    }
}