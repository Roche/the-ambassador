package com.roche.gitlab.api.project.repository

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class RepositoryFile(
    @JsonProperty("blob_id")
    val blobId: String,
    @JsonProperty("commit_id")
    val commitId: String,
    @JsonProperty("content")
    val content: String? = null,
    @JsonProperty("content_sha256")
    val contentSha256: String? = null,
    @JsonProperty("encoding")
    val encoding: String,
    @JsonProperty("file_name")
    val fileName: String,
    @JsonProperty("file_path")
    val filePath: String,
    @JsonProperty("last_commit_id")
    val lastCommitId: String,
    @JsonProperty("ref")
    val ref: String,
    @JsonProperty("size")
    val size: Long
) {

    fun getRawContent(): Optional<String> {
        return Optional.ofNullable(content)
            .map { Base64.getDecoder().decode(it) }
            .map { it.decodeToString() }
    }
}
