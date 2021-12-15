package com.roche.ambassador.advisor.model

class Advice<T>(
    val projectId: Long,
    val name: String,
    val title: String,
    val description: String,
    val details: T?,
)
