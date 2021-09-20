package com.filipowm.ambassador.model

import com.filipowm.ambassador.model.files.RawFile
import com.filipowm.ambassador.model.project.Project
import com.filipowm.ambassador.model.source.ProjectSource
import java.util.*

interface FeatureReader<T: Feature<*>> {

    suspend fun read(project: Project, source: ProjectSource<*>): Optional<T>

    companion object {
        fun <T: Feature<*>>create(reader: suspend (Project, ProjectSource<*>) -> T?): FeatureReader<T> {
            return DefaultFeatureReader(reader)
        }
        fun <T: Feature<*>>createForProject(reader: suspend (Project) -> T?): FeatureReader<T> {
            return create { project, _ -> reader.invoke(project) }
        }
        fun <T: Feature<*>>createForFile(path: String, reader: suspend (RawFile) -> T?): FeatureReader<T> {
            return create { project, source ->
                var rawFile: RawFile = RawFile.notExistent()
                if (project.defaultBranch != null) {
                    rawFile = source.readFile(project.id.toString(), path, project.defaultBranch)
                        .orElseGet { RawFile.notExistent() }
                }
                reader.invoke(rawFile)
            }
        }
        fun <T: Feature<*>>createForFile(pathsProvider: (Project) -> Set<String>, reader: suspend (RawFile) -> T?): FeatureReader<T> {
            return create { project, source ->
                var rawFile = Optional.empty<RawFile>()
                if (project.defaultBranch != null) {
                    for (path in pathsProvider.invoke(project)) {
                        rawFile = source.readFile(project.id.toString(), path, project.defaultBranch)
                        if (rawFile.isPresent) {
                            break
                        }
                    }
                }
                reader.invoke(rawFile.orElseGet { RawFile.notExistent() })
            }
        }
    }

    private class DefaultFeatureReader<T: Feature<*>>(private val reader: suspend (Project, ProjectSource<*>) -> T?) : FeatureReader<T> {
        override suspend fun read(project: Project, source: ProjectSource<*>): Optional<T> = Optional.ofNullable(reader.invoke(project, source))
    }
}