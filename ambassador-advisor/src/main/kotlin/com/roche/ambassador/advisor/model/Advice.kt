package com.roche.ambassador.advisor.model

class Advice<T>(
    val projectId: Long,
    val name: String,
    val title: String,
    val description: String,
    val labels: List<String>,
    val details: T?,
)
