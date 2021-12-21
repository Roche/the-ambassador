package com.roche.ambassador.model.source

import com.roche.ambassador.OAuth2AuthenticationProvider
import com.roche.ambassador.model.Specification
import com.roche.ambassador.model.files.RawFile
import com.roche.ambassador.model.project.*
import com.roche.ambassador.model.stats.Timeline
import kotlinx.coroutines.flow.Flow
import java.util.*

interface ProjectSource : Specification, GroupSource, OAuth2AuthenticationProvider {

    suspend fun getById(id: String): Optional<Project>
    fun flow(filter: ProjectFilter): Flow<Project>

    suspend fun readIssues(projectId: String): Issues
    suspend fun readContributors(projectId: String): List<Contributor>
    suspend fun readLanguages(projectId: String): Map<String, Float>
    suspend fun readCommits(projectId: String, ref: String): Timeline
    suspend fun readFile(projectId: String, path: String, ref: String): Optional<RawFile>
    suspend fun readReleases(projectId: String): Timeline
    suspend fun readProtectedBranches(projectId: String): List<ProtectedBranch>
    suspend fun readMembers(projectId: String): List<Member>
    suspend fun readPullRequests(projectId: String): Timeline
    suspend fun readComments(projectId: String): Timeline

}
