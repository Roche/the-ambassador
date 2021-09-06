package pl.filipowm.opensource.ambassador.document

import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import pl.filipowm.opensource.ambassador.model.files.Documentation
import kotlin.coroutines.CoroutineContext

data class TextAnalyzingService(
    val acronymDetector: AcronymDetector,
    val languageDetector: LanguageDetector,
    val readabilityTest: ReadabilityTest,
    val configuration: TextAnalyzerConfiguration
) {

    companion object {
        private val log = LoggerFactory.getLogger(TextAnalyzingService::class.java)
    }

    private fun <U> CoroutineScope.withTimeoutOr(ctx: CoroutineContext, default: () -> U, into: () -> U): Deferred<U> {
        return try {
//            withTimeout(configuration.timeout) {
            async(ctx) {
                withTimeout(1) {
                    into()
                }
            }
//            }
        } catch (e: TimeoutCancellationException) {
            return async(ctx) {
                default.invoke()
            }
        }
    }

    @kotlinx.coroutines.ObsoleteCoroutinesApi
    fun analyze(details: TextDetails): Documentation {
        val text = details.content
        if (text.isNullOrBlank()) {
            return Documentation.notExistent()
        }
        val dispatcher = newFixedThreadPoolContext(5, "withc")

        return runBlocking {
            val language = withTimeoutOr(dispatcher, { Language.UNKNOWN }) {
                languageDetector.detectLanguageOf(text)
            }

//            val r1 = withTimeoutOr(dispatcher, { }) {
//                readabilityTest.calculateReadabilityScoreOf(text)
//            }
            Documentation(
                language.await().name,
                details.hash,
                null,
                true,
                details.size,
                details.path
            )
        }
    }

    private fun isAcronymOrAbbreviation(text: String) {
        TODO("acc? $text")
    }
}
