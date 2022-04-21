package com.roche.ambassador.lookups

import com.roche.ambassador.storage.project.ProjectEntityRepository
import com.roche.ambassador.storage.topics.Topic
import com.roche.ambassador.storage.topics.TopicRepository
import org.springframework.stereotype.Service

@Service
internal class TopicsService(
    private val projectEntityRepository: ProjectEntityRepository,
    topicRepository: TopicRepository
) : LookupService<Topic, TopicRepository>(topicRepository) {

    override fun retrieveLookups(): List<Topic> {
        return projectEntityRepository.findAllTopics()
            .map { Topic(name = it.getName(), count = it.getCount()) }
    }
}
