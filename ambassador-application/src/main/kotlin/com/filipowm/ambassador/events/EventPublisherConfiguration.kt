package com.filipowm.ambassador.events

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ApplicationEventMulticaster
import org.springframework.context.event.SimpleApplicationEventMulticaster
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.scheduling.support.TaskUtils

@Configuration
internal class EventPublisherConfiguration {

    @Bean("applicationEventMulticaster")
    fun applicationEventMulticaster(eventsProperties: EventsProperties): ApplicationEventMulticaster {
        val multicaster = SimpleApplicationEventMulticaster()
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = eventsProperties.async.corePoolSize
        executor.maxPoolSize = eventsProperties.async.maxPoolsSize
        executor.setThreadNamePrefix(eventsProperties.async.threadNamePrefix)
        executor.initialize()
        multicaster.setTaskExecutor(executor)
        multicaster.setErrorHandler(TaskUtils.LOG_AND_SUPPRESS_ERROR_HANDLER)
        return multicaster
    }
}