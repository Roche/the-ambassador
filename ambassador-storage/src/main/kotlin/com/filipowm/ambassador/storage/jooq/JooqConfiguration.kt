package com.filipowm.ambassador.storage.jooq

import org.jooq.ExecuteContext
import org.jooq.impl.DataSourceConnectionProvider
import org.jooq.impl.DefaultConfiguration
import org.jooq.impl.DefaultDSLContext
import org.jooq.impl.DefaultExecuteListener
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator
import org.springframework.jdbc.support.SQLExceptionTranslator
import javax.sql.DataSource

@Configuration
class JooqConfiguration {

    private val log = LoggerFactory.getLogger(JooqConfiguration::class.java)

    @Bean
    open fun dsl(dataSource: DataSource): DefaultDSLContext {
        log.info("Setting up jooq")
        val connectionProvider = DataSourceConnectionProvider(TransactionAwareDataSourceProxy(dataSource))
        val jooqConfiguration = DefaultConfiguration()
        jooqConfiguration.set(connectionProvider)
        jooqConfiguration.set(ExceptionTranslator())
        return DefaultDSLContext(jooqConfiguration)
    }

    class ExceptionTranslator : DefaultExecuteListener() {

        override fun exception(context: ExecuteContext) {
            val dialect = context.configuration().dialect()
            val translator: SQLExceptionTranslator = SQLErrorCodeSQLExceptionTranslator(dialect.getName())
            context.exception(
                translator
                    .translate("Access database using Jooq", context.sql(), context.sqlException() ?: return)
            )
        }
    }
}
