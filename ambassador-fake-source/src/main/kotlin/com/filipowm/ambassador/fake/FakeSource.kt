package com.filipowm.ambassador.fake

import com.filipowm.ambassador.GenerationSpec
import com.filipowm.ambassador.OAuth2ClientProperties
import com.filipowm.ambassador.UserDetailsProvider
import com.filipowm.ambassador.model.files.RawFile
import com.filipowm.ambassador.model.project.*
import com.filipowm.ambassador.model.source.ProjectSource
import com.filipowm.ambassador.model.stats.Statistics
import com.filipowm.ambassador.model.stats.Timeline
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.*

class FakeSource(val spec: GenerationSpec) : ProjectSource<FakeProject> {

    private val fakeDataProvider: FakeDataProvider = FakeDataProvider()

    override fun name(): String = "Fake"

    override fun getOAuth2ClientProperties(): OAuth2ClientProperties? = null

    override fun userDetailsProvider(attributes: Map<String, Any>): UserDetailsProvider? = null

    override fun getForkedProjectCriteria(): FakeForkedProjectCriteria = FakeForkedProjectCriteria

    override fun getInvalidProjectCriteria(): FakeInvalidProjectCriteria = FakeInvalidProjectCriteria

    override fun getPersonalProjectCriteria(): FakePersonalProjectCriteria = FakePersonalProjectCriteria

    override fun resolveName(project: FakeProject): String = project.name

    override fun resolveId(project: FakeProject): String = project.id.toString()

    override suspend fun getById(id: String): Optional<Project> {
        return Optional.of(id)
            .map { generate(it, ProjectFilter(visibility = Visibility.INTERNAL, false, null)) }
            .map { it.asProject() }
    }

    override suspend fun flow(filter: ProjectFilter): Flow<FakeProject> {
        return kotlinx.coroutines.flow.flow {
            for (i in 1..spec.count) {
                val project = generate(i.toString(), filter)
                emit(project)
            }
        }
    }

    private fun generate(id: String, filter: ProjectFilter): FakeProject {
        val name = fakeDataProvider.name()
        val tags = fakeDataProvider.tags()
        val stats = Statistics(
            fakeDataProvider.nextInt(min = 0, max = 100),
            fakeDataProvider.nextInt(min = 0, max = 500),
            fakeDataProvider.nextLong(min = 0, max = 10000),
            fakeDataProvider.nextLong(min = 0, max = 5000000),
            fakeDataProvider.nextLong(min = 0, max = 5000000),
            fakeDataProvider.nextLong(min = 0, max = 10000000),
            fakeDataProvider.nextLong(min = 0, max = 3000000),
            fakeDataProvider.nextLong(min = 0, max = 15000000),
            fakeDataProvider.nextLong(min = 0, max = 500000)
        )
        val createdDate = fakeDataProvider.date(from = LocalDate.now().minusYears(5))
        return FakeProject(
            id.toLong(),
            name,
            filter.visibility ?: fakeDataProvider.visibility(),
            fakeDataProvider.date(),
            fakeDataProvider.tags(),
            fakeDataProvider.projectUrl(name),
            fakeDataProvider.avatarUrl(),
            fakeDataProvider.description(name, tags),
            fakeDataProvider.defaultBranch(),
            stats,
            fakeDataProvider.date(from = createdDate)
        )
    }

    override suspend fun map(input: FakeProject): Project = input.asProject()

    override suspend fun readIssues(projectId: String): Issues {
        return Issues(0, 0, 0, 0, 0)
    }

    override suspend fun readContributors(projectId: String): List<Contributor> {
        return listOf()
    }

    override suspend fun readLanguages(projectId: String): Map<String, Float> {
        return mapOf()
    }

    override suspend fun readCommits(projectId: String, ref: String): Timeline {
        return Timeline()
    }

    override suspend fun readFile(projectId: String, path: String, ref: String): Optional<RawFile> {
        return Optional.empty()
    }

    override suspend fun readReleases(projectId: String): Timeline {
        return Timeline()
    }

    override suspend fun readProtectedBranches(projectId: String): List<ProtectedBranch> {
        return listOf()
    }

    override suspend fun readMembers(projectId: String): List<Member> {
        return listOf()
    }
}
