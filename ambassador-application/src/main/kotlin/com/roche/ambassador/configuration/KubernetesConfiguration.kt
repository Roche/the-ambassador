package com.roche.ambassador.configuration

import com.roche.ambassador.extensions.LoggerDelegate
import org.springframework.boot.autoconfigure.condition.ConditionalOnCloudPlatform
import org.springframework.boot.cloud.CloudPlatform
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct

@Configuration
@ConditionalOnCloudPlatform(CloudPlatform.KUBERNETES)
internal class KubernetesConfiguration {

    companion object {
        private val log by LoggerDelegate()
    }

    @PostConstruct
    fun onInit() {
        log.info("Discovered that Ambassador is deployed on Kubernetes. Applied Kubernetes-specific configuration")
    }

}