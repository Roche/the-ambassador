package com.filipowm.ambassador.gradle.utils

import org.flywaydb.core.Flyway
import org.jooq.tools.jdbc.SingleConnectionDataSource
import java.lang.IllegalStateException
import java.sql.Connection
import java.util.*

class DatabaseInit {

    companion object {
        @JvmStatic
        fun flyway(connection: Connection) {
            val ds = SingleConnectionDataSource(connection)
            val migrationsPath = ConfigHolder.get()
                .map { it.resourcesPath }
                .orElseThrow { IllegalStateException("Path to migrations should be defined") }

            val result = Flyway.configure()
                .dataSource(ds)
                .locations(migrationsPath)
                .placeholders(
                    mapOf(
                        "language" to "english"
                    )
                )
                .load()
                .migrate()

            if (result.migrationsExecuted == 0) {
                throw RuntimeException("No migration was executed")
            }
        }

        fun load() {
            Class.forName(DatabaseInit::class.qualifiedName)
        }
    }

    object ConfigHolder {

        private val holder = InheritableThreadLocal<InitConfig>()

        fun set(config: InitConfig) {
            holder.set(config)
        }

        fun get(): Optional<InitConfig> {
            return Optional.ofNullable(holder.get())
        }

        fun clear() {
            holder.remove()
        }

    }

    class InitConfig(val resourcesPath: String)
}