package com.filipowm.gitlab.api.project.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Statistics(
    @JsonProperty("commit_count")
    var commitCount: Long,
    @JsonProperty("job_artifacts_size")
    var jobArtifactsSize: Long,
    @JsonProperty("lfs_objects_size")
    var lfsObjectsSize: Long,
    @JsonProperty("packages_size")
    var packagesSize: Long,
    @JsonProperty("repository_size")
    var repositorySize: Long,
    @JsonProperty("snippets_size")
    var snippetsSize: Long,
    @JsonProperty("storage_size")
    var storageSize: Long,
    @JsonProperty("wiki_size")
    var wikiSize: Long
)