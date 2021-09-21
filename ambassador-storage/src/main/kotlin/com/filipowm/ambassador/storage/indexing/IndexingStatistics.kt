package com.filipowm.ambassador.storage.indexing

import org.hibernate.annotations.Type
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class IndexingStatistics(
    @Column(name = "stats_total_projects_read")
    var totalProjectsRead: Long? = null,
    @Column(name = "stats_indexed_projects")
    var indexedProjects: Long? = null,
    @Column(name = "stats_excluded_errors")
    var excludedProjects: Long? = null,
    @Column(name = "stats_total_errors")
    var totalErrors: Long? = null,

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", name = "stats_exclusions")
    var exclusions: Map<String, Long>? = null,

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", name = "stats_errors")
    var errors: Map<String, Long>? = null,
)