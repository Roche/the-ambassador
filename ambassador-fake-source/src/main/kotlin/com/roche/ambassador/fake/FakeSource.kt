package com.roche.ambassador.fake

import com.roche.ambassador.GenerationSpec
import com.roche.ambassador.OAuth2ClientProperties
import com.roche.ambassador.UserDetailsProvider
import com.roche.ambassador.model.Visibility
import com.roche.ambassador.model.files.RawFile
import com.roche.ambassador.model.group.Group
import com.roche.ambassador.model.group.GroupFilter
import com.roche.ambassador.model.project.*
import com.roche.ambassador.model.source.GroupSource
import com.roche.ambassador.model.source.IssuesManager
import com.roche.ambassador.model.source.ProjectSource
import com.roche.ambassador.model.stats.Statistics
import com.roche.ambassador.model.stats.Timeline
import com.roche.ambassador.model.stats.TimelineGenerator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.util.*

class FakeSource(val spec: GenerationSpec) : ProjectSource, GroupSource {

    private val fakeDataProvider: FakeDataProvider = FakeDataProvider()
    private val issuesManager = FakeIssuesManager()

    override fun name(): String = "Fake"

    override fun getOAuth2ClientProperties(): OAuth2ClientProperties? = null

    override fun userDetailsProvider(attributes: Map<String, Any>): UserDetailsProvider? = null
    override suspend fun ping() {
        // do nothing
    }

    override suspend fun getById(id: String): Optional<Project> {
        return Optional.of(id)
            .map { generate(it, ProjectFilter(visibility = Visibility.INTERNAL, false, null)) }
            .map { it.asProject() }
    }

    override fun flow(filter: ProjectFilter): Flow<Project> {
        return flow {
            for (i in 1..spec.count) {
                val project = generate(i.toString(), filter)
                emit(map(project))
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
        val group = generateGroup(fakeDataProvider.nextLong(1, 1500), GroupFilter())
        return FakeProject(
            id.toLong(),
            name,
            "${fakeDataProvider.parentName()}/$name",
            filter.visibility ?: fakeDataProvider.visibility(),
            createdDate,
            fakeDataProvider.tags(),
            fakeDataProvider.projectUrl(),
            fakeDataProvider.avatarUrl(),
            fakeDataProvider.description(),
            fakeDataProvider.defaultBranch(),
            stats,
            fakeDataProvider.date(from = createdDate),
            group = group
        )
    }

    private fun map(input: FakeProject): Project = input.asProject()

    override suspend fun readIssues(projectId: String): Issues {
        val open = fakeDataProvider.nextInt()
        val closed = fakeDataProvider.nextInt()
        return Issues(open + closed, open, closed, fakeDataProvider.nextInt(max = (closed+open)), fakeDataProvider.nextInt(max = closed), fakeDataProvider.nextInt(max = open))
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
        return createWeeklyTimelineByMeanWithEmptyChance(5)
    }

    override suspend fun readComments(projectId: String): Timeline {
        return createWeeklyTimelineByMeanWithEmptyChance(10)
    }

    override fun issues(): IssuesManager = issuesManager

    private fun createWeeklyTimelineByMeanWithEmptyChance(emptyChance: Int, min: Int = 1, max: Int = 15): Timeline {
        val mean = fakeDataProvider.withBinaryChance(100 - emptyChance,
                                                     { fakeDataProvider.nextDouble(min, max) },
                                                     { 0.0 })!! // not active project
        return TimelineGenerator.withWeekAverage(mean, Calendar.getInstance().getActualMaximum(Calendar.WEEK_OF_YEAR))
    }

    override fun flowGroups(filter: GroupFilter): Flow<Group> {
        return flow {
            for (i in 1..spec.count) {
                val group = generateGroup(i, filter)
                emit(group)
            }
        }
    }

    private fun generateGroup(id: Long, filter: GroupFilter): Group {
        return Group(
            id, fakeDataProvider.projectUrl(),
            fakeDataProvider.avatarUrl(), fakeDataProvider.description(),
            fakeDataProvider.name(), fakeDataProvider.parentName(),
            filter.visibility ?: fakeDataProvider.visibility(),
            fakeDataProvider.date(),
            fakeDataProvider.groupType(), null
        )
    }

    fun createFakeId(): Long = fakeDataProvider.nextLong(1, 30000)
}
