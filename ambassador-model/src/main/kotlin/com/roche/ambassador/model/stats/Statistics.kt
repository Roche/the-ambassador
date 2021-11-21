package com.roche.ambassador.model.stats

data class Statistics(
    val forks: Int,
    val stars: Int,
    val commits: Long,
    val jobArtifactsSize: Long,
    val lfsObjectsSize: Long,
    val packagesSize: Long,
    val repositorySize: Long,
    val storageSize: Long,
    val wikiSize: Long,
) {
    companion object {
        fun no(): Statistics = Statistics(0, 0, 0, 0, 0, 0, 0, 0, 0)
    }
}
