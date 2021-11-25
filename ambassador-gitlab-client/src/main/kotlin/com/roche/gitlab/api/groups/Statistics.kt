package com.roche.gitlab.api.groups


import com.fasterxml.jackson.annotation.JsonProperty

data class Statistics(
    @JsonProperty("job_artifacts_size")
    val jobArtifactsSize: Long? = null,
    @JsonProperty("lfs_objects_size")
    val lfsObjectsSize: Long? = null,
    @JsonProperty("packages_size")
    val packagesSize: Long? = null,
    @JsonProperty("pipeline_artifacts_size")
    val pipelineArtifactsSize: Long? = null,
    @JsonProperty("repository_size")
    val repositorySize: Long? = null,
    @JsonProperty("snippets_size")
    val snippetsSize: Long? = null,
    @JsonProperty("storage_size")
    val storageSize: Long? = null,
    @JsonProperty("uploads_size")
    val uploadsSize: Long? = null,
    @JsonProperty("wiki_size")
    val wikiSize: Long? = null
)