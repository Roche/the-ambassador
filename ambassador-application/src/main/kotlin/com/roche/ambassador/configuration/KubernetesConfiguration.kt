package com.roche.ambassador.configuration

import com.roche.ambassador.extensions.LoggerDelegate
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnCloudPlatform
import org.springframework.boot.cloud.CloudPlatform
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnCloudPlatform(CloudPlatform.KUBERNETES)
internal class KubernetesConfiguration : InitializingBean {

    companion object {
        private val log by LoggerDelegate()
    }

    override fun afterPropertiesSet() {
        log.info("Discovered that Ambassador is deployed on Kubernetes. Applied Kubernetes-specific configuration")
    }
}
