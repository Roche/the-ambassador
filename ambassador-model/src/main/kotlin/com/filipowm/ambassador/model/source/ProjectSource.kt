package com.filipowm.ambassador.model.source

import com.filipowm.ambassador.OAuth2AuthenticationProvider
import com.filipowm.ambassador.model.Specification
import com.filipowm.ambassador.model.files.RawFile
import com.filipowm.ambassador.model.project.*
import com.filipowm.ambassador.model.stats.Timeline
import kotlinx.coroutines.flow.Flow
import java.util.*

interface ProjectSource<T> : Specification, IndexingCriteriaProvider<T>, ProjectDetailsResolver<T>, OAuth2AuthenticationProvider {

    suspend fun getById(id: String): Optional<Project>
    suspend fun flow(filter: ProjectFilter): Flow<T>
    suspend fun map(input: T): Project

    suspend fun readIssues(projectId: String): Issues
    suspend fun readContributors(projectId: String): List<Contributor>
    suspend fun readLanguages(projectId: String): Map<String, Float>
    suspend fun readCommits(projectId: String, ref: String): Timeline
    suspend fun readFile(projectId: String, path: String, ref: String): Optional<RawFile>
    suspend fun readReleases(projectId: String): Timeline
    suspend fun readProtectedBranches(projectId: String): List<ProtectedBranch>
    suspend fun readMembers(projectId: String): List<Member>

}