package com.roche.gitlab.api.project.repository

data class CommitStats(
    val additions: Int = 0,
    val deletions: Int = 0,
    val total: Int = additions + deletions
)
