package com.roche.ambassador.indexing

import com.roche.ambassador.model.project.Project
import com.roche.ambassador.storage.indexing.Indexing

class SingleProjectIndexingFinishedEvent(val project: Project, data: Indexing) : IndexingFinishedEvent(data) {
}