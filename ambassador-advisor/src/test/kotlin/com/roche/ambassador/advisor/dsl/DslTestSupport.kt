package com.roche.ambassador.advisor.dsl

import com.roche.ambassador.GenerationSpec
import com.roche.ambassador.advisor.AdvisorContext
import com.roche.ambassador.advisor.configuration.RulesProperties
import com.roche.ambassador.advisor.messages.AdviceMessage
import com.roche.ambassador.advisor.messages.AdviceMessageLookup
import com.roche.ambassador.advisor.model.IssueAdvice
import com.roche.ambassador.advisor.templates.TemplateEngine
import com.roche.ambassador.fake.FakeSource
import com.roche.ambassador.model.feature.FeatureReaders
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.storage.advisor.AdvisoryMessageEntity
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.assertj.core.api.ListAssert
import org.assertj.core.api.ObjectAssert
import java.util.function.Function

val source = FakeSource(GenerationSpec(100))

typealias LookupAnswerProvider = (Project, AdviceKey) -> AdviceMessage
typealias AdviceAssert = ObjectAssert<IssueAdvice>
typealias ProblemsAssert = ListAssert<AdviceMessage>

fun createProject(id: Long = source.createFakeId()): Project {
    return runBlocking {
        val project = source.getById(id.toString()).get()
        FeatureReaders.all().forEach { project.readFeature(it, source) }
        project
    }
}

fun Project.createIssueAdvice(): IssueAdvice = IssueAdvice(name)

fun createContext(
    project: Project,
    lookup: AdviceMessageLookup,
    templateEngine: TemplateEngine,
    givenAdvisoryMessages: Map<AdvisoryMessageEntity.Type, List<AdvisoryMessageEntity>> = mapOf(),
    configuration: RulesProperties
): AdvisorContext {
    return AdvisorContext(project, source, givenAdvisoryMessages, lookup, templateEngine, configuration)
}

val defaultLookupAnswerProvider: LookupAnswerProvider = { project, key ->
    AdviceMessage(key.key, key.params.joinToString { it.toString() }, "", "", AdviceMessage.AdviceSeverity.LOW, "")
}

fun testAdvise(lookupAnswerProvider: LookupAnswerProvider = defaultLookupAnswerProvider, builder: RulesBuilder<IssueAdvice>.() -> Unit): TestAdviceResponse {
    val project = createProject()
    val advice = project.createIssueAdvice()
    val lookup: AdviceMessageLookup = mockk()
    every { lookup.get(any()) } answers {
        val key = args[0] as AdviceKey
        lookupAnswerProvider(project, key)
    }
    val context = createContext(project, lookup, mockk(), mapOf(), RulesProperties())
    Dsl.advise(advice, context, builder)
    return TestAdviceResponse(advice, lookup, project, context)
}

fun testAdvise(builder: RulesBuilder<IssueAdvice>.() -> Unit): TestAdviceResponse = testAdvise(defaultLookupAnswerProvider, builder)

class TestAdviceResponse(
    val advice: IssueAdvice,
    val lookup: AdviceMessageLookup,
    val project: Project,
    val context: AdvisorContext
)

fun AdviceAssert.hasProjectName(projectName: String): AdviceAssert {
    extracting { it.projectName }.isEqualTo(projectName)
    return this
}

fun AdviceAssert.hasProblemsSize(size: Int): AdviceAssert {
    extracting { it.getProblems() }.asList().hasSize(size)
    return this
}

fun AdviceAssert.hasNoProblems(): AdviceAssert = hasProblemsSize(0)


fun x(response: TestAdviceResponse): ProblemsAssert {
    return Assertions.assertThat(response.advice.getProblems())
}

fun AdviceAssert.problems(): ProblemsAssert {
    return extracting { it.getProblems() }.asList() as ProblemsAssert
}

fun ProblemsAssert.hasNames(vararg names: String): ProblemsAssert {
    this.map(Function { it.name }).containsExactly(*names)
    return this
}

fun ProblemsAssert.hasDetails(vararg details: String): ProblemsAssert {
    this.map(Function { it.details }).containsExactly(*details)
    return this
}

fun ProblemsAssert.has(name: String, detail: String): ProblemsAssert {
    this.map(Function { it.name to it.details }).containsAnyOf(name to detail)
    return this
}

fun ProblemsAssert.has(vararg problems: Pair<String, String>): ProblemsAssert {
    this.map(Function { it.name to it.details }).containsExactly(*problems)
    return this
}

fun ProblemsAssert.has(vararg names: String): ProblemsAssert = hasNames(*names)

fun assertThat(response: TestAdviceResponse): AdviceAssert = Assertions.assertThat(response.advice)