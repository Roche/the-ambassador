package com.roche.ambassador.model.events

import com.roche.ambassador.model.project.Project

class ProjectIndexingFinishedEvent(project: Project) : Event<Project>(data = project)