package com.filipowm.ambassador.storage

import com.filipowm.ambassador.storage.config.JsonStorageConfiguration
import com.filipowm.ambassador.storage.utils.DataSourceWrapper
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(DataSourceWrapper::class, JsonStorageConfiguration::class)
class TestApplication