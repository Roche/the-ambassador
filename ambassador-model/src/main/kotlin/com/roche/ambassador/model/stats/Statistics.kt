package com.roche.ambassador.model.stats

data class Statistics(
    val forks: Int? = null,
    val stars: Int? = null,
    val commits: Long? = null,
    val jobArtifactsSize: Long? = null,
    val lfsObjectsSize: Long? = null,
    val packagesSize: Long? = null,
    val repositorySize: Long? = null,
    val storageSize: Long? = null,
    val wikiSize: Long? = null,
)