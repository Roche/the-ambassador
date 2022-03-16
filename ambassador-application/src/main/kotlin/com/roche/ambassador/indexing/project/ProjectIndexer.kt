package com.roche.ambassador.indexing.project

import com.roche.ambassador.indexing.Indexer
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.project.ProjectFilter

interface ProjectIndexer : Indexer<Project, Long, ProjectFilter>
