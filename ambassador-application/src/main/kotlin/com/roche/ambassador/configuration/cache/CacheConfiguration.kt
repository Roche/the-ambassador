package com.roche.ambassador.configuration.cache

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.cache.transaction.TransactionAwareCacheManagerProxy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
@EnableCaching
internal class CacheConfiguration {

    @Bean
    fun cacheManager(): CacheManager {
        // TODO configure cache properties
        val caffeine = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(10))
            .weakKeys()
            .weakValues()
        val manager = CaffeineCacheManager()
        manager.setCaffeine(caffeine)
        return TransactionAwareCacheManagerProxy(manager)
    }
}
