package com.roche.ambassador.model.source

import com.roche.ambassador.Identifiable

data class Issue(
    private var id: Long?,
    val projectId: Long,
    val title: String,
    val description: String,
    val labels: List<String>,
    val status: Status,
) : Identifiable<Long> {

    enum class Status {
        OPEN,
        CLOSED,
        REOPENED,
        DELETED
    }

    override fun getId(): Long? = id

    override fun setId(id: Long?) {
        this.id = id
    }

    fun withStatus(status: Status): Issue {
        return Issue(id, projectId, title, description, labels, status)
    }
}
