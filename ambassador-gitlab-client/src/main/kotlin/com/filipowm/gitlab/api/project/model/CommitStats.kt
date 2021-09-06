package com.filipowm.gitlab.api.project.model

data class CommitStats(
    val additions: Int = 0,
    val deletions: Int = 0,
    val total: Int = additions + deletions
)
