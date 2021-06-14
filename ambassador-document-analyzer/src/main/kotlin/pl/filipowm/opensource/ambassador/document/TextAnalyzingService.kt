package pl.filipowm.opensource.ambassador.document

import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import pl.filipowm.opensource.ambassador.model.files.Documentation
import java.util.*
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

    private suspend fun analyzeSync(text: String): Documentation {
        val dispatcher = newFixedThreadPoolContext(5, "doc-analyzer-")
//        val language = withTimeoutOr(dispatcher, { Language.UNKNOWN }) {
//            log.info("Analyzing language...")
//            val l = languageDetector.detectLanguageOf(text.substring(0, 1000))
//            log.info("Language analyzed...")
//            l
//        }
//        val readability = withTimeoutOr(dispatcher, { ReadabilityScore(0.0f, ReadabilityScoreRange.BAD) }) {
//            log.info("Analyuzing readability")
//            readabilityTest.calculateReadabilityScoreOf(text)
//            log.info("Readability analyzed")
//        }
        return Documentation.notExistent()
//        val hash = withTimeoutOr(dispatcher, { Optional.of("") }) { Hash.sha256(text) }
//        return Documentation(language.name, text.length, hash.orElse(""), null, true)
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
            println("TIMEOUT!!!")
            throw e
        }
    }

    fun analyze(text: String?): Documentation {
        if (text.isNullOrBlank()) {
            return Documentation.notExistent()
        }
        val dispatcher = newFixedThreadPoolContext(5, "withc")

        return runBlocking {
            val l1 = withTimeoutOr(dispatcher, { Language.UNKNOWN }) {
                val l = languageDetector.detectLanguageOf(text)
                l
            }

            val r1 = withTimeoutOr(dispatcher, { }) {
                readabilityTest.calculateReadabilityScoreOf(text)
            }
            r1.await()
            val hash = withTimeoutOr(dispatcher, { Optional.of("") }) { Hash.sha256(text) }
            Documentation(l1.await().name, text.length, hash.await().orElse(""), null, true)
        }
    }

    private fun isAcronymOrAbbreviation(text: String) {
        TODO("acc?")
    }
}
