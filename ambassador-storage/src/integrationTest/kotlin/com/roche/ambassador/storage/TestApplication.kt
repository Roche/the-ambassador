package com.roche.ambassador.storage

import com.roche.ambassador.storage.config.JsonStorageConfiguration
import com.roche.ambassador.storage.utils.DataSourceWrapper
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(DataSourceWrapper::class, JsonStorageConfiguration::class)
class TestApplication