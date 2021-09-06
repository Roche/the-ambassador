package com.filipowm.ambassador.model

data class Features(
    val pullRequests: Boolean?,
    val issues: Boolean?,
    val packages: Boolean?,
    val containerRegistry: Boolean?,
    val lfs: Boolean?,
    val cicd: Boolean?,
    val wiki: Boolean?,
    val snippets: Boolean?
)
