package com.roche.ambassador.fake

import com.roche.ambassador.GenerationSpec
import com.roche.ambassador.OAuth2ClientProperties
import com.roche.ambassador.UserDetailsProvider
import com.roche.ambassador.model.files.RawFile
import com.roche.ambassador.model.project.*
import com.roche.ambassador.model.source.ProjectSource
import com.roche.ambassador.model.stats.Statistics
import com.roche.ambassador.model.stats.Timeline
import com.roche.ambassador.model.stats.TimelineGenerator
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
            createdDate,
            fakeDataProvider.tags(),
            fakeDataProvider.projectUrl(),
            fakeDataProvider.avatarUrl(),
            fakeDataProvider.description(),
            fakeDataProvider.defaultBranch(),
            stats,
            fakeDataProvider.date(from = createdDate)
        )
    }

    override suspend fun map(input: FakeProject): Project = input.asProject()

    override suspend fun readIssues(projectId: String): Issues {
        val open = fakeDataProvider.nextInt()
        val closed = fakeDataProvider.nextInt()
        return Issues(open + closed, open, closed, fakeDataProvider.nextInt(max = closed), fakeDataProvider.nextInt(max = open))
    }

    override suspend fun readContributors(projectId: String): List<Contributor> = fakeDataProvider.generate(max = 50, generator = fakeDataProvider::contributor)

    override suspend fun readLanguages(projectId: String): Map<String, Float> {
        return fakeDataProvider.languages()
    }

    override suspend fun readCommits(projectId: String, ref: String): Timeline {
        val mean = fakeDataProvider.withBinaryChance(95,
                                                     { fakeDataProvider.nextDouble(1, 100) },
                                                     { 0.0 })!! // not active project
        return TimelineGenerator.withWeekAverage(mean, Calendar.getInstance().getActualMaximum(Calendar.WEEK_OF_YEAR))
    }

    override suspend fun readFile(projectId: String, path: String, ref: String): Optional<RawFile> {
        val value = fakeDataProvider.withBinaryChance(1,
                                                      { fakeDataProvider.createFile() },
                                                      { RawFile.notExistent() })
        return Optional.of(value!!)
    }

    override suspend fun readReleases(projectId: String): Timeline {
        val events = fakeDataProvider.withBinaryChance(30,
                                                       { fakeDataProvider.nextInt(1, 24) },
                                                       { 0 })!! // not active project
        return TimelineGenerator.withTotalEvents(events, startDate = LocalDate.now().minusYears(1))
    }

    override suspend fun readProtectedBranches(projectId: String): List<ProtectedBranch> {
        return (0..fakeDataProvider.nextInt(max = 3))
            .map { fakeDataProvider.branch() }
            .toSet()
            .map { ProtectedBranch(it, fakeDataProvider.bool(), fakeDataProvider.bool()) }
            .toList()
    }

    override suspend fun readMembers(projectId: String): List<Member> {
        return fakeDataProvider.generate(max = 20, generator = fakeDataProvider::member)
    }

    override suspend fun readPullRequests(projectId: String): Timeline {
        val mean = fakeDataProvider.withBinaryChance(95,
                                                     { fakeDataProvider.nextDouble(1, 15) },
                                                     { 0.0 })!! // not active project
        return TimelineGenerator.withWeekAverage(mean, Calendar.getInstance().getActualMaximum(Calendar.WEEK_OF_YEAR))
    }

    fun createFakeId(): Long = fakeDataProvider.nextLong(1, 30000)
}
