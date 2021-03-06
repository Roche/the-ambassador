package com.roche.ambassador.document

import com.roche.ambassador.model.files.Documentation
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

data class TextAnalyzingService(
    val acronymDetector: AcronymDetector,
    val languageDetector: LanguageDetector,
    val readabilityTest: ReadabilityTest,
    val configuration: TextAnalyzerConfiguration
) {

    @ObsoleteCoroutinesApi
    private val dispatcher = newFixedThreadPoolContext(5, "text-analyzer")

    @SuppressWarnings("SwallowedException")
    private fun <U> CoroutineScope.withTimeoutOr(ctx: CoroutineContext, default: () -> U, into: () -> U): Deferred<U> {
        return try {
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
    suspend fun analyze(details: TextDetails): Documentation {
        val text = details.content
        if (text.isNullOrBlank()) {
            return Documentation.notExistent()
        }
        return withContext(coroutineContext) {
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
}
